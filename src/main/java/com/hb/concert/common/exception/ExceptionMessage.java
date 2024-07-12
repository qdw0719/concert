package com.hb.concert.common.exception;

public interface ExceptionMessage {

    public String NOT_FOUND = "{msg} 을/를 찾을 수 없습니다.";

    public String BAD_REQUEST = "{msg} 이/가 잘못되었습니다.";

    public String UNAUTHORIZED = "토큰이 정상적이지 않습니다.";

    public String TOKEN_EXPIRED = "토큰이 만료되었습니다. 처음부터 다시 진행해주세요.";

    public String REQUEST_TIMEOUT = "{msg} 이/가 초과되었습니다.";
}
