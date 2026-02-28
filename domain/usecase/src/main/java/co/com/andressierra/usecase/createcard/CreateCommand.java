package co.com.andressierra.usecase.createcard;

import co.com.andressierra.model.card.enums.CardTypeEnum;
import co.com.andressierra.usecase.Command;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateCommand extends Command {
    private String pan;
    private String cardholderName;
    private String cardholderId;
    private CardTypeEnum cardType;
    private String phoneNumber;
}