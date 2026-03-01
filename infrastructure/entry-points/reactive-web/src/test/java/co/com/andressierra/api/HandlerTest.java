package co.com.andressierra.api;

import co.com.andressierra.api.rest.request.CreateCardRequest;
import co.com.andressierra.api.rest.request.EnrollCardRequest;
import co.com.andressierra.model.card.Card;
import co.com.andressierra.model.card.enums.CardTypeEnum;
import co.com.andressierra.model.exception.BusinessException;
import co.com.andressierra.usecase.createcard.CreateCardUseCase;
import co.com.andressierra.usecase.deletecard.DeleteCardUseCase;
import co.com.andressierra.usecase.enrollcard.EnrollCardUseCase;
import co.com.andressierra.usecase.getcard.GetCardUseCase;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class HandlerTest {

    @Mock
    private CreateCardUseCase createCardUseCase;

    @Mock
    private EnrollCardUseCase enrollCardUseCase;

    @Mock
    private GetCardUseCase getCardUseCase;

    @Mock
    private DeleteCardUseCase deleteCardUseCase;

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

        StepVerifier.create(handler.createCard(serverRequest))
                .assertNext(res -> assertEquals(201, res.statusCode().value()))
                .verifyComplete();

        verify(createCardUseCase).create(any());
    }

    @Test
    void shouldEnrollCard() {
        Card enrolledCard = card.toBuilder().status("ENROLLED").build();
        EnrollCardRequest request = new EnrollCardRequest(42);

        when(enrollCardUseCase.enroll(any())).thenReturn(Mono.just(enrolledCard));

        MockServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("identifier", "a3f7b2c1e9d04f58")
                .body(Mono.just(request));

        StepVerifier.create(handler.enrollCard(serverRequest))
                .assertNext(res -> assertEquals(200, res.statusCode().value()))
                .verifyComplete();

        verify(enrollCardUseCase).enroll(any());
    }

    @Test
    void shouldReturn404WhenCardNotFoundOnEnroll() {
        EnrollCardRequest request = new EnrollCardRequest(42);

        when(enrollCardUseCase.enroll(any()))
                .thenReturn(Mono.error(new BusinessException("Tarjeta no existe", "01", 404)));

        MockServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("identifier", "nonexistent12345")
                .body(Mono.just(request));

        StepVerifier.create(handler.enrollCard(serverRequest))
                .assertNext(res -> assertEquals(404, res.statusCode().value()))
                .verifyComplete();
    }

    @Test
    void shouldReturn400WhenInvalidValidationNumber() {
        EnrollCardRequest request = new EnrollCardRequest(99);

        when(enrollCardUseCase.enroll(any()))
                .thenReturn(Mono.error(new BusinessException("Numero de validacion invalido", "02", 400)));

        MockServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("identifier", "a3f7b2c1e9d04f58")
                .body(Mono.just(request));

        StepVerifier.create(handler.enrollCard(serverRequest))
                .assertNext(res -> assertEquals(400, res.statusCode().value()))
                .verifyComplete();
    }

    @Test
    void shouldGetCard() {
        when(getCardUseCase.getByIdentifier("a3f7b2c1e9d04f58")).thenReturn(Mono.just(card));

        MockServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("identifier", "a3f7b2c1e9d04f58")
                .build();

        StepVerifier.create(handler.getCard(serverRequest))
                .assertNext(res -> assertEquals(200, res.statusCode().value()))
                .verifyComplete();

        verify(getCardUseCase).getByIdentifier("a3f7b2c1e9d04f58");
    }

    @Test
    void shouldReturn404WhenCardNotFoundOnGet() {
        when(getCardUseCase.getByIdentifier("nonexistent12345"))
                .thenReturn(Mono.error(new BusinessException("Tarjeta no existe", "01", 404)));

        MockServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("identifier", "nonexistent12345")
                .build();

        StepVerifier.create(handler.getCard(serverRequest))
                .assertNext(res -> assertEquals(404, res.statusCode().value()))
                .verifyComplete();
    }

    @Test
    void shouldDeleteCard() {
        Card inactiveCard = card.toBuilder().status("INACTIVE").build();
        when(deleteCardUseCase.delete("a3f7b2c1e9d04f58")).thenReturn(Mono.just(inactiveCard));

        MockServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("identifier", "a3f7b2c1e9d04f58")
                .build();

        StepVerifier.create(handler.deleteCard(serverRequest))
                .assertNext(res -> assertEquals(200, res.statusCode().value()))
                .verifyComplete();

        verify(deleteCardUseCase).delete("a3f7b2c1e9d04f58");
    }

    @Test
    void shouldReturn404WhenCardNotFoundOnDelete() {
        when(deleteCardUseCase.delete("nonexistent12345"))
                .thenReturn(Mono.error(new BusinessException("Tarjeta no existe", "01", 404)));

        MockServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("identifier", "nonexistent12345")
                .build();

        StepVerifier.create(handler.deleteCard(serverRequest))
                .assertNext(res -> assertEquals(404, res.statusCode().value()))
                .verifyComplete();
    }
}