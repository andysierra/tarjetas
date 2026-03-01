package co.com.andressierra.usecase.gettransaction;

import co.com.andressierra.model.exception.BusinessException;
import co.com.andressierra.model.messages.MessagesEnum;
import co.com.andressierra.model.transaction.Transaction;
import co.com.andressierra.model.transaction.gateways.TransactionRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetTransactionUseCase {

    private final TransactionRepository transactionRepository;

    public Mono<Transaction> getByReference(String reference) {
        return transactionRepository.findByReference(reference)
                .switchIfEmpty(Mono.error(buildException(MessagesEnum.INVALID_REFERENCE)));
    }

    private BusinessException buildException(MessagesEnum messagesEnum) {
        return new BusinessException(
                messagesEnum.getMessage(),
                messagesEnum.getOperationCode(),
                messagesEnum.getCode()
        );
    }
}