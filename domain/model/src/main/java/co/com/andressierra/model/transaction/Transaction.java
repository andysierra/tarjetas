package co.com.andressierra.model.transaction;

import co.com.andressierra.model.transaction.enums.TransactionStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Transaction {
    private Long id;
    private Long cardId;
    private String reference;
    private Integer validationNumber;
    private BigDecimal totalAmount;
    private String address;
    private TransactionStatusEnum status;
    private LocalDateTime createdAt;
}