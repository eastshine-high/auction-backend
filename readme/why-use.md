# 기술 사용 배경

### **Flyway**

<img src="http://dl.dropbox.com/s/6xz92p0s6nigdvw/flyway.png" width="30%">

도메인을 개발할 때, 도메인에 변경이 발생하면 데이터베이스의 스키마 또한 변경 사항을 반영해 주어야 합니다. 다만 이 과정에서 서비스 운영에서 중요한 부분 중의 하나인 데이터베이스를 이력관리 없이 수동으로 변경하며 관리하는 것에 불안전함을 느꼈습니다. 이에 대한 관리 방법을 찾아 보면서, Flyway라는 도구에 대해 알게되었습니다. Flyway를 적용하면서 **데이터베이스의 변경 사항에 대한 이력을 관리**함으로써 데이터베이스를 좀 더 안정적으로 관리할 수 있었습니다.

이력 관리 디렉토리: [resources/db/migration/**](https://github.com/eastshine-high/auction-backend/tree/main/app/src/main/resources/db/migration)

### **Redis**

1. 캐시(Look-aside)

카테고리 엔터티는 다음과 같이 재귀 구조로 설계되어 있습니다.

![https://velog.velcdn.com/images/eastshine-high/post/d2a217bc-e8cf-4b03-9059-28c3c1a4494d/image.png](https://velog.velcdn.com/images/eastshine-high/post/d2a217bc-e8cf-4b03-9059-28c3c1a4494d/image.png)

이 구조는 JPA를 통해 조회를 할 경우, N + 1 문제가 발생할 수 밖에 없습니다. 따라서 쇼핑몰 메인페이지에서 조회하는 카테고리와 같이, 요청이 자주 들어오는 API에 캐싱 처리를 하여 조회 성능을 개선하였습니다.

```java
@RequiredArgsConstructor
@RestController
public class CategoryController {
    private final CategoryRepository categoryRepository;

    @GetMapping("/api/v1/display/categories")
    @Cacheable(value = "displayCategories", cacheManager = "cacheManager")
    public List<CategoryDto.DisplayMain> getDisplayCategories() {
        List<Category> categories = categoryRepository.findDisplayCategories();
        return categories.stream()
                .map(CategoryDto.DisplayMain::new)
                .collect(Collectors.toList());
    }
}
```

2. 분산락

멀티 쓰레드 구조의 관계형 DB와 달리 Redis는 싱글 쓰레드이면서 In-memory 저장소라는 특징을 가지고 있습니다. 따라서 [동시성 이슈](#stock) 를 처리하기 위한 분산락을 구현하기 좋은 저장소로 볼 수 있습니다.

3. In-memory 저장소

로그인 인증에 성공한 경우, 사용자에게 세션 용도의 JWT를 발급합니다(이는 [좋은 방식이 아닐](https://velog.io/@park2348190/API-서버의-인증-수단으로-JWT를-사용하는-것이-옳은가) 수 있습니다). 이 때, JWT의 페이로드는 모든 사람이 읽을 수 있음에 유의해야 하기 때문에([공식 문서 권장](https://jwt.io/introduction)) JWT의 페이로드에는 사용자의 식별자만 보관하였습니다. 따라서 접근이 제한되어있는 API에 대한 모든 HTTP 요청마다, 사용자 권한을 조회하기 위한 데이터베이스 접근이 발생합니다. 이 때, 인증에 성공한 사용자 정보를 Redis(In-memory 저장소)에 보관하여 API의 성능을 개선합니다.

4. 현재 Redis 사용의 개선점

Redis를 캐시 이외의 용도로 사용한다면, 적절한 데이터 백업이 필요합니다. 그 이유는 하나의 Redis만 사용할 때, Redis가 죽어버리면 Redis를 사용하는 로직들에 문제가 생기기 때문입니다. 따라서, 현재 하나의 Redis만 운용중인 서버에 추가적인 백업 Redis 운용이 필요합니다.

### **Kafka**

주문 도메인은 업무 특성상 다른 도메인과의 협력이 많이 필요합니다. 이 때, 이벤트를 활용하면 도메인 간의 결합도를 낮추며 협력할 수 있습니다. 따라서 [주문 및 주문 취소 업무](#order-process) 에서는 Kafka를 이용한 비동기 이벤트 처리를 통해 도메인 간의 결합도를 낮추었습니다. 사실 MSA가 아닌 모놀리틱 아키텍처에서 비동기 이벤트 처리는 Spring이 지원하는 이벤트 기능만으로 충분합니다. Kafka는 도메인 주도 개발을 공부하면서 관심이 커져, 기술 사용 연습 차원에서 도입하였습니다.

### **JSON Merge Patch**

[JSON Merge Patch를 이용한 PATCH API 구현하기](https://github.com/eastshine-high/til/blob/main/spring/spring-framework/blog/json-merge-patch.md)

리소스의 값을 변경하는 REST API를 구현할 때, 도메인 레이어에서는 다음과 같이 리소스의 값을 변경하는 메소드를 작성할 수 있습니다.

```java
@Entity
public class Product
    private String name;
    private Integer price;
    private Integer stockQuantity;
    private boolean onSale;

    public void changeWith(Product source) {
        name = source.name;
        price = source.price;
        stockQuantity = source.stockQuantity;
        onSale = source.onSale;
    }
}
```

이 때 메소드 아규먼트 `source` 에 속성 값을 전달하지 않을 경우, 해당 객체의 속성의 값은 `null` 로 변경됩니다. 따라서 위와 같이 구현된 API를 이용해 리소스를 변경할 때는 리소스를 모두 표현(Representation)하여 변경을 요청해야 합니다. 이와 같은 방식을 HTTP에서는 `PUT` 메소드라고 합니다.

하지만 HTTP의 `PUT` 메소드를 사용하면 리소스의 단일 필드를 수정해야 하는 경우에도 리소스의 전체 표현을 보내야 하므로 다소 불편합니다. 따라서 `PUT` 이 아닌 `PATCH` HTTP 메소드를 지원하는 API를 구현해 보기로 했습니다.

먼저, 가장 단순한 방법으로 각 속성을 변경하기 전에 `if` 문을 추가하면 `PATCH` HTTP 메소드를 지원하는 API의 구현이 가능합니다.

```java
@Entity
public class Product
    private String name;
    private Integer price;
    private Integer stockQuantity;
    private boolean onSale;

    public void changeWith(Product source) {
        if(source.name != null){
            name = source.name;
        }
        if(source.price != null){
            price = source.price;
        }
        ...
    }
}
```

하지만 개발자다운 접근은 아닌 것 같습니다.

또 다른 방법을 찾아보면서 JsonPatch([RFC6902](https://datatracker.ietf.org/doc/html/rfc6902)) 와 JsonMergePatch([RFC7396](https://datatracker.ietf.org/doc/html/rfc7386)) 에 대해서 알게 되었습니다. 이에 대해 정리해 보면서 [JsonMergePatch 를 이용해 PATCH API를 구현](https://github.com/eastshine-high/til/blob/main/spring/spring-framework/blog/json-merge-patch.md) 해 볼 수 있었습니다.
