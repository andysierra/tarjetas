package co.com.andressierra.usecase.createtransaction;

import co.com.andressierra.model.card.Card;
import co.com.andressierra.model.card.enums.CardStatusEnum;
import co.com.andressierra.model.card.enums.CardTypeEnum;
import co.com.andressierra.model.card.gateways.CardRepository;
import co.com.andressierra.model.exception.BusinessException;
import co.com.andressierra.model.transaction.Transaction;
import co.com.andressierra.model.transaction.enums.TransactionStatusEnum;
import co.com.andressierra.model.transaction.gateways.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTransactionUseCaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CardRepository cardRepository;

    private CreateTransactionUseCase createTransactionUseCase;

    @BeforeEach
    void setUp() {
        createTransactionUseCase = new CreateTransactionUseCase(transactionRepository, cardRepository);
    }

    private Card buildEnrolledCard() {
        return Card.builder()
                .id(1L)
                .pan("4567890123456789")
                .cardholderName("Test User")
                .cardholderId("1234567890")
                .cardType(CardTypeEnum.CREDIT)
                .phoneNumber("3001234567")
                .validationNumber(42)
                .identifier("a3f7b2c1e9d04f58")
                .status(CardStatusEnum.ENROLLED)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private CreateTransactionCommand buildCommand() {
        return CreateTransactionCommand.builder()
                .identifier("a3f7b2c1e9d04f58")
                .reference("123456")
                .totalAmount(new BigDecimal("50000.00"))
                .address("Calle 123")
                .build();
    }

    @Test
    void create_shouldCreateTransactionWhenCardIsEnrolled() {
        Card card = buildEnrolledCard();
        when(cardRepository.findByIdentifier("a3f7b2c1e9d04f58")).thenReturn(Mono.just(card));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(createTransactionUseCase.create(buildCommand()))
                .assertNext(tx -> {
                    assertEquals(1L, tx.getCardId());
                    assertEquals("123456", tx.getReference());
                    assertEquals(new BigDecimal("50000.00"), tx.getTotalAmount());
                    assertEquals("Calle 123", tx.getAddress());
                    assertEquals(TransactionStatusEnum.APPROVED, tx.getStatus());
                    assertEquals(42, tx.getValidationNumber());
                    assertNotNull(tx.getCreatedAt());
                })
                .verifyComplete();

        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void create_shouldFailWhenCardNotFound() {
        when(cardRepository.findByIdentifier("a3f7b2c1e9d04f58")).thenReturn(Mono.empty());

        StepVerifier.create(createTransactionUseCase.create(buildCommand()))
                .expectErrorSatisfies(err -> {
                    assertInstanceOf(BusinessException.class, err);
                    assertEquals("Tarjeta no existe", err.getMessage());
                })
                .verify();
    }

    @Test
    void create_shouldFailWhenCardNotEnrolled() {
        Card createdCard = buildEnrolledCard().toBuilder().status(CardStatusEnum.CREATED).build();
        when(cardRepository.findByIdentifier("a3f7b2c1e9d04f58")).thenReturn(Mono.just(createdCard));

        StepVerifier.create(createTransactionUseCase.create(buildCommand()))
                .expectErrorSatisfies(err -> {
                    assertInstanceOf(BusinessException.class, err);
                    assertEquals("Tarjeta no enrolada", err.getMessage());
                })
                .verify();
    }

    @Test
    void create_shouldFailWhenCardIsInactive() {
        Card inactiveCard = buildEnrolledCard().toBuilder().status(CardStatusEnum.INACTIVE).build();
        when(cardRepository.findByIdentifier("a3f7b2c1e9d04f58")).thenReturn(Mono.just(inactiveCard));

        StepVerifier.create(createTransactionUseCase.create(buildCommand()))
                .expectErrorSatisfies(err -> {
                    assertInstanceOf(BusinessException.class, err);
                    assertEquals("Tarjeta no enrolada", err.getMessage());
                })
                .verify();
    }
}