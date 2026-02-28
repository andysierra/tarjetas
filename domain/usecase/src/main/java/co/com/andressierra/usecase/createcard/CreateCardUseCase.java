package co.com.andressierra.usecase.createcard;

import co.com.andressierra.model.card.Card;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateCardUseCase {
    public Mono<Card> create(CreateCommand command) {
        return Mono.just(Card.builder().build());
    }
}
