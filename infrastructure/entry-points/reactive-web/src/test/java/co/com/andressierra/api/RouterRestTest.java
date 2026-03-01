package co.com.andressierra.api;

import co.com.andressierra.model.card.Card;
import co.com.andressierra.model.card.enums.CardTypeEnum;
import co.com.andressierra.usecase.createcard.CreateCardUseCase;
import co.com.andressierra.usecase.deletecard.DeleteCardUseCase;
import co.com.andressierra.usecase.enrollcard.EnrollCardUseCase;
import co.com.andressierra.usecase.getcard.GetCardUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {
        RouterRest.class,
        Handler.class
})
@WebFluxTest
class RouterRestTest {

    private static final String CARD = "/api/v1/cards";

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CreateCardUseCase createCardUseCase;

    @MockitoBean
    private EnrollCardUseCase enrollCardUseCase;

    @MockitoBean
    private GetCardUseCase getCardUseCase;

    @MockitoBean
    private DeleteCardUseCase deleteCardUseCase;

    @Test
    void createCard_shouldReturn201() {
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

        when(createCardUseCase.create(any())).thenReturn(Mono.just(card));

        String requestBody = """
                {
                    "pan": "4567890123456789",
                    "cardholderName": "Test User",
                    "cardholderId": "1234567890",
                    "cardType": "CREDIT",
                    "phoneNumber": "3001234567"
                }
                """;

        webTestClient.post()
                .uri(CARD)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.code").isEqualTo("00")
                .jsonPath("$.data.maskedPan").isEqualTo("456789******6789")
                .jsonPath("$.data.validationNumber").isEqualTo(42)
                .jsonPath("$.data.identifier").isNotEmpty();
    }
}
