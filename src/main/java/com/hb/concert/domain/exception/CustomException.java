package com.hb.concert.domain.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

@Getter @Setter
public class CustomException extends ResponseStatusException {

    private String method;
    private String className;

    public CustomException(HttpStatusCode status, String message) {
        super(status);
    }

    public static class NotFoundException extends CustomException {
        public NotFoundException(String message) {
            super(HttpStatus.NOT_FOUND, message);
        }

        public static final String USER_NOT_FOUND = "유저를 찾을 수 없습니다.";

        public static final String RESERVATION_NOT_FOUND = "해당 예약을 찾을 수 없습니다.";

        public static final String CONCERT_NOT_FOUND = "해당 콘서트 정보를 찾을 수 없습니다.";
    }

    public static class BadRequestException extends CustomException {
        public BadRequestException(String message) {
            super(HttpStatus.BAD_REQUEST, message);
        }

        public static final String TOKEN_UNAUTHORIZED = "토큰이 유효하지 않습니다.";

        public static final String PAYMENT_REQUEST_TIMEOUT = "결제 요청시간이 초과하였습니다. 처음부터 다시 진행해 주세요.";

        public static final String PAYMENT_NOTENOUPH_AMOUNT = "금액이 부족합니다. 충전 후 이용해 주세요.";
    }

    public static class QueueException extends CustomException {
        public QueueException(String message) {
            super(HttpStatus.BAD_REQUEST, message);
        }

        public static final String TOKEN_NOT_IN_QUEUE = "대기열에 대기중인 토큰이 아닙니다.";

        public static final String TOKEN_NOT_POSITION_ZERO = "대기열 순번이 0이 야닙니다.";
    }
}