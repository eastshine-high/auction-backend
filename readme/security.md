# API의 보안(Security)

API는 일부 사용자만 접근을 허용해야할 때가 있습니다. 이러한 보안을 위해서는 인증(당신은 누구입니까)과 인가(당신은 무엇을 할 수 있습니까) 과정이 필요합니다. Spring Security는 서블릿 애플리케이션에서의 인증(Authentication) 및 인가(Authentication) 처리를 지원하므로 이를 이용해 API의 보안 처리를 합니다.

먼저 사용자 도메인에서는 [로그인을 통한 인증으로 JWT를 발급](https://github.com/eastshine-high/auction-backend/blob/main/readme/jwt.md) 하였습니다. API 보안에서는 Spring Security를 이용하여 사용자 도메인에서 발급한 JWT를 인증하고 인가 처리를 합니다.

예를 들어 아래는 상품 정보의 수정을 요청하는 HTTP 요청 메세지입니다. HTTP의 `Authorization` 헤더에 발급받은 토큰을 넣어 요청합니다.

```java
PATCH /seller-api/v1/products/1 HTTP/1.1
Content-Type: application/json
Authorization: Bearer eyJ1c2VySW5mbyI6eyJpZCI6MSwiZW1haWwiOiJ0ZXN0QGdtYWlsLmNvbSIsIm5pY2tuYW1lIjoibmlja25hbWUiLCJyb2xlcyI6WyJVU0VSIiwiU0VMTEVSIl19fQ
Content-Length: 160
Host: 52.79.43.121

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

Spring Security는 위 흐름 중에서 서블릿 필터를 기반으로 동작합니다([Spring Security의 구조](https://github.com/eastshine-high/til/blob/main/spring/spring-security/architecture.md) 에 대한 자세한 설명은 Github을 통해 정리하였습니다). 따라서 `Authorization` 헤더에 담긴 JWT 를 인증하기 위해서는 스프링 시큐리티 구성(Config)에 이를 인증하기 위한 필터를 추가합니다.

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

- `addFilter(authenticationFilter)` : JWT 를 인증하기위해 구현한 필터를 추가합니다. 구현한 필터는 바로 아래에서 설명합니다.
- `WebSecurityConfigurerAdapter` : `WebSecurity` 를 위한 구성 인스턴스를 생성할 때, 편리한 기본 클래스를 제공합니다. 일반적으로 `@EnableWebSecurity` 어노테이션과 함께 사용합니다. 필요한 메서드를 재정의하여 커스텀할 수 있습니다.
- `HttpSecurity` : 네임스페이스 구성에서 Spring Security의 XML `<http>` 엘러먼트와 유사합니다. 특정 `http` 요청에 대해 웹 기반 보안을 구성할 수 있습니다. 기본적으로 모든 요청에 적용되지만 `requestMatcher(RequestMatcher)` 또는 다른 유사한 방법을 사용하여 제한할 수 있습니다.
- `csrf().disable()` : 현재 서버는 REST API로만 사용하므로 `csrf` 를 사용하지 않았습니다.
- `@EnableGlobalMethodSecurity` : 아래 인가 처리에서 메소드 시큐리티를 활성화하기 위해 사용합니다.

**인증(Authentication)**

위의 시큐리티 구성(`SecurityConfig`)에 등록한 [JwtAuthenticationFilter](https://github.com/eastshine-high/auction-backend/blob/main/app/src/main/java/com/eastshine/auction/common/security/JwtAuthenticationFilter.java) 를 살펴보겠습니다. 이 필터는 HTTP 요청 정보에 있는 JWT 토큰에 대한 검증을 성공하면 Spring Security를 이용해 인증을 완료합니다. Spring Security의 인증(Authentication) 구조에 대해서는 [Github](https://github.com/eastshine-high/til/blob/main/spring/spring-security/authentication/servlet-authentication-architecture.md) 을 통해 정리하였습니다.

```java
package com.eastshine.auction.common.security;

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

- `BasicAuthenticationFilter` : 위의 시큐리티 구성(`SecurityConfig`)에서 사용한 `addFilter` 메소드는 스프링 시큐리티에서 제공하는 필터만 등록이 가능합니다. 따라서 스프링 시큐리티에서 제공하는 필터 중의 하나인 `BasicAuthenticationFilter` 를 상속하여 필터를 구현하였습니다. `BasicAuthenticationFilter` 는 내부적으로 `OncePerRequestFilter` 를 상속합니다.
- `authenticationService.parseToken` : JWT을 인증하고 파싱합니다. JWT에 대해서는 [로그인 인증](https://github.com/eastshine-high/auction-backend/blob/main/readme/jwt.md) 에서 설명하므로, 여기서는 관련 내용만 참조하겠습니다.
    - [JWT 정리 및 활용](https://github.com/eastshine-high/auction-backend/blob/main/readme/jwt.md)
    - [AuthenticationService](https://github.com/eastshine-high/auction-backend/blob/main/app/src/main/java/com/eastshine/auction/user/application/AuthenticationService.java)
- `Authentication` : JWT가 인증되었다면, 누가 인증되었는 지를 나타내는 `Authentication` 객체를 통해 이를 표현합니다.
- `authenticationService.findUserInfo` : [로그인 인증](https://github.com/eastshine-high/auction-backend/blob/main/readme/jwt.md) 에서는 보안 상의 이유로 사용자의 식별 정보만을 JWT 페이로드에 담았었습니다. 따라서 인증 객체(Authentication)를 생성할 때 필요한 사용자 권한 등의 추가 정보를 데이터베이스(Redis)에서 조회합니다.
- `SecurityContextHolder` 는 스프링 시큐리티 인증 모델의 핵심입니다. 스프링 시큐리티를 통해 인증하는 가장 간단한 방법은 `SecurityContextHolder` 에 누가 인증되었는 지를 직접 설정하는 것입니다(스프링 시큐리티의 다른 필터들과 통합하여 사용하지 않을 경우, `AuthenticationManager` 를 사용하지 않고 `SecurityContextHolder` 를 직접 사용하여 인증할 수 있습니다).

![https://docs.spring.io/spring-security/reference/_images/servlet/authentication/architecture/securitycontextholder.png](https://docs.spring.io/spring-security/reference/_images/servlet/authentication/architecture/securitycontextholder.png)

- `new UserAuthentication(userInfo)` : 누가 인증되었는 지를 나타내는 `Authentication` 은 인터페이스이므로 이를 구현해 사용해야 합니다.

[UserAuthentication](https://github.com/eastshine-high/auction-backend/blob/main/app/src/main/java/com/eastshine/auction/common/security/UserAuthentication.java)

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

- `AbstractAuthenticationToken` : Authentication 인터페이스를 구현한 기본 클래스입니다. Authentication을 처음부터 구현하지 않고 기본 클래스인 `AbstractAuthenticationToken`을 상속하여 구현합니다.
- `SimpleGrantedAuthority` : `GrantedAuthority` 인터페이스를 구현하는 가장 간단한 구현 클래스입니다.
- `credentials` : 암호를 보관하는 필드이나, 사용자 인증이 된 후에는 암호 유출 방지를 위해 일반적으로 비웁니다.

**인가(Authorization)**

인가 처리는 [메소드 시큐리티](https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html) 를 이용하여 처리하였습니다. 보호가 필요한 API에 어노테이션을 추가하여 리소스를 보호합니다.

```java
@PatchMapping("/seller-api/v1/products/{id}")
@PreAuthorize("hasAuthority('SELLER')")
public ResponseEntity patchProduct(@PathVariable Long itemId, @RequestBody @Validated PatchProductDto productDto) {
        ...
}
```

- `@PreAuthorize("hasAuthority('SELLER')")` : 인증된 요청자이며 SELLER 권한이 있는 사용자만 이 API에 접근할 수 있습니다.
