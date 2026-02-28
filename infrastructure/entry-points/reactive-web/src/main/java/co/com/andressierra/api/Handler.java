package co.com.andressierra.api;

import co.com.andressierra.api.mapper.Mapper;
import co.com.andressierra.api.rest.ResponseBuilder;
import co.com.andressierra.api.rest.request.CreateCardRequest;
import co.com.andressierra.usecase.createcard.CreateCardUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final CreateCardUseCase createCardUseCase;

    public Mono<ServerResponse> createCard(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CreateCardRequest.class)
                .flatMap(request -> createCardUseCase.create(Mapper.toCommand(request)))
                .flatMap(card -> ResponseBuilder.success(Mapper.toResponse(card), MessagesEnum.CREATED_SUCCESS));
    }

    public Mono<ServerResponse> getCard(ServerRequest serverRequest) {
        return ServerResponse.ok().build();
    }

    public Mono<ServerResponse> enrollCard(ServerRequest serverRequest) {
        return ServerResponse.ok().build();
    }

    public Mono<ServerResponse> deleteCard(ServerRequest serverRequest) {
        return ServerResponse.ok().build();
    }

    public Mono<ServerResponse> createTransaction(ServerRequest serverRequest) {
        return ServerResponse.ok().build();
    }

    public Mono<ServerResponse> getTransaction(ServerRequest serverRequest) {
        return ServerResponse.ok().build();
    }

    public Mono<ServerResponse> cancelTransaction(ServerRequest serverRequest) {
        return ServerResponse.ok().build();
    }
}
