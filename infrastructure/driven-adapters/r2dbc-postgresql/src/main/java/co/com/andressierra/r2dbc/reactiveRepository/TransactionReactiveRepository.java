package co.com.andressierra.r2dbc.reactiveRepository;

import co.com.andressierra.r2dbc.entity.TransactionEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface TransactionReactiveRepository extends ReactiveCrudRepository<TransactionEntity, Long>, ReactiveQueryByExampleExecutor<TransactionEntity> {
    Mono<TransactionEntity> findByReference(String reference);
}
