package co.com.andressierra.api;

import co.com.andressierra.api.rest.request.CreateCardRequest;
import co.com.andressierra.model.card.Card;
import co.com.andressierra.model.card.enums.CardTypeEnum;
import co.com.andressierra.usecase.createcard.CreateCardUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HandlerTest {

    @Mock
    private CreateCardUseCase createCardUseCase;

    @InjectMocks
    private Handler handler;

    private Card card;

    @BeforeEach
    void setUp() {
        card = Card.builder()
                .pan("4567890123456789")
                .cardholderName("Test User")
                .cardholderId("1234567890")
                .cardType(CardTypeEnum.CREDIT)
                .phoneNumber("3001234567")
                .validationNumber(42)
                .identifier("a3f7b2c1e9d04f58")
                .status("CREATED")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldCreateCard() {
        CreateCardRequest request = new CreateCardRequest();
        request.setPan("4567890123456789");
        request.setCardholderName("Test User");
        request.setCardholderId("1234567890");
        request.setCardType(CardTypeEnum.CREDIT);
        request.setPhoneNumber("3001234567");

        when(createCardUseCase.create(any())).thenReturn(Mono.just(card));

        MockServerRequest serverRequest = MockServerRequest.builder()
                .body(Mono.just(request));

        Mono<ServerResponse> response = handler.createCard(serverRequest);

        StepVerifier.create(response)
                .assertNext(res -> assertEquals(201, res.statusCode().value()))
                .verifyComplete();

        verify(createCardUseCase).create(any());
    }
}
