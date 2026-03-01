package co.com.andressierra.usecase.createcard;

import co.com.andressierra.model.card.Card;
import co.com.andressierra.model.card.enums.CardStatusEnum;
import co.com.andressierra.model.card.gateways.CardRepository;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateCardUseCase {

    private final CardRepository cardRepository;
    private final Random random = new SecureRandom();

    public Mono<Card> create(CreateCommand command) {
        LocalDateTime now = LocalDateTime.now();
        var card = Card.builder()
                .pan(command.getPan())
                .cardholderName(command.getCardholderName())
                .cardholderId(command.getCardholderId())
                .cardType(command.getCardType())
                .phoneNumber(command.getPhoneNumber())
                .validationNumber(generateValidationNumber())
                .identifier(Card.generateIdentifier(command.getPan(), now))
                .status(CardStatusEnum.CREATED)
                .createdAt(now)
                .build();

        return cardRepository.save(card);
    }

    private int generateValidationNumber() {

        // podemos usar un servicio externo para números de validación
        return random.nextInt(1, 101);
    }
}
