package co.com.andressierra.r2dbc;

import co.com.andressierra.model.card.Card;
import co.com.andressierra.model.card.enums.CardTypeEnum;
import co.com.andressierra.model.exception.BusinessException;
import co.com.andressierra.r2dbc.entity.CardEntity;
import co.com.andressierra.r2dbc.reactiveRepository.MyReactiveRepository;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CardRepositoryAdapterTest {

    @Mock
    private MyReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    private CardRepositoryAdapter adapter;

    private Card card;
    private CardEntity entity;

    @BeforeEach
    void setUp() {
        adapter = new CardRepositoryAdapter(repository, mapper);

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

        entity = CardEntity.builder()
                .pan("4567890123456789")
                .cardholderName("Test User")
                .cardholderId("1234567890")
                .cardType("CREDIT")
                .phoneNumber("3001234567")
                .validationNumber(42)
                .identifier("a3f7b2c1e9d04f58")
                .status("CREATED")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldSaveCard() {
        when(mapper.map(card, CardEntity.class)).thenReturn(entity);
        when(repository.save(any(CardEntity.class))).thenReturn(Mono.just(entity));
        when(mapper.map(entity, Card.class)).thenReturn(card);

        StepVerifier.create(adapter.save(card))
                .assertNext(saved -> {
                    assertEquals("4567890123456789", saved.getPan());
                    assertEquals("a3f7b2c1e9d04f58", saved.getIdentifier());
                    assertEquals("CREATED", saved.getStatus());
                })
                .verifyComplete();

        verify(repository).save(any(CardEntity.class));
    }

    @Test
    void shouldMapDataIntegrityViolationToBusinessException() {
        when(mapper.map(card, CardEntity.class)).thenReturn(entity);
        when(repository.save(any(CardEntity.class)))
                .thenReturn(Mono.error(new DataIntegrityViolationException("duplicate key")));

        StepVerifier.create(adapter.save(card))
                .expectErrorSatisfies(err -> {
                    assertInstanceOf(BusinessException.class, err);
                    assertEquals("La tarjeta ya existe en el sistema", err.getMessage());
                })
                .verify();
    }

    @Test
    void shouldMapUnknownErrorToPersistenceException() {
        when(mapper.map(card, CardEntity.class)).thenReturn(entity);
        when(repository.save(any(CardEntity.class)))
                .thenReturn(Mono.error(new RuntimeException("connection lost")));

        StepVerifier.create(adapter.save(card))
                .expectErrorSatisfies(err -> {
                    assertInstanceOf(BusinessException.class, err);
                    assertEquals("Hubo un error desconocido en persistencia", err.getMessage());
                })
                .verify();
    }

    @Test
    void shouldFindByIdentifier() {
        when(repository.findByIdentifier("a3f7b2c1e9d04f58")).thenReturn(Mono.just(entity));
        when(mapper.map(entity, Card.class)).thenReturn(card);

        StepVerifier.create(adapter.findByIdentifier("a3f7b2c1e9d04f58"))
                .assertNext(found -> {
                    assertEquals("4567890123456789", found.getPan());
                    assertEquals("a3f7b2c1e9d04f58", found.getIdentifier());
                })
                .verifyComplete();

        verify(repository).findByIdentifier("a3f7b2c1e9d04f58");
    }

    @Test
    void shouldReturnEmptyWhenIdentifierNotFound() {
        when(repository.findByIdentifier("nonexistent")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findByIdentifier("nonexistent"))
                .verifyComplete();
    }
}