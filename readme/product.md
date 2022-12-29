# 상품 도메인

### 도메인 모델링

다음은 도메인 주도 설계 개념을 적용한 **상품 도메인 모델**입니다.

![http://dl.dropbox.com/s/ps226nop93v6g5q/product_diagram.png](http://dl.dropbox.com/s/ps226nop93v6g5q/product_diagram.png)

상품 도메인은 카테고리(Category) 에그리거트와 물품(Item) 에그리거트로 구성됩니다. 물품 에그리거트는 루트 엔터티인 물품(Item), Item과 일대다 관계인 물품옵션(ItemOption), Item의 값 객체(VO)인 배송정보(ShippingFragment), ShippingFragment의 값 객체인 반송정보(ReturnFragment)로 구성됩니다.

### 데이터 모델링

![http://dl.dropbox.com/s/qbsy9xmccyl3eue/product_erd.png](http://dl.dropbox.com/s/qbsy9xmccyl3eue/product_erd.png)
