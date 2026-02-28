package co.com.andressierra.api.rest;

import co.com.andressierra.model.messages.MessagesEnum;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Getter
@Builder
public class ResponseBuilder {
    private String code;
    private String message;
    private Object data;

    public static Mono<ServerResponse> success(Object data, MessagesEnum messagesEnum) {
        ResponseBuilder body = ResponseBuilder.builder()
                .code(messagesEnum.getOperationCode())
                .message(messagesEnum.getMessage())
                .data(data)
                .build();
        return ServerResponse.status(HttpStatus.valueOf(messagesEnum.getCode()))
                .bodyValue(body);
    }
}