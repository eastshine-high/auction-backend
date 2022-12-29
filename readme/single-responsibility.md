# 단일 책임 원칙과 URI 설계

[REST API 디자인 가이드](https://github.com/eastshine-high/til/blob/main/web/http/rest/api/resource-modeling.md) 를 따라 ‘상품 조회 API’를 설계하면, 일반적으로 다음과 같을 것입니다.

```
GET /api/v1/products/{id}
```

그런데 사이트 방문자(Guest)가 조회할 상품 정보와 판매자(Seller)가 조회할 상품 정보는 다릅니다. 따라서 이를 구분할 필요가 있었습니다. 이 경우에는 단일 책임 원칙(”하나의 모듈은 하나의, 오직 하나의 액터에 대해서만 책임져야 한다”)을 URI에 적용해 볼 수 있었습니다. 다음과 같이 액터를 URI에 추가하여 표현합니다.

방문자 상품 조회 URI : `/guest-api/v1/products/{id}`

판매자 상품 조회 URI : `/seller-api/v1/products/{id}`

> 이 프로젝트에서 `guest-api` 는 편의상 `guest` 를 생략하여 `api` 로 표현하였습니다.
>

실제 [쿠팡](https://developers.coupangcorp.com/hc/ko/articles/360033877853-%EC%83%81%ED%92%88-%EC%83%9D%EC%84%B1) 에서도 다음과 같이 액터를 구분하여 URI를 설계하는 것을 확인해 볼 수 있었습니다.

```
/v2/providers/seller_api/apis/api/v1/marketplace/seller-products
```
URI 분리와 함께, 클래스 또한 액터에 따라 분리합니다. 이렇게 분리한 클래스들은 더욱 단일 책임 원칙을 준수하는 것을 확인할 수 있었습니다.

또한 액터에 따른 분리는 [CQRS](https://github.com/eastshine-high/til/blob/main/domain-driven-design/cqrs.md) 의 기준이 될 수도 있었습니다. 상품에 대한 방문자의 주요 관심사는 조회(Query)이며 판매자의 주요 관심사는 데이터의 조작(Command)입니다. 따라서 액터의 분리가 자연스럽게 CQRS의 기준이 되었습니다.

이 프로젝트에서는 복잡한 로직이 필요하지 않은 방문자 API의 컨트롤러가 리포지토리에 직접 의존하므로써 간단한 형태의 CQRS를 적용하였습니다. 이를 통해 서비스 레이어의 구현을 생략함으로써 조회 로직을 간소화 시킬 수 있었습니다.
