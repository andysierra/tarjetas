package co.com.andressierra.model.transaction.gateways;

import co.com.andressierra.model.transaction.Transaction;
import reactor.core.publisher.Mono;

public interface TransactionRepository {
    Mono<Transaction> save(Transaction transaction);
    Mono<Transaction> findByReference(String reference);
}