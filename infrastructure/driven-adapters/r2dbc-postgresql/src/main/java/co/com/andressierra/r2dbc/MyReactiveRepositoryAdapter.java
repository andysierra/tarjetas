package co.com.andressierra.r2dbc;

import co.com.andressierra.model.card.Card;
import co.com.andressierra.model.card.gateways.CardRepository;
import co.com.andressierra.r2dbc.entity.CardEntity;
import co.com.andressierra.r2dbc.helper.ReactiveAdapterOperations;
import co.com.andressierra.r2dbc.reactiveRepository.MyReactiveRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

@Repository
public class MyReactiveRepositoryAdapter extends ReactiveAdapterOperations<Card,CardEntity,Long,MyReactiveRepository>
        implements CardRepository
{
    public MyReactiveRepositoryAdapter(MyReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Card.class));
    }
}
