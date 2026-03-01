package co.com.andressierra.r2dbc;

import co.com.andressierra.model.card.Card;
import co.com.andressierra.model.exception.BusinessException;
import co.com.andressierra.model.messages.MessagesEnum;
import co.com.andressierra.r2dbc.entity.CardEntity;
import co.com.andressierra.r2dbc.helper.ReactiveAdapterOperations;
import co.com.andressierra.r2dbc.reactiveRepository.CardReactiveRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class CardRepositoryAdapter extends ReactiveAdapterOperations<Card,CardEntity,Long, CardReactiveRepository>
        implements co.com.andressierra.model.card.gateways.CardRepository
{
    public CardRepositoryAdapter(CardReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Card.class));
    }

    @Override
    public Mono<Card> save(Card card) {
        return super.save(card)
                .onErrorMap(DataIntegrityViolationException.class, e -> BusinessException.fromMessage(MessagesEnum.CARD_ALREADY_EXISTS))
                .onErrorMap(e -> !(e instanceof BusinessException), e -> BusinessException.fromMessage(MessagesEnum.PERSISTENCE_ERROR));
    }

    @Override
    public Mono<Card> findByIdentifier(String identifier) {
        return repository.findByIdentifier(identifier).map(this::toEntity);
    }
}
