package co.com.andressierra.api.rest.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateCardResponse {
    private Integer validationNumber;
    private String maskedPan;
    private String identifier;
}