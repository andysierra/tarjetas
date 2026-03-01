package co.com.andressierra.api.rest.response;

import co.com.andressierra.model.transaction.enums.TransactionStatusEnum;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateTransactionResponse {
    private TransactionStatusEnum status;
    private String reference;
}