package co.com.andressierra.r2dbc;

import co.com.andressierra.model.card.Card;
import co.com.andressierra.model.card.enums.CardTypeEnum;
import co.com.andressierra.r2dbc.entity.CardEntity;
import co.com.andressierra.r2dbc.reactiveRepository.MyReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardRepositoryAdapterTest {

    @Mock
    private MyReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    private CardRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new CardRepositoryAdapter(repository, mapper);
    }

    @Test
    void shouldSaveCard() {
        Card card = Card.builder()
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

        CardEntity entity = CardEntity.builder()
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
}
