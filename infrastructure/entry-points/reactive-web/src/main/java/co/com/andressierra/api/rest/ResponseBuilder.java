package co.com.andressierra.api.rest;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseBuilder {
    private String code;
    private String message;
    private Object data;

    public static ResponseBuilder success(Object data, MessagesEnum messagesEnum) {
        return ResponseBuilder.builder()
                .code(messagesEnum.code)
                .message(messagesEnum.message)
                .data(data)
                .build();
    }

    public static ResponseBuilder success(String message, Object data) {
        return ResponseBuilder.builder()
                .code("00")
                .message(message)
                .data(data)
                .build();
    }
}
