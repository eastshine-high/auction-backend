# 주문 프로세스(비동기 이벤트)

**주문하기**

주문하기 업무는 업무 특성상 여러 도메인과의 협력이 필요합니다.

주문하기 업무는 여러 도메인과의 협력을 통해 진행됩니다. 이 때, 이벤트를 활용하면 협력하는 도메인 간의 결합도를 낮출 수 있습니다.

만저, 주문하기 업무는 다음 흐름으로 진행됩니다.

도메인 결합도를 낮춘 것에 대한 의미를 설명해야 한다.

![http://dl.dropbox.com/s/auchgbr2ovvvajd/place_order_flow.png](http://dl.dropbox.com/s/auchgbr2ovvvajd/place_order_flow.png)

- 주문 성공에 대한 알림 메일은 Kafka를 이용해 비동기 이벤트 처리하였습니다
    - 사실 MSA가 아닌 모놀리틱 아키텍처에서의 비동기 이벤트 처리는 Spring이 지원하는 이벤트 기능만으로 충분합니다. Kafka는 기술 사용 연습 차원에서 적용하였습니다.

- 재고 차감은, 재고 차감의 성공 여부에 따라 주문 결과가 달라지므로 이벤트로 처리하지 않고, 상품 도메인의 재고 서비스를 의존성 주입하였습니다.

[PlaceOrderService](https://github.com/eastshine-high/auction-backend/blob/main/app/src/main/java/com/eastshine/auction/order/application/PlaceOrderService.java) - '주문하기' 서비스 코드는 다음과 같습니다.

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

- [PlaceOrderProducer](https://github.com/eastshine-high/auction-backend/blob/main/app/src/main/java/com/eastshine/auction/order/adaptor/PlaceOrderProducer.java) 는 주문하기 서비스의 이벤트 발행을 담당합니다.

<br>

**주문 취소**

주문 취소의 경우, 모든 도메인과 비동기 이벤트를 통해 협력합니다. 재고 차증은 재고 차감과 달리 재고 부족 문제가 발생하지 않으므로 비통기 이벤트로 처리하였습니다.

![http://dl.dropbox.com/s/3ihq122y08jnulp/cancel_order_flow.png](http://dl.dropbox.com/s/3ihq122y08jnulp/cancel_order_flow.png)

[CancelOrderService](https://github.com/eastshine-high/auction-backend/blob/main/app/src/main/java/com/eastshine/auction/order/application/CancelOrderService.java) - '주문 취소' 서비스 코드는 다음과 같습니다.

```java
@RequiredArgsConstructor
@Service
public class CancelOrderService {
    private final OrderRepository orderRepository;
    private final CancelOrderPolicy cancelOrderPolicy;
    private final CancelOrderProducer cancelOrderProducer;    

    @Transactional
    public void cancelOrder(Long orderId, UserInfo userInfo) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ORDER_NOT_FOUND));
        cancelOrderPolicy.validateCancelableOrder(order, userInfo);

        order.cancel(); // 1. 주문 취소
        cancelOrderProducer.increaseStock(order); // 2. 재고 차증
        cancelOrderProducer.sendMail(userInfo, order); // 3. 메일 발송 이벤트 발행
    }
}
```

- [CancelOrderPolicy](https://github.com/eastshine-high/auction-backend/blob/main/app/src/main/java/com/eastshine/auction/order/domain/policy/CancelOrderPolicy.java) - 주문 취소 정책을 통해, 취소가 가능한 주문인지 확인합니다.
- [CancelOrderProducer](https://github.com/eastshine-high/auction-backend/blob/main/app/src/main/java/com/eastshine/auction/order/adaptor/CancelOrderProducer.java) - 주문 취소 서비스의 이벤트 발행을 담당합니다.
