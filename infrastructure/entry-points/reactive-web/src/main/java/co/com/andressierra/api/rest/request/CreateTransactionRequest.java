package co.com.andressierra.api.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransactionRequest {
    @NotBlank
    private String identifier;
    @NotBlank
    @Pattern(regexp = "\\d{6}")
    private String reference;
    @NotNull
    @Positive
    private BigDecimal totalAmount;
    @Size(max = 200)
    private String address;
}