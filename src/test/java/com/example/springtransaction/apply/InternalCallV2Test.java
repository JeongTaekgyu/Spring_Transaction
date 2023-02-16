package com.example.springtransaction.apply;

import lombok.RequiredArgsConstructor;
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
public class InternalCallV2Test {

    @Autowired CallService callService;

    @Test
    void printProxy() {
        log.info("callService class={}", callService.getClass());
        // 여기서는 테스트에서 callService 를 주입 받는데, 해당 클래스를 출력해보면 뒤에 CGLIB...이 붙은 것을 확인할 수 있다.
        // 원본 객체 대신에 트랜잭션을 처리하는 프록시 객체를 주입 받은 것이다.
    }

    @Test
    void externalCallV2() {
        callService.external();
    }

    @TestConfiguration
    static class InternalCallV1TestConfig {
        @Bean
        CallService callService() {
            return new CallService(internalService());
        }

        @Bean
        InternalService internalService() {
            return new InternalService();
        }
    }

    @Slf4j
    @RequiredArgsConstructor
    static class CallService {
        // 반면 callService는 external()이 @Transactional 이 없어서 프록시를 안 만든다.

        // ★ internalService는 프록시이다.
        // ( 그 이유가 internalService.internal(); 호출하는데 internal()에 @Transactional 이 있어서 그렇다. )
        // 중요한건 internal()을 호출하는데 internal()이 외부에 있기 때문에 프록시를 거쳐서 트랜잭션을 적용할 수 있다.
        private final InternalService internalService;

        public void external(){
            log.info("call external");
            printTxInfo(); // 자바에서는 앞에 생략하면 this.printTxInfo(); 이렇게 this가 default이다.
            log.info("-----------------------");
            internalService.internal();
        }

        private void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive(); // 트랜잭션 여부를 확인할 수 있다.
            log.info("tx active={}", txActive);
        }
    }

    static class InternalService {
        @Transactional
        public void internal() {
            log.info("call internal");
            printTxInfo();
        }

        private void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive(); // 트랜잭션 여부를 확인할 수 있다.
            log.info("tx active={}", txActive);
        }
    }
}
