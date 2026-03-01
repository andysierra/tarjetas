package co.com.andressierra.usecase.getcard;

import co.com.andressierra.model.card.Card;
import co.com.andressierra.model.card.gateways.CardRepository;
import co.com.andressierra.model.exception.BusinessException;
import co.com.andressierra.model.messages.MessagesEnum;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetCardUseCase {

    private final CardRepository cardRepository;

    public Mono<Card> getByIdentifier(String identifier) {
        return cardRepository.findByIdentifier(identifier)
                .switchIfEmpty(Mono.error(buildException(MessagesEnum.CARD_NOT_FOUND)));
    }

    private BusinessException buildException(MessagesEnum messagesEnum) {
        return new BusinessException(
                messagesEnum.getMessage(),
                messagesEnum.getOperationCode(),
                messagesEnum.getCode()
        );
    }
}