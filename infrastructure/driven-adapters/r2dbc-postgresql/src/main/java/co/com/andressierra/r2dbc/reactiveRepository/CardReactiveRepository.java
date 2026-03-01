package co.com.andressierra.r2dbc.reactiveRepository;

import co.com.andressierra.r2dbc.entity.CardEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CardReactiveRepository extends ReactiveCrudRepository<CardEntity, Long>, ReactiveQueryByExampleExecutor<CardEntity> {
    Mono<CardEntity> findByIdentifier(String identifier);
}
