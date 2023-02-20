package com.example.springtransaction.exception;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class RollbackTest {

    @Autowired
    RollbackService service;
    
    @Test
    void runtimeException() {
//        service.runtimeException();
        assertThatThrownBy(() -> service.runtimeException())
                .isInstanceOf(RuntimeException.class);
        // RuntimeException (Unchecked Exception)이 발생하므로 트랜잭션이 롤백된다
    }

    @Test
    void checkedException() {
        assertThatThrownBy(() -> service.checkedException())
                .isInstanceOf(MyException.class);
        // MyException 은 Exception 을 상속받은 체크 예외(Checked Exception)이다. 따라서 예외가 발생해도 트랜잭션이 커밋된다.
    }

    @Test
    void rollbackFor() {
        assertThatThrownBy(() -> service.rollbackFor())
                .isInstanceOf(MyException.class);
    }

    @TestConfiguration
    static class RollbackTestConfig {
        @Bean
        RollbackService rollbackService() {
            return new RollbackService();
        }
    }

    @Slf4j
    static class RollbackService {
        // 런타임 예외 발생 : -> 롤백
        @Transactional
        public void runtimeException() {
            log.info("call runtimeException");
            throw new RuntimeException(); // runtimeException의 자식들?은 다 rollback된다.
        }
        // 체크 예외 발생 : -> 커밋
        @Transactional
        public void checkedException() throws MyException {
            log.info("call checkedException");
            throw new MyException();
        }

        // 체크 예외 rollbackFor 지정 : -> 롤백
        @Transactional(rollbackFor = MyException.class) // 이 옵션을 사용하면 기본 정책에 추가로 어떤 예외가 발생할 때 롤백할 지 지정할 수 있다
        // 예를 들어서 이렇게 지정하면 체크 예외인 Exception 이 발생해도 커밋 대신 롤백된다. (자식 타입도 롤백된다.)
        public void rollbackFor() throws MyException {
            log.info("call rollbackFor");
            throw new MyException();
            // 기본 정책과 무관하게 특정 예외를 강제로 롤백하고 싶으면 rollbackFor 를 사용하면 된다. (해당 예외의 자식도 포함된다.)
            // rollbackFor = MyException.class 을 지정했기 때문에 MyException 이 발생하면 체크 예외이지만 트랜잭션이 롤백된다
        }
    }

    static class MyException extends Exception {
    }
}
