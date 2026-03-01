package co.com.andressierra.usecase.createtransaction;

import co.com.andressierra.model.card.enums.CardStatusEnum;
import co.com.andressierra.model.card.gateways.CardRepository;
import co.com.andressierra.model.exception.BusinessException;
import co.com.andressierra.model.messages.MessagesEnum;
import co.com.andressierra.model.transaction.Transaction;
import co.com.andressierra.model.transaction.enums.TransactionStatusEnum;
import co.com.andressierra.model.transaction.gateways.TransactionRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class CreateTransactionUseCase {

    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;

    public Mono<Transaction> create(CreateTransactionCommand command) {
        return cardRepository.findByIdentifier(command.getIdentifier())
                .switchIfEmpty(Mono.error(buildException(MessagesEnum.CARD_NOT_FOUND)))
                .flatMap(card -> {
                    if (!CardStatusEnum.ENROLLED.equals(card.getStatus())) {
                        return Mono.error(buildException(MessagesEnum.CARD_NOT_ENROLLED));
                    }
                    var transaction = Transaction.builder()
                            .cardId(card.getId())
                            .reference(command.getReference())
                            .validationNumber(card.getValidationNumber())
                            .totalAmount(command.getTotalAmount())
                            .address(command.getAddress())
                            .status(TransactionStatusEnum.APPROVED)
                            .createdAt(LocalDateTime.now())
                            .build();
                    return transactionRepository.save(transaction);
                });
    }

    private BusinessException buildException(MessagesEnum messagesEnum) {
        return new BusinessException(
                messagesEnum.getMessage(),
                messagesEnum.getOperationCode(),
                messagesEnum.getCode()
        );
    }
}