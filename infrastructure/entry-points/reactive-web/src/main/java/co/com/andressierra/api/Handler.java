package co.com.andressierra.api;

import co.com.andressierra.api.mapper.Mapper;
import co.com.andressierra.api.rest.ResponseBuilder;
import co.com.andressierra.api.rest.request.CreateCardRequest;
import co.com.andressierra.api.rest.request.EnrollCardRequest;
import co.com.andressierra.model.messages.MessagesEnum;
import co.com.andressierra.usecase.createcard.CreateCardUseCase;
import co.com.andressierra.usecase.deletecard.DeleteCardUseCase;
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

    private static final String IDENTIFIER_PATH_VARIABLE = "identifier";
    private static final String REFERENCE_PATH_VARIABLE = "reference";

    private final CreateCardUseCase createCardUseCase;
    private final EnrollCardUseCase enrollCardUseCase;
    private final GetCardUseCase getCardUseCase;
    private final DeleteCardUseCase deleteCardUseCase;

    public Mono<ServerResponse> createCard(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CreateCardRequest.class)
                .flatMap(request -> createCardUseCase.create(Mapper.toCommand(request)))
                .flatMap(card -> ResponseBuilder.success(Mapper.toResponse(card), MessagesEnum.CARD_CREATED))
                .onErrorResume(ResponseBuilder::handleError);
    }

    public Mono<ServerResponse> enrollCard(ServerRequest serverRequest) {
        String identifier = serverRequest.pathVariable(IDENTIFIER_PATH_VARIABLE);
        return serverRequest.bodyToMono(EnrollCardRequest.class)
                .flatMap(request -> enrollCardUseCase.enroll(Mapper.toCommand(request, identifier)))
                .flatMap(card -> ResponseBuilder.success(Mapper.toEnrollResponse(card), MessagesEnum.CARD_ENROLLED))
                .onErrorResume(ResponseBuilder::handleError);
    }

    public Mono<ServerResponse> getCard(ServerRequest serverRequest) {
        String identifier = serverRequest.pathVariable(IDENTIFIER_PATH_VARIABLE);
        return getCardUseCase.getByIdentifier(identifier)
                .flatMap(card -> ResponseBuilder.success(Mapper.toGetCardResponse(card), MessagesEnum.CARD_FOUND))
                .onErrorResume(ResponseBuilder::handleError);
    }

    public Mono<ServerResponse> deleteCard(ServerRequest serverRequest) {
        String identifier = serverRequest.pathVariable(IDENTIFIER_PATH_VARIABLE);
        return deleteCardUseCase.delete(identifier)
                .flatMap(card -> ResponseBuilder.success(Mapper.toDeleteCardResponse(card), MessagesEnum.CARD_DELETED))
                .onErrorResume(ResponseBuilder::handleError);
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