package co.com.andressierra.usecase.createtransaction;

import co.com.andressierra.usecase.Command;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CreateTransactionCommand extends Command {
    private String identifier;
    private String reference;
    private BigDecimal totalAmount;
    private String address;
}