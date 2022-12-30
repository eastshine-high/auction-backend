# 주문 도메인

### 도메인 모델링

[DDD전술적 설계](https://engineering-skcc.github.io/microservice%20modeling/BackEnd-modeling-domainModeling/#ddd%EC%A0%84%EC%88%A0%EC%A0%81-%EC%84%A4%EA%B3%84%EB%8F%84%EB%A9%94%EC%9D%B8-%EB%AA%A8%EB%8D%B8%EB%A7%81-%EA%B5%AC%EC%84%B1%EC%9A%94%EC%86%8C) 를 적용하여 주문 도메인을 모델링합니다.

![http://dl.dropbox.com/s/0wfivcgtgx49awf/order_diagram.png](http://dl.dropbox.com/s/0wfivcgtgx49awf/order_diagram.png)

주문 에그리거트는 루트 엔터티인 주문(Order), Order의 값 객체(VO)인 배달정보(DeliveryFragment), Order와 일대다 관계인 주문물품(OrderItem), OrderItem과 일대다 관계인 주문물품옵션(OrderItemOption)으로 구성됩니다.

### 데이터 모델링

![http://dl.dropbox.com/s/pgedo149dlo3buf/order_erd.png](http://dl.dropbox.com/s/pgedo149dlo3buf/order_erd.png)
