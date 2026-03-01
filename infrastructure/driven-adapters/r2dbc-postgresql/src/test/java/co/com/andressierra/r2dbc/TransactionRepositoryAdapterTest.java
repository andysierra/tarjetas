package co.com.andressierra.r2dbc;

import co.com.andressierra.model.exception.BusinessException;
import co.com.andressierra.model.transaction.Transaction;
import co.com.andressierra.model.transaction.enums.TransactionStatusEnum;
import co.com.andressierra.r2dbc.entity.TransactionEntity;
import co.com.andressierra.r2dbc.reactiveRepository.TransactionReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionRepositoryAdapterTest {

    @Mock
    private TransactionReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    private TransactionRepositoryAdapter adapter;

    private Transaction transaction;
    private TransactionEntity entity;

    @BeforeEach
    void setUp() {
        adapter = new TransactionRepositoryAdapter(repository, mapper);

        transaction = Transaction.builder()
                .cardId(1L)
                .reference("123456")
                .validationNumber(42)
                .totalAmount(new BigDecimal("50000.00"))
                .address("Calle 123")
                .status(TransactionStatusEnum.APPROVED)
                .createdAt(LocalDateTime.now())
                .build();

        entity = TransactionEntity.builder()
                .cardId(1L)
                .reference("123456")
                .validationNumber(42)
                .totalAmount(new BigDecimal("50000.00"))
                .address("Calle 123")
                .status("APPROVED")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldSaveTransaction() {
        when(mapper.map(transaction, TransactionEntity.class)).thenReturn(entity);
        when(repository.save(any(TransactionEntity.class))).thenReturn(Mono.just(entity));
        when(mapper.map(entity, Transaction.class)).thenReturn(transaction);

        StepVerifier.create(adapter.save(transaction))
                .assertNext(saved -> {
                    assertEquals("123456", saved.getReference());
                    assertEquals(TransactionStatusEnum.APPROVED, saved.getStatus());
                })
                .verifyComplete();

        verify(repository).save(any(TransactionEntity.class));
    }

    @Test
    void shouldMapDataIntegrityViolationToBusinessException() {
        when(mapper.map(transaction, TransactionEntity.class)).thenReturn(entity);
        when(repository.save(any(TransactionEntity.class)))
                .thenReturn(Mono.error(new DataIntegrityViolationException("duplicate key")));

        StepVerifier.create(adapter.save(transaction))
                .expectErrorSatisfies(err -> {
                    assertInstanceOf(BusinessException.class, err);
                    assertEquals("El numero de referencia ya existe", err.getMessage());
                })
                .verify();
    }

    @Test
    void shouldMapUnknownErrorToPersistenceException() {
        when(mapper.map(transaction, TransactionEntity.class)).thenReturn(entity);
        when(repository.save(any(TransactionEntity.class)))
                .thenReturn(Mono.error(new RuntimeException("connection lost")));

        StepVerifier.create(adapter.save(transaction))
                .expectErrorSatisfies(err -> {
                    assertInstanceOf(BusinessException.class, err);
                    assertEquals("Hubo un error desconocido en persistencia", err.getMessage());
                })
                .verify();
    }

    @Test
    void shouldFindByReference() {
        when(repository.findByReference("123456")).thenReturn(Mono.just(entity));
        when(mapper.map(entity, Transaction.class)).thenReturn(transaction);

        StepVerifier.create(adapter.findByReference("123456"))
                .assertNext(found -> {
                    assertEquals("123456", found.getReference());
                    assertEquals(1L, found.getCardId());
                })
                .verifyComplete();

        verify(repository).findByReference("123456");
    }

    @Test
    void shouldReturnEmptyWhenReferenceNotFound() {
        when(repository.findByReference("999999")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findByReference("999999"))
                .verifyComplete();
    }
}