package com.hb.concert.domain.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter @Setter
public class CustomException extends ResponseStatusException {

    private String method;
    private String className;

    public CustomException(HttpStatus status, String message) {
        super(status, message);
    }

    public static class NotFoundException extends CustomException {
        public NotFoundException(String message) {
            super(HttpStatus.NOT_FOUND, message);
        }
        public static final String USER_NOT_FOUND = "유저를 찾을 수 없습니다.";
        public static final String RESERVATION_NOT_FOUND = "해당 예약을 찾을 수 없습니다.";
        public static final String CONCERT_NOT_FOUND = "해당 콘서트 정보를 찾을 수 없습니다.";
        public static final String PAYMENT_INFO_NOT_FOUND = "해당 예약번호로 찾을 수 있는 결제정보가 없습니다.";
        public static final String CONCERT_SCHEDULE_NOT_FOUND = "해당하는 콘서트 일정이 없습니다.";
    }

    public static class BadRequestException extends CustomException {
        public BadRequestException(String message) {
            super(HttpStatus.BAD_REQUEST, message);
        }

        public static final String PAYMENT_REQUEST_TIMEOUT = "결제 요청시간이 초과하였습니다. 처음부터 다시 진행해 주세요.";
        public static final String ALREADY_RESERVED = "해당 좌석은 이미 예약되었습니다.";
        public static final String PAYMENT_NOT_ENOUGH_AMOUNT = "금액이 부족합니다. 충전 후 이용해 주세요.";
        public static final String NOT_AVILABLE_CHARGE_AMOUNT = "충전 금액이 정상적이지 않습니다. 다시 시도해주세요.";
        public static final String SEAT_SOLDOUT_IN_CONCERT = "해당 콘서트는 좌석이 모두 매진되었습니다.";
        public static final String ANY_SEAT_RESERVE_ALREADY = "이미 예약된 좌석이 포함되어 있습니다.";
    }

    public static class InvalidServerException extends CustomException {
        public InvalidServerException(String message) {
            super(HttpStatus.NOT_ACCEPTABLE, message);
        }
        public static final String NOT_SELECTED_SEAT = "현재 해당 좌석을 예약할 수 없습니다. 다시 시도해 주세요.";
        public static final String NOT_DEDUCT_BALANCE = "현재 잔액을 차감할 수 없습니다. 다시 시도해 주세요.";
    }

    public static class QueueTokenException extends CustomException {
        public QueueTokenException(String message) {
            super(HttpStatus.BAD_REQUEST, message);
        }
        public static final String TOKEN_NOT_IN_QUEUE = "대기열에 대기중인 토큰이 아닙니다.";
        public static final String TOKEN_NOT_POSITION_ZERO = "대기 순번이 0이 아닙니다.";
        public static final String TOKEN_UNAUTHORIZED = "토큰이 유효하지 않습니다.";
        public static final String TOKEN_EXPIREDED = "만료된 토큰입니다. 처음부터 다시 시도해 주세요.";
    }
}
