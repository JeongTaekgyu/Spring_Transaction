package com.example.springtransaction.order;

public class NotEnoughMoneyException extends Exception{

    // 결제 잔고가 부족하면 발생하는 비즈니스 예외이다. Exception 을 상속 받아서 체크 예외가 된다
    public NotEnoughMoneyException(String message) {
        super(message);
    }
}
