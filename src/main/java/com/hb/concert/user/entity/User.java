package com.hb.concert.user.entity;

import com.hb.concert.exception.CustomException.BadRequestException;
import com.hb.concert.user.entity.listener.UserEntityListener;
import com.hb.concert.support.CommonUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor @AllArgsConstructor
@Builder @Data
@Entity @Table(name = "HB_USER")
@EntityListeners(value = UserEntityListener.class)
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID userId;
    private Integer balance;

    @PrePersist protected void onCreate() {
        if (CommonUtil.isNull(this.balance)) {
            this.balance = 0;
        }
    }

    public void charge(int amount) {
        if (CommonUtil.isNonNull(amount) && amount != 0) {
            this.balance += amount;
        } else {
            throw new BadRequestException(BadRequestException.NOT_AVILABLE_CHARGE_AMOUNT);
        }
    }

    public void consume(int amount) {
        if (CommonUtil.isNonNull(this.balance) && this.balance >= amount) {
            this.balance -= amount;
        } else {
            throw new BadRequestException(BadRequestException.PAYMENT_NOT_ENOUGH_AMOUNT);
        }
    }
}
