package com.example.springtransaction.order;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class OrderServiceTest {

    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    void order() throws NotEnoughMoneyException {
        // given
        Order order = new Order();
        order.setUsername("정상");

        // when
        orderService.order(order);

        // then
        Order findOrder = orderRepository.findById(order.getId()).get();
        assertThat(findOrder.getPayStatus()).isEqualTo("완료");
    }

    @Test
    void runtimeException() {
        //given
        Order order = new Order();
        order.setUsername("예외");
        //when, then
        assertThatThrownBy(() -> orderService.order(order))
                .isInstanceOf(RuntimeException.class);
        // 런타임 에러는 Unchecked Exception 이므로 롤백이 발생한다. 롤백이 되면 db에 insert쿼리 자체도 안남긴다.
        //then: 롤백되었으므로 데이터가 없어야 한다.
        Optional<Order> orderOptional =
                orderRepository.findById(order.getId());
        assertThat(orderOptional.isEmpty()).isTrue(); // 롤백 돼서 데이터가 비어있어야한다.
    }

    @Test
    void bizException() {
        //given
        Order order = new Order();
        order.setUsername("잔고부족");
        //when, then
        try {
            orderService.order(order);
        } catch (NotEnoughMoneyException e) {
            // NotEnoughMoneyException는 Exception을 상속받는 checked 에러이다. 그러므로 커밋이 된다.
            log.info("고객에게 잔고 부족을 알리고 별도의 계좌로 입금하도록 안내");
        }

        Order findOrder = orderRepository.findById(order.getId()).get();
        assertThat(findOrder.getPayStatus()).isEqualTo("대기");
    }
}