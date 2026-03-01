package co.com.andressierra.usecase.deletecard;

import co.com.andressierra.model.card.Card;
import co.com.andressierra.model.card.enums.CardStatusEnum;
import co.com.andressierra.model.card.gateways.CardRepository;
import co.com.andressierra.model.exception.BusinessException;
import co.com.andressierra.model.messages.MessagesEnum;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class DeleteCardUseCase {

    private final CardRepository cardRepository;

    public Mono<Card> delete(String identifier) {
        return cardRepository.findByIdentifier(identifier)
                .switchIfEmpty(Mono.error(BusinessException.fromMessage(MessagesEnum.CARD_NOT_FOUND)))
                .flatMap(card -> {
                    card.setStatus(CardStatusEnum.INACTIVE);
                    return cardRepository.save(card);
                });
    }
}