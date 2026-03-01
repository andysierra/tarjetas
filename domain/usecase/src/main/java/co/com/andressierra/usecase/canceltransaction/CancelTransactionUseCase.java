package co.com.andressierra.usecase.canceltransaction;

import co.com.andressierra.model.exception.BusinessException;
import co.com.andressierra.model.messages.MessagesEnum;
import co.com.andressierra.model.transaction.Transaction;
import co.com.andressierra.model.transaction.enums.TransactionStatusEnum;
import co.com.andressierra.model.transaction.gateways.TransactionRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class CancelTransactionUseCase {

    private static final int CANCEL_WINDOW_MINUTES = 5;

    private final TransactionRepository transactionRepository;

    public Mono<Transaction> cancel(String reference) {
        return transactionRepository.findByReference(reference)
                .switchIfEmpty(Mono.error(BusinessException.fromMessage(MessagesEnum.INVALID_REFERENCE)))
                .flatMap(transaction -> {
                    if (transaction.getCreatedAt().plusMinutes(CANCEL_WINDOW_MINUTES).isBefore(LocalDateTime.now())) {
                        return Mono.error(BusinessException.fromMessage(MessagesEnum.TRANSACTION_CANNOT_CANCEL));
                    }
                    transaction.setStatus(TransactionStatusEnum.CANCELLED);
                    return transactionRepository.save(transaction);
                });
    }
}