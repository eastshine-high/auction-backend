# 상품 도메인

### 도메인 모델링

[DDD전술적 설계](https://engineering-skcc.github.io/microservice%20modeling/BackEnd-modeling-domainModeling/#ddd%EC%A0%84%EC%88%A0%EC%A0%81-%EC%84%A4%EA%B3%84%EB%8F%84%EB%A9%94%EC%9D%B8-%EB%AA%A8%EB%8D%B8%EB%A7%81-%EA%B5%AC%EC%84%B1%EC%9A%94%EC%86%8C) 를 적용하여 상품 도메인을 모델링합니다.

![http://dl.dropbox.com/s/ps226nop93v6g5q/product_diagram.png](http://dl.dropbox.com/s/ps226nop93v6g5q/product_diagram.png)

상품 도메인은 카테고리(Category) 에그리거트와 물품(Item) 에그리거트로 구성됩니다. 물품 에그리거트는 루트 엔터티인 물품(Item), Item과 일대다 관계인 물품옵션(ItemOption), Item의 값 객체(VO)인 배송정보(ShippingFragment), ShippingFragment의 값 객체인 반송정보(ReturnFragment)로 구성됩니다.

### 데이터 모델링

![http://dl.dropbox.com/s/qbsy9xmccyl3eue/product_erd.png](http://dl.dropbox.com/s/qbsy9xmccyl3eue/product_erd.png)
