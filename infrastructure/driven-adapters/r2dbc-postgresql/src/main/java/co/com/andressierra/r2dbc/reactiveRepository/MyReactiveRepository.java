package co.com.andressierra.r2dbc.reactiveRepository;

import co.com.andressierra.r2dbc.entity.CardEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface MyReactiveRepository extends ReactiveCrudRepository<CardEntity, Long>, ReactiveQueryByExampleExecutor<CardEntity> {

}
