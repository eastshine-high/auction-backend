# Auction backend

## 프로젝트 개요

Auction Backend는 쇼핑몰 REST API를 설계, 구현하고 이 과정에서 고민했던 것들에 대해 블로그 형식으로 기록한 프로젝트입니다.

## 목차

- 사용 기술
- 프로젝트 문서
- 프로젝트 ERD
- 기능 설명 및 설계, 구현 과정에서 배운 내용 정리
    - 공통 기능
        - [REST API의 예외(Exception) 처리](#exception)
        - [API의 보안(Security)](#security)
        - [Auditing](#auditing)
    - 사용자
        - [인증(JWT)](#jwt)
        - [외래키와 복합키 사용에 대하여](#constraints)
    - 상품
        - [재고 관리(동시성 이슈)](#stock)
        - [단일 책임 원칙과 URI 설계](#single-responsibility)
        - [Main-Sub 구조 엔터티 VS 계층(재귀) 구조 엔터티](#entity-design)
        - [상품 검색](#searching-product)
    - 주문
        - [주문 프로세스](#order-process)        
- 코드 개선하기
    - [관심사의 분리](#separation-of-concern)
    - [테스트 코드 작성을 통한 올바른 책임의 이해](#test-responsibility)

## 사용 기술

- Java 11, Gradle
- JPA, QueryDsl, Junit5, JWT, Json Merge Patch
- Spring Boot, Spring Data JPA, Spring REST Docs, Spring Security
- MariaDB 10, Redis, Kafka
- Devops : AWS EC2, Github Action, Nginx, Docker
- Design : HTTP(REST) API, 도메인 주도 설계

## 프로젝트 문서

- [API 문서(AWS 배포)](http://3.36.136.227/docs/index.html)

- [API 유스 케이스](https://eastshine.notion.site/5802417b375e474380a1a092e07e79fe?v=65b6e4f02626434597726a247cb3bf2e)

## 프로젝트 ERD

<details>
   <summary> 본문 확인 (Click)</summary>
<br />

![](http://dl.dropbox.com/s/xxxuc4de4ryj3mm/auction-erd.svg)

</details>

## 기능 설명 및 설계, 구현 과정에서 배운 내용 정리

## 공통 기능

### REST API의 예외(Exception) 처리 <a name = "exception"></a>

<details>
   <summary> 본문 확인 (Click)</summary>
<br />

### REST API의 예외(Exception) 처리

(1) 오류는 일관성 있게 표현합니다.

[REST API 디자인 규칙(마크 마세 저)](https://digital.kyobobook.co.kr/digital/ebook/ebookDetail.ink?selectedLargeCategory=001&barcode=480D150507640&orderClick=LAG&Kc=) 에서는 “오류는 일관성 있게 표현하여 응답”하는 것을 권합니다.

```json
{
    "errorCode" : "PRODUCT_NOT_FOUND",
    "message" : "상품을 찾을 수 없습니다."
}
```

따라서 아래와 같은 형식으로 오류 메세지를 정의합니다.

```java
public class ErrorResponse {
    private String errorCode;
    private String message;
}
```

`errorCode` 는 내부적으로 Enum을 통해 관리하며 `message` 를 매핑합니다.

```java
public enum ErrorCode {
    PRODUCT_NOT_FOUND("상품을 찾을 수 없습니다."),
    PRODUCT_UNACCESSABLE("상품에 대한 접근 권한이 없습니다.");

    private final String errorMsg;

    public String getErrorMsg(Object... arg) {
        return String.format(errorMsg, arg);
    }
}
```

예외는 `ErrorCode` 를 기반으로 처리하기 때문에 `RuntimeException` 을 확장한 기반 클래스를 만듭니다.

```java
@Getter
public class BaseException extends RuntimeException {
    private ErrorCode errorCode;

    public BaseException() {
    }

    public BaseException(ErrorCode errorCode) {
        super(errorCode.getErrorMsg());
        this.errorCode = errorCode;
    }
}
```

그리고 기반 클래스인 `BaseException` 을 확장하여 실제 비즈니스 로직에서 표현할 예외 클래스들을 만듭니다.

![http://dl.dropbox.com/s/g3rwsw09kf8l2rs/exception%20hierarchy.png](http://dl.dropbox.com/s/g3rwsw09kf8l2rs/exception%20hierarchy.png)

(2) HTTP 응답 상태 코드

REST API는 HTTP 응답 메시지의 Status-Line을 사용하여 클라이언트가 요청한 결과를 알려줍니다. 이 때, 오류의 응답 상태 코드는 ‘4xx’ 또는 ‘5xx’ 중 하나여야 합니다.

위의 다이어그램에서 `BaseException` 상속한 클래스들은 HTTP 응답 상태 코드의 표현이기도 합니다. 예를 들어 `EntityNotFoundException`는 아래와 같이 404 상태 코드를 응답합니다.

```java
@Slf4j
@ControllerAdvice
public class ControllerErrorAdvice {

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = EntityNotFoundException.class)
    public ErrorResponse onEntityNotFoundException(EntityNotFoundException e) {
        String eventId = MDC.get(CommonHttpRequestInterceptor.HEADER_REQUEST_UUID_KEY);
        log.error("[EntityNotFoundException] eventId = {}, cause = {}, errorMsg = {}", eventId, NestedExceptionUtils.getMostSpecificCause(e), NestedExceptionUtils.getMostSpecificCause(e).getMessage());
        return ErrorResponse.of(e.getMessage(), e.getErrorCode().name());
    }
}
```

- 스프링은 API 예외 처리 문제를 해결하기 위해 몇 가지 어노테이션을 지원합니다.
    - `@ControllerAdvice` : 모든 예외를 한 곳에서 처리하기 위해 선언합니다.
    - `@ExceptionHandler` : 처리하고 싶은 예외를 지정합니다.
- 초기에 정의한 `ErrorResponse` 를 사용하여 일관성 있는 표현으로 오류를 응답합니다.

이제 예외 처리를 위한 준비를 마쳤으니 비즈니스 로직에서 예외 처리를 합니다.

```java
@RequiredArgsConstructor
@Service
public class ProductService {
	private final ProductRepository productRepository;

	public Product findProduct(Long id) {
	  return productRepository.findById(id)
	          .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
	}
}
```

- `new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND)`
    - 식별자의 엔터티를 찾지 못했을 경우, 이를 표현하는 `EntityNotFoundException` 을 던집니다.
    - `ErrorCode.PRODUCT_NOT_FOUND` 를 통해 예외 상황을 좀 더 자세히 설명합니다. 같은 상황일 경우, 코드를 재사용함으로서 메세지를 통일할 수 있습니다.

</details>

### API의 보안(Security) <a name = "security"></a>

<details>
   <summary> 본문 확인 (Click)</summary>
<br />

API는 일부 사용자만 접근을 허용해야할 때가 있습니다. 이러한 보안을 위해서는 인증(당신은 누구입니까)과 인가(당신은 무엇을 할 수 있습니까) 과정이 필요합니다. Spring Security는 서블릿 애플리케이션에서의 인증(Authentication) 및 인가(Authentication) 처리를 지원하므로 API의 보안 처리는 Spring Security를 이용합니다.

먼저 사용자 도메인에서는 [로그인을 통한 인증으로 JWT를 발급](https://github.com/eastshine-high/auction-backend#jwt) 하였습니다. API 보안에서는 Spring Security를 이용하여 발급한 JWT를 인증한 뒤, 인가 처리를 합니다.

예를 들어 아래는 상품 정보의 수정을 요청하는 HTTP 요청 메세지입니다. HTTP의 `Authorization` 헤더에 발급받은 토큰을 넣어 요청합니다.

```java
PATCH /seller-api/products/1 HTTP/1.1
Content-Type: application/json
Authorization: Bearer eyJ1c2VySW5mbyI6eyJpZCI6MSwiZW1haWwiOiJ0ZXN0QGdtYWlsLmNvbSIsIm5pY2tuYW1lIjoibmlja25hbWUiLCJyb2xlcyI6WyJVU0VSIiwiU0VMTEVSIl19fQ
Content-Length: 160
Host: 3.36.136.227

{
  "name" : "modify name",
  "price" : 99999,
  "stockQuantity" : 30,
}
```

**Spring Security Architecture**

Spring MVC에 위의 요청이 들어오면, 아래와 같은 흐름을 통해 컨트롤러에 전달됩니다.

```
HTTP 요청 -> WAS -> (서블릿)필터 -> 서블릿(dispatcher) -> 스프링 인터셉터 -> 컨트롤러
```

Spring Security는 위 흐름 중에서 서블릿 필터를 기반으로 동작합니다([Spring Security의 구조](https://github.com/eastshine-high/til/blob/main/spring/spring-security/architecture.md) 에 대한 자세한 설명은 Github을 통해 정리하였습니다).

**인증(Authentication)**

스프링 시큐리티를 이용해 JWT를 인증하는 방법은 다양합니다. 예를 들어 [JwtAuthenticationProvider](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/oauth2/server/resource/authentication/JwtAuthenticationProvider.html) 혹은 [BearerTokenAuthenticationFilter](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/oauth2/server/resource/web/BearerTokenAuthenticationFilter.html) 를 이용해 인증할 수 있습니다.

여기서는 스프링 시큐리티의 필터를 직접 구현하였습니다.

```java
package com.eastshine.auction.common.filters;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
		private final AuthenticationService authenticationService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                   AuthenticationService authenticationService) {
        super(authenticationManager);
        this.authenticationService = authenticationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws IOException, ServletException {
        String authorization = request.getHeader("Authorization");

        if(Objects.nonNull(authorization)){
						String accessToken =  authorization.substring("Bearer ".length());
            Long userId = authenticationService.parseToken(accessToken);
            UserInfo userInfo = authenticationService.findUserInfo(userId);
            Authentication authentication = new UserAuthentication(userInfo);

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }
}
```

- 시큐리티 필터의 모든 기능을 구현하기는 어려움으로,  `BasicAuthenticationFilter` 필터를 상속하여 필요한 메소드를 오버라이드합니다. `BasicAuthenticationFilter` 는 `OncePerRequestFilter` 를 상속한 클래스입니다.
- `authenticationService.parseToken` : JWT을 인증 및 파싱합니다. JWT에 대해서는 [로그인 인증](https://github.com/eastshine-high/auction-backend#jwt) 에서 설명합니다. 여기서는 관련 내용의 링크만 참조하겠습니다.
    - [JWT 정리 및 활용](https://github.com/eastshine-high/til/blob/main/web/jwt.md)
    - [AuthenticationService](https://github.com/eastshine-high/auction-backend/blob/main/app/src/main/java/com/eastshine/auction/user/application/AuthenticationService.java)
- `SecurityContextHolder.getContext` : JWT가 인증되었다면, Spring Security를 통해 이를 표현합니다. 가장 간단한 방법은 SecurityContextHolder 에 누가 인증되었는 지를 직접 설정하는 것입니다(스프링 시큐리티의 다른 필터들과 통합하여 사용하지 않을 경우, AuthenticationManager 를 사용하지 않고 SecurityContextHolder 를 직접 사용하여 인증할 수 있습니다).
- `authenticationService.findUserInfo` : **로그인 인증** 에서는 보안 상의 이유로 사용자의 식별 정보만을 JWT 페이로드에 담았었습니다. 따라서 인증 객체(Authentication)를 생성할 때 필요한 사용자 권한 등의 추가 정보를 데이터베이스(Redis)에서 조회합니다.

![https://docs.spring.io/spring-security/reference/_images/servlet/authentication/architecture/securitycontextholder.png](https://docs.spring.io/spring-security/reference/_images/servlet/authentication/architecture/securitycontextholder.png)

- `new UserAuthentication(userInfo)` : SecurityContextHolder는 위의 그림과 같이 내부에 현재 인증된 사용자를 표현하는 Authentication 인터페이스를 포함합니다. 따라서 이를 구현한 구현체를 설정해야 합니다. 아래는 이를 구현한 코드입니다.

```java
public class UserAuthentication extends AbstractAuthenticationToken {
    private final UserInfo userInfo;

    public UserAuthentication(UserInfo userInfo) {
        super(authorities(userInfo));
        this.userInfo = userInfo;
    }

    private static List<GrantedAuthority> authorities(UserInfo userInfo) {
        if(Objects.isNull(userInfo.getRoles())){
            return new ArrayList<>();
        }

        return userInfo.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return userInfo;
    }
}
```

- `AbstractAuthenticationToken` : Authentication 인터페이스를 구현한 기본 클래스입니다. Authentication를 처음부터 구현하기는 어려우므로 AbstractAuthenticationToken을 상속하여 구현하였습니다.

이제 구현한 필터를 HTTP 요청에 적용하기 위해 Spring Security의 구성에 추가합니다.

```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

		@Autowired
    AuthenticationService authenticationService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        Filter authenticationFilter = new JwtAuthenticationFilter(authenticationManager(), authenticationService);
        Filter authenticationErrorFilter = new JwtAuthenticationErrorFilter();

        http
                .csrf().disable()
                .headers()
                    .frameOptions().disable() // H2 데이터베이스 콘솔을 위한 설정.
                .and()
                .addFilter(authenticationFilter)
                .addFilterBefore(authenticationErrorFilter, JwtAuthenticationFilter.class);
    }
}
```

- `WebSecurityConfigurerAdapter` : `WebSecurityConfigurer` 인스턴스를 생성할 때, 편리한 기본 클래스를 제공합니다. 메서드를 재정의하여 커스텀할 수 있습니다.
- `HttpSecurity` : 네임스페이스 구성에서 Spring Security의 XML `<http>` 엘러먼트와 유사합니다. 특정 `http` 요청에 대해 웹 기반 보안을 구성할 수 있습니다. 기본적으로 모든 요청에 적용되지만 `requestMatcher(RequestMatcher)` 또는 다른 유사한 방법을 사용하여 제한할 수 있습니다.
- `csrf().disable()` : 현재 서버는 REST API로만 사용하므로 `csrf` 를 사용하지 않았습니다.
- `@EnableGlobalMethodSecurity` : 아래 인가 처리에서 메소드 시큐리티를 활성화하기 위해 사용합니다.

**인가(Authorization)**

인가 처리는 [메소드 시큐리티](https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html) 를 이용하여 처리하였습니다. 보호가 필요한 API에 어노테이션을 추가하여 리소스를 보호합니다.

```java
@PostMapping
@PreAuthorize("hasAuthority('SELLER')")
public ResponseEntity registerProduct(@RequestBody @Validated ProductDto productDto) {
    Product registeredProduct = sellerProductService.registerProduct(productDto);
    return ResponseEntity.created(URI.create("/api/products/" + registeredProduct.getId())).build();
}
```

- `@PreAuthorize("hasAuthority('SELLER')")` : 인증된 요청자이며 SELLER 권한이 있는 사용자만 이 API에 접근할 수 있습니다.

</details>

### Auditing <a name = "auditing"></a>

<details>
   <summary> 본문 확인 (Click)</summary>
<br />

ORM에서 Auditing은 영속 엔터티와 관련된 이벤트를 추적하고 로깅하는 것을 의미합니다. 여기서 이벤트란 SQL 트리거에서 영감을 얻어은 것으로 삽입, 수정, 삭제 작업을 의미합니다.

이 프로젝트에서는 Spring Data JPA의 Auditing을 사용하여 변경 시점 혹은 사람에 대한 추적이 필요한 엔터티들에 적용합니다. Auditing을 적용하는 과정은 [Github](https://github.com/eastshine-high/til/blob/main/spring/spring-data/spring-data-jpa/auditing.md) 을 통해 정리하였습니다.

</details>

## 사용자 도메인

### 인증(JWT) <a name = "jwt"></a>

<details>
   <summary> 본문 확인 (Click)</summary>
<br />

사용자가 접근이 제한되어 있는 API의 리소스에 접근할 때는, 먼저 로그인을 통해 자신이 누구인지를 인증하고 리소스 접근에 대한 인가를 받아 리소스에 접근할 수 있습니다.

하지만 웹의 기반인 HTTP 프로토콜은 [무상태성(Stateless)](https://github.com/eastshine-high/til/blob/main/web/http/rest/stateless.md) 과 비연결성(Connectionless)이란 특성을 가지고 있습니다. 따라서 모든 요청마다 인증이 필요합니다. 하지만 모든 요청마다 아이디와 비밀번호를 통해 인증할 수는 없으므로 로그인을 통해 인증한 다음에는 쿠키, 세션, JWT 등의 수단을 이용하여 이를 대신합니다.

이 프로젝트에서는 인증을 대신하는 수단으로 JWT를 사용하였습니다. JWT에 대한 설명과 이를 자바 코드로 표현하는 과정은 [Github](https://github.com/eastshine-high/til/blob/main/web/jwt.md) 을 통해 정리하였습니다.

먼저, JWT를 발급하고 파싱할 수 있는 빈(Bean)을 만듭니다.

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

- `claim(KEY_OF_USER_ID, userId)` : [JWT 문서](https://jwt.io/introduction)에서는 토큰의 요소들을 모든 사람이 읽을 수 있음에 유의해야 한다고 설명합니다. 실제로 [JWT debugger](https://jwt.io/#debugger-io) 등을 이용하여 토큰을 확인할 수 있으므로 사용자의 식별자 이외의 정보를 페이로드에 담지 않았습니다(사용자의 식별자 또한 토큰화한다면 더 좋습니다).
- `setExpiration(new Date(System.currentTimeMillis() + (60 * 1000) * 100))` : 토큰은 보안 이슈(위.변조 등)가 발생하지 않도록 필요 이상으로 유지하지 않습니다([JWT의 공식 문서 권장](https://jwt.io/introduction)). 따라서 토큰의 유효 시간은 100분으로 설정하였습니다.

이제 로그인을 통해 인증한 사용자에게 `JwtUtil` 을 이용하여 JWT를 발행할 수 있습니다.

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

</details>

### 외래키와 복합키 사용에 대하여 <a name = "constraints"></a>

<details>
   <summary> 본문 확인 (Click)</summary>
<br />

개인적으로 참여했던 실무 프로젝트에서는 개발 편의성과 유연성을 이유로 외래키와 복합키를 잘 사용하지 않았습니다. 이번 토이 프로젝트를 기회로 이를 직접 경험해 보고 관련 글들을 읽어보면서, 이에 대한 생각을 정리해 볼 수 있었습니다.

외래키 사용에 대하여

- **무결성과 정합성** : 외래키 사용의 가장 큰 장점이라고 생각합니다. 외래키가 설정되어있는 테이블 또는 데이터를 변경할 때, 참조 무결성에 위배되는 데이터가 있을 경우, 즉시 오류가 발생하므로 해당 작업을 수행할 수 없습니다. 따라서 변경 작업 전에 해당 문제가 해결이 되어야 데이터 또는 테이블을 변경할 수 있습니다. 이는 인지하지 못했던 데이터 오류를 사전에 확인하고 조치할 수 있도록 합니다.
- **관리포인트 증가** : 외래키를 설정하면서 `RESTRICT` , `ON UPDATE SET NULL` , `ON DELETE CASCADE`와 같은 옵션을 넣든 넣지 않든, 어느 쪽이든 신경 써야 할 부분이 늘어납니다. 데이터의 양이 더 많아지고 관계가 복잡해질수록 신경 써야 할 부분은 더욱 많아질 수 있습니다. 특히 `ON DELETE CASCADE` 와 같은 옵션은 매우 주의해서 사용해야 합니다.
- **개발 편의성과 변경의 유연성에 대하여** : 위의 두 가지 등의 이유로 외래키의 사용은 개발 편의성과 변경의 유연성이 떨어집니다. 즉, 개발 편의성과 변경의 유연성은 무결성 그리고 정합성과 트레이드 오프 관계로 볼 수 있습니다. 특히 변경이 자주 발생하는 개발 초기 단계에서는 무결성 문제로 인해 변경 작업에 어려움을 겪을 수 있기 때문에, 개발이 안정화 되는 단계에서 외래키를 적용하는 것도 하나의 방법이 될 수 있습니다.
- **인덱스** : 데이터베이스는 외래키를 설정하는 테이블의 칼럼에 자동으로 인덱스를 생성합니다. 따라서 외래키를 사용하지 않지만 해당 칼럼으로 테이블 조인이 자주 발생한다면, 인덱스 생성이 강력히 권장됩니다.
- **성능** : 외래키 제약조건이 있는 테이블의 경우, 부모-자식 관계로 정의된 컬럼에 대해서 두 테이블 데이터가 일치해야 하기 때문에, 외래키로 정의된 동일 데이터(레코드)에 대해 DML 작업이 발생하게 되면, Lock으로 인해 대기해야 하는 상황이 발생합니다.  따라서 대량의 트랜잭션이 발생하는 경우라면 외래키 사용을 지양해야 할 필요가 있습니다. 성능의 차이에 대해서는 다음 [블로그](https://martin-son.github.io/Martin-IT-Blog/mysql/foreign) 를 참고해 볼 수 있습니다.

복합키 사용에 대하여

- **주의 사항** : 복합키를 정의할 때는, 복합키를 구성하는 칼럼의 순서에 주의할 필요가 있었습니다. DBMS는 자동으로 복합키를 구성하는 칼럼의 순서대로 인덱스를 생성합니다. 이 때, 복합 인덱스의 선두 칼럼의 카디널리티에 따라서 인덱스의 성능 차이가 발생할 수 있습니다. 따라서 카디널리티가 높은 칼럼의 순서대로 복합키의 순서를 구성하는 것이 좋습니다.
- **인덱스** : 만약 복합키를 사용하지 않고 인조 식별자를 기본키를 사용한다면, 복합키로 선언하지 않은 칼럼들은 인덱스로 구성하는 것을 고려할 필요가 있습니다.

</details>

## 상품 도메인

### 재고 관리(동시성 이슈) <a name = "stock"></a>

<details>
   <summary> 본문 확인 (Click)</summary>
<br />

재고 관리는 동시성 이슈를 고려하여 로직을 작성해야 합니다.

먼저, [재고 감소 로직에서 발생할 수 있는 동시성 이슈](https://github.com/eastshine-high/til/blob/main/spring/spring-framework/blog/concurrency-Issue-1.md) 를 알아보고 이를 [MySQL에서 해결하는 방법](https://github.com/eastshine-high/til/blob/main/spring/spring-framework/blog/concurrency-Issue-2.md) 을 Github을 통해 정리하였습니다. 또한 싱글 스레드, 인메모리 데이터베이스인 Redis를 통해서도 이를 해결할 수 있습니다(이 방법은 추후 정리하겠습니다).

이 프로젝트에서는 동시성 이슈 문제의 해결을 위해, MySQL의 Named Lock을 이용한 분산 락과 Redis의 Redisson 클라이언트를 이용한 분산 락을 구현하였습니다. 두 코드 모두 템플릿-콜백 패턴을 이용하여 Lock을 획득한 후에, 구현 로직을 호출하도록 설계하였습니다.

- [MariaDbLock](https://github.com/eastshine-high/auction-backend/blob/main/app/src/main/java/com/eastshine/auction/common/lock/MariaDbLock.java)
- RedissonLock

```java
@Slf4j
@RequiredArgsConstructor
@Component
public class RedissonLock {
    private final RedissonClient redissonClient;

    public void executeWithLock(String prefix, String id, Runnable runnable) {
        final RLock lock = redissonClient.getLock(prefix + id);
        try {
            boolean isAvailable = lock.tryLock(10, 1, TimeUnit.SECONDS);

            if (!isAvailable) {
                log.info(prefix + id + " : redisson getLock timeout");
                return;
            }

            runnable.run();
        } catch (InterruptedException e) {
            throw new BaseException(ErrorCode.COMMON_LOCK_FAIL);
        } finally {
            lock.unlock();
        }
    }
}
```

아래는 `RedissonLock` 을 활용하여 Lock을 획득한 뒤에, 재고를 차감합니다.

```java
@RequiredArgsConstructor
@Service
public class ItemStockService {
    private static final String ITEM_LOCK_PREFIX = "ITEM_STOCK_";
    
    private final RedissonLock redissonLock;
    private final ItemRepository itemRepository;
    
    @Transactional
    public void decreaseItemStockWithLock(Long id, Integer quantity){
        redissonLock.executeWithLock(
            ITEM_LOCK_PREFIX,
            id.toString(),
            () -> decreaseItemStock(id, quantity)
        );
    }
    
    private void decreaseItemStock(Long id, Integer quantity) {
        Item item = itemRepository.findById(id)
              .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ITEM_NOT_FOUND));
        item.decreaseStockQuantity(quantity);
        itemRepository.saveAndFlush(item);
    }
}
```

</details>

### 단일 책임 원칙과 URI 설계  <a name = "single-responsibility"></a>

<details>
   <summary> 본문 확인 (Click)</summary>
<br />

[REST API 디자인 가이드](https://github.com/eastshine-high/til/blob/main/web/http/rest/api/resource-modeling.md) 를 따라 ‘상품 조회 API’를 설계하면, 일반적으로 다음과 같을 것입니다.

```
GET /api/products/{id}
```

그런데 사이트 방문자(Guest)가 조회할 상품 정보와 판매자(Seller)가 조회할 상품 정보는 다릅니다. 따라서 이를 구분할 필요가 있었습니다. 이 경우에는 단일 책임 원칙(”하나의 모듈은 하나의, 오직 하나의 액터에 대해서만 책임져야 한다”)을 URI에 적용해 볼 수 있었습니다. 다음과 같이 액터를 URI에 추가하여 표현합니다.

방문자 상품 조회 URI : `guest-api/products/{id}`

판매자 상품 조회 URI : `seller-api/products/{id}`

> 이 프로젝트에서 `guest-api` 는 편의상 `api` 로 표현하였습니다.
>

실제 [쿠팡](https://developers.coupangcorp.com/hc/ko/articles/360033877853-%EC%83%81%ED%92%88-%EC%83%9D%EC%84%B1) 에서도 다음과 같이 액터를 구분하여 URI를 설계하는 것을 확인해 볼 수 있었습니다.

```
/v2/providers/seller_api/apis/api/v1/marketplace/seller-products
```
그리고 URI와 함께, 클래스 또한 액터에 따라 분리하였습니다. 이렇게 분리한 클래스들은 더욱 단일 책임 원칙을 준수하는 것을 확인할 수 있었습니다.

또한 액터에 따른 분리는 [CQRS](https://github.com/eastshine-high/til/blob/main/domain-driven-design/cqrs.md) 의 기준이 될 수도 있었습니다. 상품에 대한 방문자의 주요 관심사는 조회(Query)이며 판매자의 주요 관심사는 데이터의 조작(Command)입니다. 따라서 액터의 분리가 자연스럽게 CQRS의 기준이 되었습니다.

이 프로젝트에서는 복잡한 로직이 필요하지 않은 방문자 API의 컨트롤러가 리포지토리에 직접 의존하므로써 간단한 형태의 CQRS를 적용하였습니다. 이를 통해 서비스 레이어의 구현을 생략함으로써 조회 로직을 간소화 시킬 수 있었습니다.

</details>

### Main-Sub 엔터티 vs 계층 구조 엔터티 <a name = "entity-design"></a>

<details>
   <summary> 본문 확인 (Click)</summary>
<br />

아래의 쇼핑몰 카테고리를 설계할 때는 Main-Sub 엔터티 구조와 자기 자신을 참조하는 계층 구조의 엔터티로의 설계를 고려해 볼 수 있습니다.

![https://velog.velcdn.com/images/eastshine-high/post/bde225b5-4d69-4eb4-87c8-facf09c17ea6/image.png](https://velog.velcdn.com/images/eastshine-high/post/bde225b5-4d69-4eb4-87c8-facf09c17ea6/image.png)

무엇이 좋은 방법일지를 고민하면서 얻은 결론은 “**설계에 정답있는 것은 아니며 Trade off의 과정이다**”라는 점을 배울 수 있었습니다. 따라서 설계에 따른 Trade off를 생각해 봅니다.

**Main-Sub Entity 구조**

- (장점) 데이터를 관리(CRUD)하기 쉽습니다.
- (단점) 엔티티의 계층적 확장 측면에서 유연하지 못합니다.

**계층(재귀) 구조**

- (장점) 엔티티의 계층적 확장 측면에서 유연합니다.
- (단점) 데이터를 관리(CRUD)하기 어렵습니다.

결론적으로 추가적인 Sub Entity의 확장을 고려하여 `Category` 엔티티의 설계는 재귀 구조로 결정하였습니다.

![https://velog.velcdn.com/images/eastshine-high/post/d2a217bc-e8cf-4b03-9059-28c3c1a4494d/image.png](https://velog.velcdn.com/images/eastshine-high/post/d2a217bc-e8cf-4b03-9059-28c3c1a4494d/image.png)

</details>

### 상품 검색 <a name = "searching-product"></a>

<details>
   <summary> 본문 확인 (Click)</summary>
<br />

상품 검색 API의 문제점

상품 검색은 RDB의 SQL문 `LIKE '%Keyword%'` 을 사용하여 검색합니다. 이 SQL 문은 Index Range Scan을 할 수 없고, **Index Full Scan을 수행하기 때문에 조회 성능이 좋지 못합니다**. 만약 상품 검색 요청이 자주 발생할 경우, 서비스 성능이 저하될 수 있는 부분입니다.

개선 방안

이러한 문제를 개선하기 위한 방법으로 Elasticsearch를 검색 엔진으로 활용해보는 것을 검토해 볼 수 있습니다. Elasticsearch는 특정 문장을 입력받으면, 파싱을 통해 문장을 단어 단위로 분리하여 저장합니다. 검색을 할 때는 분리된 단어를 기반으로 역으로 인덱스(Reverted Index)를 찾아가는 방식으로 검색을 수행합니다. 따라서 RDB의 Keyword 검색에 수행하는 Index Full Scan 만큼의 시간을 아낄 수 있습니다.

또한 간단한 방법으로 MySQL의 경우, 전문 검색 Index를 사용할 수 있습니다. 전문 검색 Index 또한 Elasticsearch처럼 분리된 단어를 기반으로 인덱스를 찾아갑니다. 다만 이 방법을 통한 서비스 사례는 찾을 수 없었습니다.

</details>


## 주문

### 주문 프로세스 <a name = "order-process"></a>

<details>
   <summary> 본문 확인 (Click)</summary>
<br />

**주문하기**

주문하기 업무는 여러 도메인과의 협력을 통해 진행됩니다. 이 때, 이벤트를 활용하면 도메인 간의 결합도를 낮출 수 있습니다. 따라서 주문 성공에 대한 알림 메일은 Kafka를 이용해 비동기 이벤트 처리하였습니다(사실 모놀리틱 아킥텍처에서는 Spring의 비동기 이벤트만으로 충분하므로 Kafka 사용은 오버엔지니어링입니다).

재고 차감의 경우, 재고 차감 여부에 따라 주문 결과가 달라지므로 이벤트 처리가 아닌 상품 도메인의 재고 서비스를 의존성 주입 받아 협력하였습니다(MSA 구조에서는 동기 통신으로 협력합니다).

![http://dl.dropbox.com/s/auchgbr2ovvvajd/place_order_flow.png](http://dl.dropbox.com/s/auchgbr2ovvvajd/place_order_flow.png)

다음은 주문하기 서비스의 코드입니다.

```java
@RequiredArgsConstructor
@Service
public class PlaceOrderService {
    private final ProductStockService productStockService;
    private final OrderService orderService;
    private final PlaceOrderProducer placeOrderProducer;

    @Transactional
    public Order placeOrder(OrderDto.PlaceOrderRequest request) {
        request.getOrderItems().stream()
                .forEach(productStockService::decreaseStock); // 1. 재고 차감
        Order registeredOrder = orderService.registerOrder(request);// 2. 주문 등록
        placeOrderProducer.sendMail(request.getUserInfo(), registeredOrder); // 3. 메일 발송 이벤트 발행
        return registeredOrder;
    }
}
```

- `PlaceOrderService` 는 퍼사드로 표현할 수도 있습니다.

<br>

**주문 취소**

주문 취소의 경우, 모든 도메인과 비동기 이벤트를 통해 협력합니다. 재고 차증은 재고 차감과 달리 재고 부족 문제가 발생하지 않으므로 비통기 이벤트로 처리합니다.

![http://dl.dropbox.com/s/3ihq122y08jnulp/cancel_order_flow.png](http://dl.dropbox.com/s/3ihq122y08jnulp/cancel_order_flow.png)

다음은 주문 취소 서비스의 코드입니다.

```java
@RequiredArgsConstructor
@Service
public class CancelOrderService {
    private final OrderRepository orderRepository;
    private final CancelOrderProducer cancelOrderProducer;
    private final CancelOrderPolicy cancelOrderPolicy;

    @Transactional
    public void cancelOrder(Long orderId, UserInfo userInfo) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ORDER_NOT_FOUND));
        cancelOrderPolicy.validateCancelableOrder(order, userInfo);

        order.cancel(); // 1. 주문 취소
        cancelOrderProducer.increaseStock(order); // 2. 재고 증가
        cancelOrderProducer.sendMail(userInfo, order); // 3. 메일 발송 이벤트 발행
    }
}
```

</details>

## 코드 개선하기

### 관심사의 분리 <a name = "separation-of-concern"></a>

<details>
   <summary> 본문 확인 (Click)</summary>
<br />

다음은 카테고리( `Category` )를 등록하는 서비스 코드입니다. 단순히 요청 객체(DTO)의 값을 검증하고 이를 도메인 객체로 매핑한 뒤에, 리포지토리에 저장하는 간단한 로직입니다.

```java
@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public Category registerCategory(CategoryRegistrationRequest request) {
        Category parentCategory = null;
        if(Objects.nonNull(request.getParentId())) {
            parentCategory = categoryRepository.findById(request.getParentId())
                    .orElseThrow(CategoryEntityNotFoundException::new);
        }
    
        Category category = Category.builder()
                .id(request.getId())
                .parent(parentCategory)
                .ordering(request.getOrdering())
                .name(request.getName())
                .build();
        
        return categoryRepository.save(category);
    }
}
```

간단한 로직이지만 코드의 길이가 길어지면서 코드의 가독성이 떨어집니다. 이렇게 코드가 복잡해진 이유는 DTO 객체의 필드를 도메인 객체의 필드로 매핑하는 책임을 서비스가 가지고 있기 때문입니다.

매핑하는 책임은 매핑할 정보를 알고 있는 DTO 객체에서 수행하는 것이 조금 더 적합해 보입니다. 따라서 매핑하는 책임을 DTO 객체에 위임하여 관심사를 분리합니다.

```java

public class CategoryRegistrationRequest {

    @NotNull
    private Integer id;
    private Integer parentId;

    @NotBlank
    private String name;

    @NotNull
    private Integer ordering;

    public Category toEntity(Category parentCategory) {
        return Category.builder()
                .id(id)
                .parent(parentCategory)
                .name(name)
                .ordering(ordering)
                .build();
    }
}
```

이제 다음과 같이 코드의 길이가 짧아지면서 서비스 코드의 가독성이 개선되는 것을 확인할 수 있습니다. 또한 getter와 같이 메소드를 필요 이상으로 사용하지 않을 수 있습니다.

```java
@Transactional
public Category registerCategory(CategoryRegistrationRequest categoryRegistrationRequest) {
    Category parentCategory = null;
    if(Objects.nonNull(categoryRegistrationRequest.getParentId())) {
        parentCategory = categoryRepository.findById(categoryRegistrationRequest.getParentId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.CATEGORY_PARENT_ENTITY_NOT_FOUND));
    }

    return categoryRepository.save(categoryRegistrationRequest.toEntity(parentCategory));
}
```

</details>

### 테스트 코드 작성을 통한 올바른 책임의 이해 <a name = "test-responsibility"></a>

<details>
   <summary> 본문 확인 (Click)</summary>
<br />



테스트 코드를 작성하다보면, 객체에 책임을 잘못 할당한 것을 깨닫게 되는 경우가 있습니다. 객체에 할당한 잘못된 책임은 테스트 코드 작성에도 영향을 주기 때문입니다. 이를 고치는 과정에서 객체의 책임을 더 잘 이해할 수 있었습니다.

“물품 정보는 물품 정보를 생성한 사용자만 수정할 수 있다”는 권한 검사를 예로 들어보겠습니다. 이 책임을 수행하기 좋은 객체는 `컨트롤러` , `서비스` , `도메인 객체` 중에 어디일까요? 결론부터 이야기 하면, ‘물품 정보를 생성한 사용자’ 정보를 알고 있는 `도메인 객체` 입니다.

하지만 저는 처음에, 이 권한 검사를 컨트롤러에서 처리하도록 하였습니다. 이렇게 했던 이유로는, 컨트롤러에서 접근 검증을 마친다면 서비스와 도메인 엔터티에서는 이를 신경쓸 필요가 없기 때문입니다. 또한 서비스 메소드에 접근하려는 사용자의 식별자를 전달할 필요가 없으므로, 서비스 메소드의 아규먼트 갯수를 줄일 수 있기 때문입니다(클린코드에서는 메소드의 아규먼트가 적을 수록 좋다고 합니다).

위의 이유로, 아래와 같이 접근 검증을 컨트롤러에서 수행하였습니다.

```java
@PatchMapping("/items/{itemId}")
@PreAuthorize("hasAuthority('SELLER')")
@ResponseStatus(HttpStatus.OK)
public void patchItem(
        @PathVariable Long itemId,
        @RequestBody JsonMergePatch patchDocument,
        Authentication authentication
) {
    Item item = itemService.findItem(itemId);
    validateAccessableItem(item, authentication);

    itemService.updatePatchedItem(patchDocument, item);
}

private void validateAccessableUser(Item item, Authentication authentication) {
		UserInfo userInfo = (UserInfo) authentication.getPrincipal();
    if(item.getCreatedBy() != userInfo.getId()){
        throw new UnauthorizedException(ErrorCode.ITEM_UNACCESSABLE);
    }
}
```

하지만 이에 대한 테스트 코드를 작성하는 과정에서 어려움을 겪게 되었습니다. 그 이유는 컨트롤러의 메소드 아규먼트에서는 ‘물품 정보를 생성한 사용자의 식별자’가 없기 때문입니다. 이는 컨트롤러에 너무 많은 책임을 할당했다고 볼 수 있습니다.

**캡슐화**

“물품 정보는 물품 정보를 생성한 사용자만 수정할 수 있다”는 책임을 수행하기 가장 적합한 객체는 ‘물품 정보를 생성한 사용자’를 알고 있는 객체에서 수행하는 것이 가장 적합합니다. 따라서 ‘도메인 객체’에서 접근을 검증하는 책임을 수행합니다.

```java
@Entity
public class Item {
		...

		private Long createdBy;

		public void validateAccessibleUser(Long userId) {
        if(createdBy != userId){
            throw new UnauthorizedException(ErrorCode.ITEM_INACCESSIBLE);
        }
    }
}
```

이를 통해 캡슐화를 지키며 접근 검증을 수행할 수 있습니다. 사실, 위의 컨트롤러 코드에서 `item.getCreatedBy` 는 `Item` 객체의 `createdBy` 필드를 외부에 노출시킵니다. 따라서 캡슐화를 위배하기도 합니다.

또한 이에 대한 테스트를 작성하기가 매우 쉬워졌습니다. `validateAccessibleUser` 메소드의 아규먼트인 `userId` 의 값에 변화를 주어 쉽게 테스트가 가능합니다.

```java
@Nested
@DisplayName("validateAccessibleUser 메소드는")
class Describe_validateAccessibleUser{

    @Test
    @DisplayName("물품을 생성한 사용자가 아닐 경우, InvalidArgumentException 예외를 던진다.")
    void contextWithInaccessibleUser() {
        Item item = new Item();
        Long creatorId = 21L;
        Long accessorId = 2000L;
        ReflectionTestUtils.setField(item, "createdBy", creatorId);

        assertThatThrownBy(
                () -> item.validateAccessibleUser(accessorId)
        )
                .isExactlyInstanceOf(UnauthorizedException.class)
                .hasMessage(ErrorCode.ITEM_INACCESSIBLE.getErrorMsg());
    }
    
    @Test
    @DisplayName("물품을 생성한 사용자일 경우, 예외를 던지지 않는다.")
    void contextWithAccessibleUser() {
        Item item = new Item();
        Long creatorId = 21L;
        Long accessorId = 21L;
        ReflectionTestUtils.setField(item, "createdBy", creatorId);

        item.validateAccessibleUser(accessorId);
    }
}
```

</details>
