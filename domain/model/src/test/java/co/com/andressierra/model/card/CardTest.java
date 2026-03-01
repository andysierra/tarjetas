package co.com.andressierra.model.card;

import co.com.andressierra.model.card.enums.CardTypeEnum;
import co.com.andressierra.model.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

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
    void generateIdentifier_shouldReturnHexHash() {
        LocalDateTime date = LocalDateTime.of(2026, 2, 28, 12, 0);
        String identifier = Card.generateIdentifier("4567890123456789", date);
        assertNotNull(identifier);
        assertEquals(16, identifier.length());
        assertTrue(identifier.matches("[0-9a-f]+"));
    }

    @Test
    void generateIdentifier_samePanAndDate_shouldBeDeterministic() {
        LocalDateTime date = LocalDateTime.of(2026, 2, 28, 12, 0);
        assertEquals(
                Card.generateIdentifier("4567890123456789", date),
                Card.generateIdentifier("4567890123456789", date)
        );
    }

    @Test
    void generateIdentifier_differentPan_shouldBeDifferent() {
        LocalDateTime date = LocalDateTime.of(2026, 2, 28, 12, 0);
        assertNotEquals(
                Card.generateIdentifier("4567890123456789", date),
                Card.generateIdentifier("9876543210123456", date)
        );
    }

    @Test
    void generateIdentifier_withNullPan_shouldReturnNull() {
        assertNull(Card.generateIdentifier(null, LocalDateTime.now()));
    }

    @Test
    void generateIdentifier_withNullCreatedAt_shouldReturnNull() {
        assertNull(Card.generateIdentifier("4567890123456789", null));
    }

    @Test
    void generateIdentifier_shouldThrowBusinessExceptionWhenAlgorithmNotFound() {
        try (MockedStatic<MessageDigest> mockedDigest = mockStatic(MessageDigest.class)) {
            mockedDigest.when(() -> MessageDigest.getInstance("SHA-256"))
                    .thenThrow(new NoSuchAlgorithmException("SHA-256 not available"));

            LocalDateTime now = LocalDateTime.now();
            assertThrows(BusinessException.class,
                    () -> Card.generateIdentifier("4567890123456789", now));
        }
    }
}