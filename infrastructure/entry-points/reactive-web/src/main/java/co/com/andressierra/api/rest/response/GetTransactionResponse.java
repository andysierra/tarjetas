package co.com.andressierra.api.rest.response;

import co.com.andressierra.model.transaction.enums.TransactionStatusEnum;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class GetTransactionResponse {
    private String reference;
    private BigDecimal totalAmount;
    private String address;
    private TransactionStatusEnum status;
}