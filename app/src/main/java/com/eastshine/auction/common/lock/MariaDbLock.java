package com.eastshine.auction.common.lock;

import com.eastshine.auction.common.exception.BaseException;
import com.eastshine.auction.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Component
public class MariaDbLock {
    private static final String GET_LOCK = "SELECT GET_LOCK(?, ?)";
    private static final String RELEASE_LOCK = "SELECT RELEASE_LOCK(?)";

    private final DataSource dataSource;

    public MariaDbLock(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void executeWithLock(String lockName,
                                 int timeoutSeconds,
                                 Runnable runnable) {

    try (Connection connection = dataSource.getConnection()) {
        try {
                log.info("start getLock=[], timeoutSeconds ], connection=[]", lockName, timeoutSeconds, connection);
                getLock(connection, lockName, timeoutSeconds);
                log.info("success getLock=[], timeoutSeconds ], connection=[]", lockName, timeoutSeconds, connection);
                runnable.run();

        } finally {
                log.info("start releaseLock=[], connection=[]", lockName, connection);
                releaseLock(connection, lockName);
                log.info("success releaseLock=[], connection=[]", lockName, connection);
        }
    } catch (SQLException | RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void getLock(Connection connection,
                         String lockName,
                         int timeoutSeconds) throws SQLException {

        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_LOCK)) {
            preparedStatement.setString(1, lockName);
            preparedStatement.setInt(2, timeoutSeconds);

            checkResultSet(lockName, preparedStatement, "GetLock_");
        }
    }

    private void releaseLock(Connection connection,
                             String lockName) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(RELEASE_LOCK)) {
            preparedStatement.setString(1, lockName);

            checkResultSet(lockName, preparedStatement, "ReleaseLock_");
        }
    }

    private void checkResultSet(String lockName,
                                PreparedStatement preparedStatement,
                                String type) throws SQLException {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (!resultSet.next()) {
                log.error("MariaDB LOCK 쿼리 결과 값이 없습니다. type = [], lockName ], connection=[]", type, lockName, preparedStatement.getConnection());
                throw new BaseException(ErrorCode.COMMON_LOCK_FAIL);
            }
            int result = resultSet.getInt(1);
            if (result != 1) {
                log.error("MariaDB LOCK 쿼리 결과 값이 1이 아닙니다. type = [], result ] lockName ], connection=[]", type, result, lockName, preparedStatement.getConnection());
                throw new BaseException(ErrorCode.COMMON_LOCK_FAIL);
            }
        }
    }
}
