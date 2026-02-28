package co.com.andressierra.model.card;

import co.com.andressierra.model.card.enums.CardTypeEnum;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    private Card buildCard(String pan) {
        return Card.builder()
                .pan(pan)
                .cardholderName("Test User")
                .cardholderId("1234567890")
                .cardType(CardTypeEnum.CREDIT)
                .phoneNumber("3001234567")
                .validationNumber(42)
                .status("CREATED")
                .createdAt(LocalDateTime.of(2026, 2, 28, 12, 0))
                .build();
    }

    @Test
    void getMaskedPan_shouldMaskMiddleDigits() {
        Card card = buildCard("4567890123456789");
        assertEquals("456789******6789", card.getMaskedPan());
    }

    @Test
    void getMaskedPan_with19Digits_shouldMaskCorrectly() {
        Card card = buildCard("4567890123456789012");
        assertEquals("456789*********9012", card.getMaskedPan());
    }

    @Test
    void getMaskedPan_withNullPan_shouldReturnNull() {
        Card card = buildCard(null);
        assertNull(card.getMaskedPan());
    }

    @Test
    void getMaskedPan_withShortPan_shouldReturnAsIs() {
        Card card = buildCard("12345");
        assertEquals("12345", card.getMaskedPan());
    }

    @Test
    void getIdentifier_shouldReturnHexHash() {
        Card card = buildCard("4567890123456789");
        String identifier = card.getIdentifier();
        assertNotNull(identifier);
        assertEquals(16, identifier.length());
        assertTrue(identifier.matches("[0-9a-f]+"));
    }

    @Test
    void getIdentifier_samePanAndDate_shouldBeDeterministic() {
        Card card1 = buildCard("4567890123456789");
        Card card2 = buildCard("4567890123456789");
        assertEquals(card1.getIdentifier(), card2.getIdentifier());
    }

    @Test
    void getIdentifier_differentPan_shouldBeDifferent() {
        Card card1 = buildCard("4567890123456789");
        Card card2 = buildCard("9876543210123456");
        assertNotEquals(card1.getIdentifier(), card2.getIdentifier());
    }

    @Test
    void getIdentifier_withNullPan_shouldReturnNull() {
        Card card = buildCard(null);
        assertNull(card.getIdentifier());
    }

    @Test
    void getIdentifier_withNullCreatedAt_shouldReturnNull() {
        Card card = buildCard("4567890123456789");
        card.setCreatedAt(null);
        assertNull(card.getIdentifier());
    }
}