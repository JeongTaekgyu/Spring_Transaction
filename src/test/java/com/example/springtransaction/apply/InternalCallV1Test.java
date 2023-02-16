package com.example.springtransaction.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@SpringBootTest
public class InternalCallV1Test {

    @Autowired CallService callService; // callService 빈을 주입 받으면 트랜잭션 프록시 객체가 대신 주입된다

    @Test
    void printProxy() {
        log.info("callService class={}", callService.getClass());
        // 여기서는 테스트에서 callService 를 주입 받는데, 해당 클래스를 출력해보면 뒤에 CGLIB...이 붙은 것을 확인할 수 있다.
        // 원본 객체 대신에 트랜잭션을 처리하는 프록시 객체를 주입 받은 것이다.
    }

    @Test
    void internalCall() {
        callService.internal();
    }

    @Test
    void externalCall() {
        callService.external();
    }

    @TestConfiguration
    static class InternalCallV1TestConfig {
        @Bean
        CallService callService() {
            return new CallService();
        }
    }

    static class CallService {

        public void external(){
            log.info("call external");
            printTxInfo();
            log.info("-----------------------");
            internal(); // 자바에서는 앞에 생략하면 this.internal(); 이렇게 this가 default이다.
            // internal()이 호출하는 클래스 내부에 있으니까 @Transactional 있어도 결과적을 이러한 내부 호출은
            // 프록시를 거치지 않는다.
        }

        @Transactional
        public void internal() {
            log.info("call internal");
            printTxInfo();
        }

        private void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive(); // 트랜잭션 여부를 확인할 수 있다.
            log.info("tx active={}", txActive);
//            boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly(); // 현재 트랜잭션에 적용된 readOnly 옵션의 값을 반환한다
//            log.info("tx readOnly={}", readOnly);
        }
    }
}
