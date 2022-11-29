package com.eastshine.auction.common.lock;

import com.eastshine.auction.common.exception.BaseException;
import com.eastshine.auction.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

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
                log.info(prefix + id + " : redisson getLock timeout");
                return;
            }

            runnable.run();
        } catch (InterruptedException e) {
            log.error("[InterruptedException] cause = {}, errorMsg = {}", NestedExceptionUtils.getMostSpecificCause(e), NestedExceptionUtils.getMostSpecificCause(e).getMessage());
            throw new BaseException(ErrorCode.COMMON_LOCK_FAIL);
        } finally {
            lock.unlock();
        }
    }
}
