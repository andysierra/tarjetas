package co.com.andressierra.usecase.enrollcard;

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
class EnrollCardUseCaseTest {

    @Mock
    private CardRepository cardRepository;

    private EnrollCardUseCase enrollCardUseCase;

    @BeforeEach
    void setUp() {
        enrollCardUseCase = new EnrollCardUseCase(cardRepository);
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
                .status(CardStatusEnum.CREATED)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private EnrollCommand buildCommand(int validationNumber) {
        return EnrollCommand.builder()
                .identifier("a3f7b2c1e9d04f58")
                .validationNumber(validationNumber)
                .build();
    }

    @Test
    void enroll_shouldChangeStatusToEnrolled() {
        Card card = buildCard();
        when(cardRepository.findByIdentifier("a3f7b2c1e9d04f58")).thenReturn(Mono.just(card));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(enrollCardUseCase.enroll(buildCommand(42)))
                .assertNext(result -> assertEquals(CardStatusEnum.ENROLLED.name(), result.getStatus().name()))
                .verifyComplete();
    }

    @Test
    void enroll_shouldCallSaveWithUpdatedCard() {
        Card card = buildCard();
        when(cardRepository.findByIdentifier("a3f7b2c1e9d04f58")).thenReturn(Mono.just(card));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(enrollCardUseCase.enroll(buildCommand(42)))
                .expectNextCount(1)
                .verifyComplete();

        ArgumentCaptor<Card> captor = ArgumentCaptor.forClass(Card.class);
        verify(cardRepository).save(captor.capture());
        assertEquals(CardStatusEnum.ENROLLED.name(), captor.getValue().getStatus().name());
    }

    @Test
    void enroll_shouldFailWhenCardNotFound() {
        when(cardRepository.findByIdentifier("a3f7b2c1e9d04f58")).thenReturn(Mono.empty());

        StepVerifier.create(enrollCardUseCase.enroll(buildCommand(42)))
                .expectErrorSatisfies(err -> {
                    assertInstanceOf(BusinessException.class, err);
                    assertEquals("Tarjeta no existe", err.getMessage());
                })
                .verify();
    }

    @Test
    void enroll_shouldFailWhenValidationNumberDoesNotMatch() {
        Card card = buildCard();
        when(cardRepository.findByIdentifier("a3f7b2c1e9d04f58")).thenReturn(Mono.just(card));

        StepVerifier.create(enrollCardUseCase.enroll(buildCommand(99)))
                .expectErrorSatisfies(err -> {
                    assertInstanceOf(BusinessException.class, err);
                    assertEquals("Numero de validacion invalido", err.getMessage());
                })
                .verify();
    }

    @Test
    void enroll_shouldPreserveCardFieldsAfterEnroll() {
        Card card = buildCard();
        when(cardRepository.findByIdentifier("a3f7b2c1e9d04f58")).thenReturn(Mono.just(card));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(enrollCardUseCase.enroll(buildCommand(42)))
                .assertNext(result -> {
                    assertEquals("4567890123456789", result.getPan());
                    assertEquals("Test User", result.getCardholderName());
                    assertEquals(42, result.getValidationNumber());
                    assertEquals("a3f7b2c1e9d04f58", result.getIdentifier());
                })
                .verifyComplete();
    }
}