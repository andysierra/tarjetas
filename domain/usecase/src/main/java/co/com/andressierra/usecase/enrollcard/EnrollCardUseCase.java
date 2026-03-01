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
                .switchIfEmpty(Mono.error(buildException(MessagesEnum.CARD_NOT_FOUND)))
                .flatMap(card -> {
                    if (!card.getValidationNumber().equals(command.getValidationNumber())) {
                        return Mono.error(buildException(MessagesEnum.INVALID_VALIDATION_NUMBER));
                    }
                    card.setStatus(CardStatusEnum.ENROLLED.name());
                    return cardRepository.save(card);
                });
    }

    private BusinessException buildException(MessagesEnum messagesEnum) {
        return new BusinessException(
                messagesEnum.getMessage(),
                messagesEnum.getOperationCode(),
                messagesEnum.getCode()
        );
    }
}