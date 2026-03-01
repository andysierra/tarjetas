package co.com.andressierra.api;

import co.com.andressierra.api.mapper.Mapper;
import co.com.andressierra.api.rest.ResponseBuilder;
import co.com.andressierra.api.rest.request.CreateCardRequest;
import co.com.andressierra.api.rest.request.EnrollCardRequest;
import co.com.andressierra.model.messages.MessagesEnum;
import co.com.andressierra.usecase.createcard.CreateCardUseCase;
import co.com.andressierra.usecase.enrollcard.EnrollCardUseCase;
import co.com.andressierra.usecase.getcard.GetCardUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final CreateCardUseCase createCardUseCase;
    private final EnrollCardUseCase enrollCardUseCase;
    private final GetCardUseCase getCardUseCase;

    public Mono<ServerResponse> createCard(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CreateCardRequest.class)
                .flatMap(request -> createCardUseCase.create(Mapper.toCommand(request)))
                .flatMap(card -> ResponseBuilder.success(Mapper.toResponse(card), MessagesEnum.CARD_CREATED))
                .onErrorResume(ResponseBuilder::handleError);
    }

    public Mono<ServerResponse> enrollCard(ServerRequest serverRequest) {
        String identifier = serverRequest.pathVariable("identifier");
        return serverRequest.bodyToMono(EnrollCardRequest.class)
                .flatMap(request -> enrollCardUseCase.enroll(Mapper.toCommand(request, identifier)))
                .flatMap(card -> ResponseBuilder.success(Mapper.toEnrollResponse(card), MessagesEnum.CARD_ENROLLED))
                .onErrorResume(ResponseBuilder::handleError);
    }

    public Mono<ServerResponse> getCard(ServerRequest serverRequest) {
        String identifier = serverRequest.pathVariable("identifier");
        return getCardUseCase.getByIdentifier(identifier)
                .flatMap(card -> ResponseBuilder.success(Mapper.toGetCardResponse(card), MessagesEnum.CARD_FOUND))
                .onErrorResume(ResponseBuilder::handleError);
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