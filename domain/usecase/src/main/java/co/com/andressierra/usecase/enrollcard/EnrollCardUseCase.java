package co.com.andressierra.usecase.enrollcard;

import co.com.andressierra.model.card.Card;
import co.com.andressierra.model.card.enums.CardStatusEnum;
import co.com.andressierra.model.card.gateways.CardRepository;
import co.com.andressierra.model.exception.BusinessException;
import co.com.andressierra.model.messages.MessagesEnum;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class EnrollCardUseCase {

    private final CardRepository cardRepository;

    public Mono<Card> enroll(EnrollCommand command) {
        return cardRepository.findByIdentifier(command.getIdentifier())
                .switchIfEmpty(Mono.error(BusinessException.fromMessage(MessagesEnum.CARD_NOT_FOUND)))
                .flatMap(card -> {
                    if (!card.getValidationNumber().equals(command.getValidationNumber())) {
                        return Mono.error(BusinessException.fromMessage(MessagesEnum.INVALID_VALIDATION_NUMBER));
                    }
                    card.setStatus(CardStatusEnum.ENROLLED);
                    return cardRepository.save(card);
                });
    }
}