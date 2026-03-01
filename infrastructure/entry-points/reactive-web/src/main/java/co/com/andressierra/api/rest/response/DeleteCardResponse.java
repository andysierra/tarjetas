package co.com.andressierra.api.rest.response;

import co.com.andressierra.model.card.enums.CardStatusEnum;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteCardResponse {
    private String identifier;
    private CardStatusEnum status;
}