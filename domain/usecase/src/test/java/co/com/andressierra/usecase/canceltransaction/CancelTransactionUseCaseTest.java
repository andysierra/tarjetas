package co.com.andressierra.usecase.canceltransaction;

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
class CancelTransactionUseCaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    private CancelTransactionUseCase cancelTransactionUseCase;

    @BeforeEach
    void setUp() {
        cancelTransactionUseCase = new CancelTransactionUseCase(transactionRepository);
    }

    private Transaction buildTransaction(LocalDateTime createdAt) {
        return Transaction.builder()
                .id(1L)
                .cardId(1L)
                .reference("123456")
                .validationNumber(42)
                .totalAmount(new BigDecimal("50000.00"))
                .address("Calle 123")
                .status(TransactionStatusEnum.APPROVED)
                .createdAt(createdAt)
                .build();
    }

    @Test
    void shouldCancelTransactionWithinWindow() {
        Transaction transaction = buildTransaction(LocalDateTime.now().minusMinutes(2));
        Transaction cancelled = transaction.toBuilder().status(TransactionStatusEnum.CANCELLED).build();

        when(transactionRepository.findByReference("123456")).thenReturn(Mono.just(transaction));
        when(transactionRepository.save(any())).thenReturn(Mono.just(cancelled));

        StepVerifier.create(cancelTransactionUseCase.cancel("123456"))
                .assertNext(result -> {
                    assertEquals(TransactionStatusEnum.CANCELLED, result.getStatus());
                    assertEquals("123456", result.getReference());
                })
                .verifyComplete();

        verify(transactionRepository).findByReference("123456");
        verify(transactionRepository).save(any());
    }

    @Test
    void shouldFailWhenTransactionNotFound() {
        when(transactionRepository.findByReference("999999")).thenReturn(Mono.empty());

        StepVerifier.create(cancelTransactionUseCase.cancel("999999"))
                .expectErrorSatisfies(err -> {
                    assertInstanceOf(BusinessException.class, err);
                    assertEquals("Numero de referencia invalido", err.getMessage());
                })
                .verify();
    }

    @Test
    void shouldFailWhenTransactionOlderThan5Minutes() {
        Transaction transaction = buildTransaction(LocalDateTime.now().minusMinutes(10));

        when(transactionRepository.findByReference("123456")).thenReturn(Mono.just(transaction));

        StepVerifier.create(cancelTransactionUseCase.cancel("123456"))
                .expectErrorSatisfies(err -> {
                    assertInstanceOf(BusinessException.class, err);
                    assertEquals("No se puede anular transaccion por su antiguedad", err.getMessage());
                })
                .verify();
    }

    @Test
    void shouldCancelTransactionAtExactly5Minutes() {
        Transaction transaction = buildTransaction(LocalDateTime.now().minusMinutes(4).minusSeconds(50));
        Transaction cancelled = transaction.toBuilder().status(TransactionStatusEnum.CANCELLED).build();

        when(transactionRepository.findByReference("123456")).thenReturn(Mono.just(transaction));
        when(transactionRepository.save(any())).thenReturn(Mono.just(cancelled));

        StepVerifier.create(cancelTransactionUseCase.cancel("123456"))
                .assertNext(result -> assertEquals(TransactionStatusEnum.CANCELLED, result.getStatus()))
                .verifyComplete();
    }
}