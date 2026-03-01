package co.com.andressierra.usecase.createcard;

import co.com.andressierra.model.card.Card;
import co.com.andressierra.model.card.enums.CardTypeEnum;
import co.com.andressierra.model.card.gateways.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateCardUseCaseTest {

    @Mock
    private CardRepository cardRepository;

    private CreateCardUseCase createCardUseCase;

    @BeforeEach
    void setUp() {
        createCardUseCase = new CreateCardUseCase(cardRepository);
    }

    private CreateCommand buildCommand() {
        return CreateCommand.builder()
                .pan("4567890123456789")
                .cardholderName("Test User")
                .cardholderId("1234567890")
                .cardType(CardTypeEnum.CREDIT)
                .phoneNumber("3001234567")
                .build();
    }

    @Test
    void create_shouldSaveCardWithCorrectFields() {
        when(cardRepository.save(any(Card.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(createCardUseCase.create(buildCommand()))
                .assertNext(card -> {
                    assertEquals("4567890123456789", card.getPan());
                    assertEquals("Test User", card.getCardholderName());
                    assertEquals("1234567890", card.getCardholderId());
                    assertEquals(CardTypeEnum.CREDIT, card.getCardType());
                    assertEquals("3001234567", card.getPhoneNumber());
                })
                .verifyComplete();
    }

    @Test
    void create_shouldSetStatusToCreated() {
        when(cardRepository.save(any(Card.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(createCardUseCase.create(buildCommand()))
                .assertNext(card -> assertEquals("CREATED", card.getStatus().name()))
                .verifyComplete();
    }

    @Test
    void create_shouldGenerateValidationNumberBetween1And100() {
        when(cardRepository.save(any(Card.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(createCardUseCase.create(buildCommand()))
                .assertNext(card -> {
                    assertNotNull(card.getValidationNumber());
                    assertTrue(card.getValidationNumber() >= 1 && card.getValidationNumber() <= 100);
                })
                .verifyComplete();
    }

    @Test
    void create_shouldSetCreatedAt() {
        when(cardRepository.save(any(Card.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(createCardUseCase.create(buildCommand()))
                .assertNext(card -> assertNotNull(card.getCreatedAt()))
                .verifyComplete();
    }

    @Test
    void create_shouldCallRepositorySave() {
        when(cardRepository.save(any(Card.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(createCardUseCase.create(buildCommand()))
                .expectNextCount(1)
                .verifyComplete();

        ArgumentCaptor<Card> captor = ArgumentCaptor.forClass(Card.class);
        verify(cardRepository).save(captor.capture());
        assertEquals("4567890123456789", captor.getValue().getPan());
    }
}