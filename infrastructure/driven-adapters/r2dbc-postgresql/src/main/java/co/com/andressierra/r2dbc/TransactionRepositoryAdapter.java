package co.com.andressierra.r2dbc;

import co.com.andressierra.model.exception.BusinessException;
import co.com.andressierra.model.messages.MessagesEnum;
import co.com.andressierra.model.transaction.Transaction;
import co.com.andressierra.r2dbc.entity.TransactionEntity;
import co.com.andressierra.r2dbc.helper.ReactiveAdapterOperations;
import co.com.andressierra.r2dbc.reactiveRepository.TransactionReactiveRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class TransactionRepositoryAdapter extends ReactiveAdapterOperations<Transaction, TransactionEntity,Long, TransactionReactiveRepository>
        implements co.com.andressierra.model.transaction.gateways.TransactionRepository
{
    public TransactionRepositoryAdapter(TransactionReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Transaction.class));
    }

    @Override
    public Mono<Transaction> save(Transaction transaction) {
        return super.save(transaction)
                .onErrorMap(DataIntegrityViolationException.class, e -> BusinessException.fromMessage(MessagesEnum.TRANSACTION_ALREADY_EXISTS))
                .onErrorMap(e -> !(e instanceof BusinessException), e -> BusinessException.fromMessage(MessagesEnum.PERSISTENCE_ERROR));
    }

    @Override
    public Mono<Transaction> findByReference(String reference) {
        return repository.findByReference(reference).map(this::toEntity);
    }
}
