package co.com.andressierra.api.rest.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteCardResponse {
    private String identifier;
    private String status;
}