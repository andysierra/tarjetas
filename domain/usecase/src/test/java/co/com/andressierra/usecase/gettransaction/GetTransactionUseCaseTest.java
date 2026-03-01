package co.com.andressierra.usecase.gettransaction;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTransactionUseCaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    private GetTransactionUseCase getTransactionUseCase;

    @BeforeEach
    void setUp() {
        getTransactionUseCase = new GetTransactionUseCase(transactionRepository);
    }

    private Transaction buildTransaction() {
        return Transaction.builder()
                .id(1L)
                .cardId(1L)
                .reference("123456")
                .validationNumber(42)
                .totalAmount(new BigDecimal("50000.00"))
                .address("Calle 123")
                .status(TransactionStatusEnum.APPROVED)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldReturnTransactionWhenFound() {
        Transaction transaction = buildTransaction();
        when(transactionRepository.findByReference("123456")).thenReturn(Mono.just(transaction));

        StepVerifier.create(getTransactionUseCase.getByReference("123456"))
                .assertNext(result -> {
                    assertEquals("123456", result.getReference());
                    assertEquals(new BigDecimal("50000.00"), result.getTotalAmount());
                    assertEquals("Calle 123", result.getAddress());
                    assertEquals(TransactionStatusEnum.APPROVED, result.getStatus());
                })
                .verifyComplete();

        verify(transactionRepository).findByReference("123456");
    }

    @Test
    void shouldFailWhenTransactionNotFound() {
        when(transactionRepository.findByReference("999999")).thenReturn(Mono.empty());

        StepVerifier.create(getTransactionUseCase.getByReference("999999"))
                .expectErrorSatisfies(err -> {
                    assertInstanceOf(BusinessException.class, err);
                    assertEquals("Numero de referencia invalido", err.getMessage());
                })
                .verify();
    }
}