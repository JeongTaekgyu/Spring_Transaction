package com.example.springtransaction.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@SpringBootTest
public class TxLevelTest {

    @Autowired LevelService service;

    @Test
    void orderTest() {
        service.write();
        service.read();
    }

    @TestConfiguration
    static class TxApplyLevelConfig {
        @Bean
        LevelService levelService() {
            return new LevelService();
        }
    }

    @Slf4j
    @Transactional(readOnly = true) // @Transactional은 쓰기와 읽기를 둘다 할 수 있는데 readOnly = true 이면 읽기 전용 트랜잭션이다.
    static class LevelService {

        // 클래스 보다는 메서드가 더 구체적이므로 메서드에 있는 @Transactional(readOnly = false) 옵션을 사용한 트랜잭션이 적용된다
        // 참고로 readOnly=false 는 기본 옵션이기 때문에 보통 생략한다. (default가 false 이기 때문에 false도 회색으로 되어있다.)
        @Transactional(readOnly = false) // readOnly = false 이면 write와 read 둘 다 가능하다.
        public void write() {
            log.info("call write");
            printTxInfo();
        }

        // LevelService 클래스에 @Transactional(readOnly = true) 이 적용되어 있다. 따라서 트랜잭션이 적용되고 readOnly = true 옵션을 사용하게 된다
        public void read() {
            log.info("call read");
            printTxInfo();
        }

        private void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive(); // 트랜잭션 여부를 확인할 수 있다.
            log.info("tx active={}", txActive);
            boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly(); // 현재 트랜잭션에 적용된 readOnly 옵션의 값을 반환한다
            log.info("tx readOnly={}", readOnly);
        }
    }
}
