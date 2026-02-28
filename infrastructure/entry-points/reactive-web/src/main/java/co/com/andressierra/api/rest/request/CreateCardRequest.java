package co.com.andressierra.api.rest.request;

import co.com.andressierra.model.card.enums.CardTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class CreateCardRequest {

    @NotBlank
    @Pattern(regexp = "\\d{16,19}", message = "PAN must be between 16 and 19 digits")
    private String pan;

    @NotBlank
    @Size(max = 150)
    private String cardholderName;

    @NotBlank
    @Size(min = 10, max = 15)
    private String cardholderId;

    @NotNull
    private CardTypeEnum cardType;

    @NotBlank
    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    private String phoneNumber;
}
