package co.com.andressierra.api;

import co.com.andressierra.api.rest.request.CreateCardRequest;
import co.com.andressierra.api.rest.request.CreateTransactionRequest;
import co.com.andressierra.api.rest.request.EnrollCardRequest;
import co.com.andressierra.model.card.Card;
import co.com.andressierra.model.card.enums.CardStatusEnum;
import co.com.andressierra.model.card.enums.CardTypeEnum;
import co.com.andressierra.model.exception.BusinessException;
import co.com.andressierra.model.transaction.Transaction;
import co.com.andressierra.model.transaction.enums.TransactionStatusEnum;
import co.com.andressierra.usecase.canceltransaction.CancelTransactionUseCase;
import co.com.andressierra.usecase.createcard.CreateCardUseCase;
import co.com.andressierra.usecase.createtransaction.CreateTransactionUseCase;
import co.com.andressierra.usecase.deletecard.DeleteCardUseCase;
import co.com.andressierra.usecase.enrollcard.EnrollCardUseCase;
import co.com.andressierra.usecase.getcard.GetCardUseCase;
import co.com.andressierra.usecase.gettransaction.GetTransactionUseCase;
import java.math.BigDecimal;
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

    @Mock
    private CreateTransactionUseCase createTransactionUseCase;

    @Mock
    private GetTransactionUseCase getTransactionUseCase;

    @Mock
    private CancelTransactionUseCase cancelTransactionUseCase;

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
                .status(CardStatusEnum.CREATED)
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
        Card enrolledCard = card.toBuilder().status(CardStatusEnum.ENROLLED).build();
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
                .thenReturn(Mono.error(new BusinessException("Tarjeta no existe", "41", 404)));

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
                .thenReturn(Mono.error(new BusinessException("Numero de validacion invalido", "45", 400)));

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
                .thenReturn(Mono.error(new BusinessException("Tarjeta no existe", "41", 404)));

        MockServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("identifier", "nonexistent12345")
                .build();

        StepVerifier.create(handler.getCard(serverRequest))
                .assertNext(res -> assertEquals(404, res.statusCode().value()))
                .verifyComplete();
    }

    @Test
    void shouldDeleteCard() {
        Card inactiveCard = card.toBuilder().status(CardStatusEnum.INACTIVE).build();
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
                .thenReturn(Mono.error(new BusinessException("Tarjeta no existe", "41", 404)));

        MockServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("identifier", "nonexistent12345")
                .build();

        StepVerifier.create(handler.deleteCard(serverRequest))
                .assertNext(res -> assertEquals(404, res.statusCode().value()))
                .verifyComplete();
    }

    @Test
    void shouldCreateTransaction() {
        Transaction transaction = Transaction.builder()
                .cardId(1L)
                .reference("123456")
                .totalAmount(new BigDecimal("50000.00"))
                .address("Calle 123")
                .status(TransactionStatusEnum.APPROVED)
                .createdAt(LocalDateTime.now())
                .build();

        CreateTransactionRequest request = new CreateTransactionRequest(
                "a3f7b2c1e9d04f58", "123456", new BigDecimal("50000.00"), "Calle 123");

        when(createTransactionUseCase.create(any())).thenReturn(Mono.just(transaction));

        MockServerRequest serverRequest = MockServerRequest.builder()
                .body(Mono.just(request));

        StepVerifier.create(handler.createTransaction(serverRequest))
                .assertNext(res -> assertEquals(201, res.statusCode().value()))
                .verifyComplete();

        verify(createTransactionUseCase).create(any());
    }

    @Test
    void shouldReturn404WhenCardNotFoundOnCreateTransaction() {
        CreateTransactionRequest request = new CreateTransactionRequest(
                "nonexistent12345", "123456", new BigDecimal("50000.00"), "Calle 123");

        when(createTransactionUseCase.create(any()))
                .thenReturn(Mono.error(new BusinessException("Tarjeta no existe", "41", 404)));

        MockServerRequest serverRequest = MockServerRequest.builder()
                .body(Mono.just(request));

        StepVerifier.create(handler.createTransaction(serverRequest))
                .assertNext(res -> assertEquals(404, res.statusCode().value()))
                .verifyComplete();
    }

    @Test
    void shouldReturn400WhenCardNotEnrolledOnCreateTransaction() {
        CreateTransactionRequest request = new CreateTransactionRequest(
                "a3f7b2c1e9d04f58", "123456", new BigDecimal("50000.00"), "Calle 123");

        when(createTransactionUseCase.create(any()))
                .thenReturn(Mono.error(new BusinessException("Tarjeta no enrolada", "42", 400)));

        MockServerRequest serverRequest = MockServerRequest.builder()
                .body(Mono.just(request));

        StepVerifier.create(handler.createTransaction(serverRequest))
                .assertNext(res -> assertEquals(400, res.statusCode().value()))
                .verifyComplete();
    }

    @Test
    void shouldGetTransaction() {
        Transaction transaction = Transaction.builder()
                .cardId(1L)
                .reference("123456")
                .totalAmount(new BigDecimal("50000.00"))
                .address("Calle 123")
                .status(TransactionStatusEnum.APPROVED)
                .createdAt(LocalDateTime.now())
                .build();

        when(getTransactionUseCase.getByReference("123456")).thenReturn(Mono.just(transaction));

        MockServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("reference", "123456")
                .build();

        StepVerifier.create(handler.getTransaction(serverRequest))
                .assertNext(res -> assertEquals(200, res.statusCode().value()))
                .verifyComplete();

        verify(getTransactionUseCase).getByReference("123456");
    }

    @Test
    void shouldReturn400WhenTransactionNotFound() {
        when(getTransactionUseCase.getByReference("999999"))
                .thenReturn(Mono.error(new BusinessException("Numero de referencia invalido", "43", 400)));

        MockServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("reference", "999999")
                .build();

        StepVerifier.create(handler.getTransaction(serverRequest))
                .assertNext(res -> assertEquals(400, res.statusCode().value()))
                .verifyComplete();
    }

    @Test
    void shouldCancelTransaction() {
        Transaction transaction = Transaction.builder()
                .cardId(1L)
                .reference("123456")
                .totalAmount(new BigDecimal("50000.00"))
                .address("Calle 123")
                .status(TransactionStatusEnum.CANCELLED)
                .createdAt(LocalDateTime.now())
                .build();

        when(cancelTransactionUseCase.cancel("123456")).thenReturn(Mono.just(transaction));

        MockServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("reference", "123456")
                .build();

        StepVerifier.create(handler.cancelTransaction(serverRequest))
                .assertNext(res -> assertEquals(200, res.statusCode().value()))
                .verifyComplete();

        verify(cancelTransactionUseCase).cancel("123456");
    }

    @Test
    void shouldReturn400WhenTransactionNotFoundOnCancel() {
        when(cancelTransactionUseCase.cancel("999999"))
                .thenReturn(Mono.error(new BusinessException("Numero de referencia invalido", "43", 400)));

        MockServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("reference", "999999")
                .build();

        StepVerifier.create(handler.cancelTransaction(serverRequest))
                .assertNext(res -> assertEquals(400, res.statusCode().value()))
                .verifyComplete();
    }

    @Test
    void shouldReturn400WhenTransactionTooOldToCancel() {
        when(cancelTransactionUseCase.cancel("123456"))
                .thenReturn(Mono.error(new BusinessException("No se puede anular transaccion por su antiguedad", "44", 400)));

        MockServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("reference", "123456")
                .build();

        StepVerifier.create(handler.cancelTransaction(serverRequest))
                .assertNext(res -> assertEquals(400, res.statusCode().value()))
                .verifyComplete();
    }
}