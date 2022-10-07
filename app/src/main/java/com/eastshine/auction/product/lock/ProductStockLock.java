package com.eastshine.auction.product.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Component
public class ProductStockLock {

    private static final String GET_LOCK = "SELECT GET_LOCK(?, ?)";
    private static final String RELEASE_LOCK = "SELECT RELEASE_LOCK(?)";
    private static final String LOCK_PREFIX = "product_stock_";
    private static final String EXCEPTION_MESSAGE = "LOCK 을 수행하는 중에 오류가 발생하였습니다.";

    private final DataSource dataSource;

    public ProductStockLock(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void executeWithLock(String productLockName,
                                 int timeoutSeconds,
                                 Runnable runnable) {

        try (Connection connection = dataSource.getConnection()) {
            try {
                log.info("start getLock=[], timeoutSeconds ], connection=[]", LOCK_PREFIX + productLockName, timeoutSeconds, connection);
                getLock(connection, LOCK_PREFIX + productLockName, timeoutSeconds);
                log.info("success getLock=[], timeoutSeconds ], connection=[]", LOCK_PREFIX + productLockName, timeoutSeconds, connection);
                runnable.run();

            } finally {
                log.info("start releaseLock=[], connection=[]", LOCK_PREFIX + productLockName, connection);
                releaseLock(connection, LOCK_PREFIX + productLockName);
                log.info("success releaseLock=[], connection=[]", LOCK_PREFIX + productLockName, connection);
            }
        } catch (SQLException | RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void getLock(Connection connection,
                         String productLockName,
                         int timeoutSeconds) throws SQLException {

        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_LOCK)) {
            preparedStatement.setString(1, productLockName);
            preparedStatement.setInt(2, timeoutSeconds);

            checkResultSet(productLockName, preparedStatement, "GetLock_");
        }
    }

    private void releaseLock(Connection connection,
                             String productLockName) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(RELEASE_LOCK)) {
            preparedStatement.setString(1, productLockName);

            checkResultSet(productLockName, preparedStatement, "ReleaseLock_");
        }
    }

    private void checkResultSet(String productLockName,
                                PreparedStatement preparedStatement,
                                String type) throws SQLException {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (!resultSet.next()) {
                log.error("PRODUCT STOCK LOCK 쿼리 결과 값이 없습니다. type = [], productLockName ], connection=[]", type, productLockName, preparedStatement.getConnection());
                throw new RuntimeException(EXCEPTION_MESSAGE);
            }
            int result = resultSet.getInt(1);
            if (result != 1) {
                log.error("PRODUCT STOCK LOCK 쿼리 결과 값이 1이 아닙니다. type = [], result ] productLockName ], connection=[]", type, result, productLockName, preparedStatement.getConnection());
                throw new RuntimeException(EXCEPTION_MESSAGE);
            }
        }
    }
}
