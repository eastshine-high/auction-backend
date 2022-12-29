# 로그인(JWT 발행하기)

사용자가 접근이 제한되어 있는 리소스에 접근할 때는, 로그인(자격 증명)을 통해 자신이 누구인지를 인증하고 인가를 받아 리소스에 접근합니다.

하지만 웹의 기반인 HTTP 프로토콜은 [무상태성(Stateless)](https://github.com/eastshine-high/til/blob/main/web/http/rest/stateless.md) 과 비연결성(Connectionless)이란 특성을 가지고 있어, 모든 요청마다 인증이 필요합니다. 모든 요청마다 로그인을 통해 인증할 수는 없으므로, 로그인으로 인증한 다음에는 쿠키, 세션, JWT 등의 수단으로 인증을 대신합니다.

이 프로젝트에서는 인증을 대신하는 수단으로 JWT를 사용하였습니다. JWT에 대한 설명과 이를 Java 코드로 표현하는 방법은 [Github](https://github.com/eastshine-high/til/blob/main/web/jwt.md) 을 통해 정리하였습니다.


### 로그인(JWT 발행) 구현

먼저 JWT를 지원하는 의존성을 추가합니다.

```groovy
implementation 'io.jsonwebtoken:jjwt-api:0.11.2'
runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.11.2'
runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.11.2'
```

[JwtUtil](https://github.com/eastshine-high/auction-backend/blob/main/app/src/main/java/com/eastshine/auction/common/utils/JwtUtil.java) - 추가한 JWT 의존성을 사용하여 JWT 토큰을 발행하고 파싱할 수 있는 빈(Bean)을 만듭니다.

```java
@Component
public class JwtUtil {
    public static final String KEY_OF_USER_ID = "userId";

    private final Key signKey;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        signKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String encode(Long userId) {
        return Jwts.builder()
                .claim(KEY_OF_USER_ID, userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (60 * 1000) * 100)) // 토큰 유효 일시(+100분)
                .signWith(signKey)
                .compact();
    }

    public Claims decode(String token) {
        if (token == null || token.isBlank()) {
            throw new AuthenticationException(ErrorCode.INVALID_TOKEN);
        }

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (SignatureException | MalformedJwtException e) {
            throw new AuthenticationException(ErrorCode.INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new AuthenticationException(ErrorCode.EXPIRED_TOKEN);
        }
    }
}
```

- `claim(KEY_OF_USER_ID, userId)` : [JWT 문서](https://jwt.io/introduction) 에서는 토큰의 요소들을 모든 사람이 읽을 수 있음에 유의해야 한다고 설명합니다. 실제로 [JWT debugger](https://jwt.io/#debugger-io) 등을 이용하여 토큰을 확인할 수 있으므로 사용자의 식별자 이외의 정보를 페이로드에 담지 않았습니다(사용자의 식별자 또한 토큰화한다면 더 좋습니다).
- `setExpiration(new Date(System.currentTimeMillis() + (60 * 1000) * 100))` : 토큰은 보안 이슈(위.변조 등)가 발생하지 않도록 필요 이상으로 유지하지 않습니다([JWT의 공식 문서 권장](https://jwt.io/introduction)). 따라서 토큰의 유효 시간은 100분으로 설정하였습니다.

[AuthenticationService](https://github.com/eastshine-high/auction-backend/blob/main/app/src/main/java/com/eastshine/auction/user/application/AuthenticationService.java) - `JwtUtil` 의존성을 사용하여 자격 증명(email, password)을 통해 인증한 사용자에게 JWT를 발행합니다.

```java
package com.eastshine.auction.user.application;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserInfoRedisRepository userInfoRedisRepository;

    @Transactional(readOnly = true)
    public String login(String email, String password){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException(ErrorCode.USER_LOGIN_FAIL));

        if (!user.authenticate(password, passwordEncoder)) {
            throw new AuthenticationException(ErrorCode.USER_LOGIN_FAIL);
        }

        userInfoRedisRepository.save(user.toUserInfo());
        return jwtUtil.encode(user.getId());
    }
}
```

- `jwtUtil.encode(user.getId())` : 사용자가 인증에 성공한 경우, 주입 받은 `JwtUtil` 의존성을 이용하여 토큰을 발급합니다.
- `userInfoRedisRepository.save(user.toUserInfo())` : 토큰을 발급 사용자가 이를 이용해 다른 리소스에 접근할 때는, 식별 정보 외에 권한 정보 등이 필요합니다. 하지만 토큰에는 사용자 식별 정보만 담겨있으므로 데이터베이스에 접근하여 사용자 정보를 조회해야 합니다. 이러한 조회는 매 요청마다 발생하므로 매우 자주 발생합니다. 따라서 관계형 데이터베이스보다는 인메모리 데이터베이스를 활용하는 것이 서비스 성능에 유리합니다.

[SessionController](https://github.com/eastshine-high/auction-backend/blob/main/app/src/main/java/com/eastshine/auction/user/web/SessionController.java) - 클라이언트의 인증 요청에 응답합니다.

```java
package com.eastshine.auction.user.web;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/session")
public class SessionController {
    public static final String TOKEN_TYPE_BEARER = "Bearer";

    private final AuthenticationService authenticationService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public SessionDto.Response login(@RequestBody SessionDto.Request sessionRequestDto) {
        String email = sessionRequestDto.getEmail();
        String password = sessionRequestDto.getPassword();

        String accessToken = authenticationService.login(email, password);

        return SessionDto.Response.builder()
                .tokenType(TOKEN_TYPE_BEARER)
                .accessToken(accessToken)
                .build();
    }
}
```

이제 사용자는 HTTP 요청을 통해 발급 받은 JWT 토큰을 이용하여 접근이 제한되어 있는 리소스에 접근할 수 있습니다. 이후의 과정은 [API의 보안(Security)](https://github.com/eastshine-high/auction-backend/blob/main/readme/security.md) 에서 설명하겠습니다.
