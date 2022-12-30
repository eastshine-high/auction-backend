# MultipleBagFetchException 해결하기

`MultipleBagFetchException` 는 JPA의 N+1 문제에 대한 해결책으로 Fetch Join을 사용하다보면 자주 만나는 문제입니다. `MultipleBagFetchException` 는 2개 이상의 ToMany 자식 테이블에 Fetch Join을 선언했을 때 발생합니다.

여기서 `Bag` 이란 `org.hibernate.type.BagType` 을 의미합니다. Bag(Multiset)은 Set과 같이 순서가 없고, List와 같이 중복을 허용하는 자료구조입니다. 하지만 자바 컬렉션 프레임워크에서는 Bag이 없기 때문에 하이버네이트에서는 List를 Bag으로써 사용하고 있습니다.

### 문제 상황

먼저 주문 엔터티의 구조와 정의는 다음과 같습니다.

![http://dl.dropbox.com/s/5jb5mnu9h8vcmed/order_entity_diagram.png](http://dl.dropbox.com/s/5jb5mnu9h8vcmed/order_entity_diagram.png)

```java
@Entity
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    private List<OrderItem> orderItems = new ArrayList<>();

		...
}
```

```java
@Entity
public class OrderItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @JoinColumn(name = "order_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.PERSIST)
    private List<OrderItemOption> orderItemOptions = new ArrayList();

		...
}
```

```java
@Entity
public class OrderItemOption  {

    @Id @Column(name = "order_item_option_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "order_item_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private OrderItem orderItem;

		...
}
```

위에서 정의한 주문 에그리것을 QueryDsl을 이용하여 조회합니다. N + 1 문제를 방지하기 위해 `@OneToMany` 관계는 모두 `fetchJoin` 을 사용해 조회하였습니다.

```java
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {
    private final JPAQueryFactory query;

    public OrderRepositoryCustomImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Optional<Order> findByIdWithFetchJoin(Long orderId) {
        return Optional.ofNullable(
                query.selectFrom(order)
                        .join(order.orderItems, orderItem).fetchJoin()
                        .leftJoin(orderItem.orderItemOptions, orderItemOption).fetchJoin()
                        .where(order.id.eq(orderId))
                        .fetchOne()
        );
    }
}
```

하지만 다음과 같이 `MultipleBagFetchException` 이 발생합니다.

```
org.springframework.dao.InvalidDataAccessApiUsageException: 
org.hibernate.loader.MultipleBagFetchException: cannot simultaneously fetch multiple bags: 
[com.eastshine.auction.order.domain.Order.orderItems, com.eastshine.auction.order.domain.item.OrderItem.orderItemOptions]; 
nested exception is java.lang.IllegalArgumentException: 
org.hibernate.loader.MultipleBagFetchException: cannot simultaneously fetch multiple bags: 
[com.eastshine.auction.order.domain.Order.orderItems, com.eastshine.auction.order.domain.item.OrderItem.orderItemOptions]
```

### 문제 해결

**해결 1**

먼저, [Stack overflow](https://stackoverflow.com/questions/4334970/hibernate-throws-multiplebagfetchexception-cannot-simultaneously-fetch-multipl) 를 통해 `List` 자료형을 `Set` 자료형으로 바꾸어 해결할 수 있다는 것을 확인할 수 있습니다. 하지만 **Set 자료형을 사용할 경우에는 다음과 같은 단점**이 있었습니다.

```java
@EqualsAndHashCode(of = "id")
@Entity
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    private Set<OrderItem> orderItems = new HashSet<>();

		public void addOrderItem(OrderItem orderItem) {
        orderItem.setOrder(this);
        orderItems.add(orderItem);
    }
		...
}
```

1. [CascadeType.PERSIST](https://github.com/eastshine-high/til/blob/main/java/specification/jpa-hibernate/persistence-context/cascading-entity-state-transitions.md) 를 이용해 여러 자식 엔터티를 한 번에 영속화 하기가 어렵습니다. `id` 로 Equals와 HashCode 메소드가 오버라이드 되어있는 엔터티가 [비영속(transient) 상태](https://github.com/eastshine-high/til/blob/main/java/specification/jpa-hibernate/persistence-context/persistent-data-status.md) 일 때, Set 자료형에서는 `id` 값이 모두 `null` 이므로 같은 객체로 취급됩니다. 따라서 Set의 `add` 메소드를 사용하기 어려우므로 `CascadeType.PERSIST` 를 이용해 복수의 자식 엔터티들을 한 번에 영속화하기가 어렵습니다.
2. 성능적 문제 - 지연 로딩으로 컬렉션을 조회했을 경우, 컬력션이 아직 초기화 되지 않은 상태에서 컬렉션에 값을 넣게 되면 프록시가 강제로 초기화 되는 문제가 발생합니다. 왜냐하면 중복 데이터가 있는지 비교해야 하는데, 그럴러면 컬렉션에 모든 데이터를 로딩해야 하기 때문입니다.

**해결 2**

`MultipleBagFetchException` 이라는 예외의 이름처럼 `ToMany` 에 대한 Fetch Join은 한 번만 사용할 수 있습니다. 따라서 또 다른 N + 1 문제의 해결 방법인 **Hibernate default_batch_fetch_size**를 이용합니다.

스프링부트에서는 다음과 같이 옵션을 적용할 수 있습니다 - application.yml

```yaml
spring:jpa:properties:hibernate.default_batch_fetch_size: 1000
```

위에서 작성한 Querydsl의 조회 문장도 수정하여 `leftJoin` 문 하나를 제거하였습니다.

```java
@Override
public Optional<Order> findByIdWithFetchJoin(Long orderId) {
    return Optional.ofNullable(
            query.selectFrom(order)
                    .join(order.orderItems, orderItem).fetchJoin()
                    .where(order.id.eq(orderId))
                    .fetchOne()
    );
}
```

수행된 SQL을 확인해 보면 Join으로 처리하지 않은 부분은 `IN` 절로 처리되어 N+1 문제 발생 없이 조회가 되는 것을 확인할 수 있습니다.

```
select
    orderitemo0_.order_item_id as order_it8_4_1_,
    orderitemo0_.order_item_option_id as order_it1_4_1_,
    orderitemo0_.order_item_option_id as order_it1_4_0_,
    orderitemo0_.created_at as created_2_4_0_,
    orderitemo0_.updated_at as updated_3_4_0_,
    orderitemo0_.additional_price as addition4_4_0_,
    orderitemo0_.item_option_id as item_opt5_4_0_,
    orderitemo0_.item_option_name as item_opt6_4_0_,
    orderitemo0_.order_count as order_co7_4_0_,
    orderitemo0_.order_item_id as order_it8_4_0_ 
from
    order_item_option orderitemo0_ 
where
    orderitemo0_.order_item_id in (
        ?, ?
    )
```
