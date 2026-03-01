package co.com.andressierra.api.rest.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetCardResponse {
    private String maskedPan;
    private String cardholderName;
    private String cardholderId;
    private String phoneNumber;
    private String status;
}