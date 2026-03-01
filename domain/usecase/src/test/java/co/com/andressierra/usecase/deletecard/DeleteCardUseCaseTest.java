package co.com.andressierra.usecase.deletecard;

import co.com.andressierra.model.card.Card;
import co.com.andressierra.model.card.enums.CardStatusEnum;
import co.com.andressierra.model.card.enums.CardTypeEnum;
import co.com.andressierra.model.card.gateways.CardRepository;
import co.com.andressierra.model.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteCardUseCaseTest {

    @Mock
    private CardRepository cardRepository;

    private DeleteCardUseCase deleteCardUseCase;

    @BeforeEach
    void setUp() {
        deleteCardUseCase = new DeleteCardUseCase(cardRepository);
    }

    private Card buildCard() {
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

    @Test
    void delete_shouldChangeStatusToInactive() {
        Card card = buildCard();
        when(cardRepository.findByIdentifier("a3f7b2c1e9d04f58")).thenReturn(Mono.just(card));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(deleteCardUseCase.delete("a3f7b2c1e9d04f58"))
                .assertNext(result -> assertEquals(CardStatusEnum.INACTIVE.name(), result.getStatus().name()))
                .verifyComplete();
    }

    @Test
    void delete_shouldCallSaveWithInactiveStatus() {
        Card card = buildCard();
        when(cardRepository.findByIdentifier("a3f7b2c1e9d04f58")).thenReturn(Mono.just(card));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(deleteCardUseCase.delete("a3f7b2c1e9d04f58"))
                .expectNextCount(1)
                .verifyComplete();

        ArgumentCaptor<Card> captor = ArgumentCaptor.forClass(Card.class);
        verify(cardRepository).save(captor.capture());
        assertEquals(CardStatusEnum.INACTIVE.name(), captor.getValue().getStatus().name());
    }

    @Test
    void delete_shouldFailWhenCardNotFound() {
        when(cardRepository.findByIdentifier("nonexistent")).thenReturn(Mono.empty());

        StepVerifier.create(deleteCardUseCase.delete("nonexistent"))
                .expectErrorSatisfies(err -> {
                    assertInstanceOf(BusinessException.class, err);
                    assertEquals("Tarjeta no existe", err.getMessage());
                })
                .verify();
    }

    @Test
    void delete_shouldPreserveOtherFields() {
        Card card = buildCard();
        when(cardRepository.findByIdentifier("a3f7b2c1e9d04f58")).thenReturn(Mono.just(card));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(deleteCardUseCase.delete("a3f7b2c1e9d04f58"))
                .assertNext(result -> {
                    assertEquals("4567890123456789", result.getPan());
                    assertEquals("Test User", result.getCardholderName());
                    assertEquals("a3f7b2c1e9d04f58", result.getIdentifier());
                })
                .verifyComplete();
    }
}