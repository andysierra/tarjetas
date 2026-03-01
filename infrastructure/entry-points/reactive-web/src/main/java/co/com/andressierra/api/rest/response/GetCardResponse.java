package co.com.andressierra.api.rest.response;

import co.com.andressierra.model.card.enums.CardStatusEnum;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetCardResponse {
    private String maskedPan;
    private String cardholderName;
    private String cardholderId;
    private String phoneNumber;
    private CardStatusEnum status;
}