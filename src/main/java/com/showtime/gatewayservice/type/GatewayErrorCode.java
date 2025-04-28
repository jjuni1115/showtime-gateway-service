package com.showtime.gatewayservice.type;

import com.showtime.coreapi.type.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum GatewayErrorCode implements ErrorCode {

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "401001","인증되지 않은 사용자입니다."),
    TOKEN_EXPIRED_EXCEPTION(HttpStatus.UNAUTHORIZED, "401002","만료된 토큰 입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"500001","서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;


    private static final Map<String,ErrorCode> CODE_MAP = Arrays.stream(values()).collect(Collectors.toMap(ErrorCode::getCode, e -> e));

    public static ErrorCode fromCode(String code){
        return CODE_MAP.get(code);
    }


}
