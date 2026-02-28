package co.com.andressierra.model.card.gateways;

import co.com.andressierra.model.card.Card;
import reactor.core.publisher.Mono;

public interface CardRepository {
    public Mono<Card> save(Card card);
}
