package co.com.andressierra.usecase.getcard;

import co.com.andressierra.model.card.Card;
import co.com.andressierra.model.card.enums.CardStatusEnum;
import co.com.andressierra.model.card.enums.CardTypeEnum;
import co.com.andressierra.model.card.gateways.CardRepository;
import co.com.andressierra.model.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCardUseCaseTest {

    @Mock
    private CardRepository cardRepository;

    private GetCardUseCase getCardUseCase;

    @BeforeEach
    void setUp() {
        getCardUseCase = new GetCardUseCase(cardRepository);
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

    @Test
    void shouldReturnCardWhenFound() {
        Card card = buildCard();
        when(cardRepository.findByIdentifier("a3f7b2c1e9d04f58")).thenReturn(Mono.just(card));

        StepVerifier.create(getCardUseCase.getByIdentifier("a3f7b2c1e9d04f58"))
                .assertNext(result -> {
                    assertEquals("4567890123456789", result.getPan());
                    assertEquals("Test User", result.getCardholderName());
                    assertEquals("1234567890", result.getCardholderId());
                    assertEquals("3001234567", result.getPhoneNumber());
                    assertEquals("CREATED", result.getStatus().name());
                })
                .verifyComplete();

        verify(cardRepository).findByIdentifier("a3f7b2c1e9d04f58");
    }

    @Test
    void shouldFailWhenCardNotFound() {
        when(cardRepository.findByIdentifier("nonexistent")).thenReturn(Mono.empty());

        StepVerifier.create(getCardUseCase.getByIdentifier("nonexistent"))
                .expectErrorSatisfies(err -> {
                    assertInstanceOf(BusinessException.class, err);
                    assertEquals("Tarjeta no existe", err.getMessage());
                })
                .verify();
    }
}