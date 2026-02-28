package co.com.andressierra.model.card;

import co.com.andressierra.model.card.enums.CardTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class Card {
    private Long id;
    private String pan;
    private String cardholderName;
    private String cardholderId;
    private CardTypeEnum cardType;
    private String phoneNumber;
    private Integer validationNumber;
    private String status;
    private LocalDateTime createdAt;

    public String getMaskedPan() {
        if (pan == null || pan.length() < 10) return pan;
        String first6 = pan.substring(0, 6);
        String last4 = pan.substring(pan.length() - 4);
        String mask = "*".repeat(pan.length() - 10);
        return first6 + mask + last4;
    }

    public String getIdentifier() {
        if (pan == null || createdAt == null) return null;
        return String.valueOf((pan + createdAt.toLocalDate()).hashCode());
    }
}
