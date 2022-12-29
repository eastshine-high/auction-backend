# 재고 관리(동시성 이슈)

**관련 정리**

- [재고 감소 로직에서 발생할 수 있는 동시성 이슈](https://github.com/eastshine-high/til/blob/main/spring/spring-framework/blog/concurrency-Issue-1.md)
- [MySQL을 이용한 동시성 이슈를 해결](https://github.com/eastshine-high/til/blob/main/spring/spring-framework/blog/concurrency-Issue-2.md)

재고 관리는 동시성 이슈를 고려하여 로직을 작성해야 합니다.

이 프로젝트에서는 동시성 이슈 문제의 해결을 위해 Redis의 Redisson 클라이언트를 이용한 분산 락과 MySQL의 Named Lock을 이용한 분산 락을 구현하였습니다. 두 코드 모두 템플릿-콜백 패턴을 이용하여 Lock을 획득한 후에, 구현 로직을 호출합니다.

- [MariaDbLock](https://github.com/eastshine-high/auction-backend/blob/main/app/src/main/java/com/eastshine/auction/common/lock/MariaDbLock.java)
- [RedissonLock](https://github.com/eastshine-high/auction-backend/blob/main/app/src/main/java/com/eastshine/auction/common/lock/RedissonLock.java)

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
                log.info(prefix + "-" + id + " : redisson getLock timeout");
                return;
            }

            runnable.run();
        } catch (InterruptedException e) {
            log.error("[RedissonLock-InterruptedException] cause = {}, errorMsg = {}", NestedExceptionUtils.getMostSpecificCause(e), NestedExceptionUtils.getMostSpecificCause(e).getMessage());
            throw new BaseException(ErrorCode.COMMON_LOCK_FAIL);
        } finally {
            lock.unlock();
        }
    }
}
```
[ItemStockService](https://github.com/eastshine-high/auction-backend/blob/main/app/src/main/java/com/eastshine/auction/product/application/ItemStockService.java) 은 `RedissonLock` 을 활용하여 Lock을 획득한 뒤에, 물품 재고를 차감합니다.

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
