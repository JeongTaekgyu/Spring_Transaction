package com.example.springtransaction.order;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue
    private Long id;
    private String username; //정상, 예외, 잔고부족
    private String payStatus; //대기, 완료 (원래는 Enum 사용하는게 맞는데 예쩨니까 그냥 String 사용했음)
}
