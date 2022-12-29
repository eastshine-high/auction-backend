# 주문 도메인

### 도메인 모델링

다음은 도메인 주도 설계 개념을 적용한 **주문 도메인 모델**입니다.

![http://dl.dropbox.com/s/0wfivcgtgx49awf/order_diagram.png](http://dl.dropbox.com/s/0wfivcgtgx49awf/order_diagram.png)

주문 에그리거트는 루트 엔터티인 주문(Order), Order의 값 객체(VO)인 배달정보(DeliveryFragment), Order와 일대다 관계인 주문물품(OrderItem), OrderItem과 일대다 관계인 주문물품옵션(OrderItemOption)으로 구성됩니다.

### 데이터 모델링

![http://dl.dropbox.com/s/pgedo149dlo3buf/order_erd.png](http://dl.dropbox.com/s/pgedo149dlo3buf/order_erd.png)
