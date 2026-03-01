package co.com.andressierra.model.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MessagesEnum {

    CARD_CREATED(201, "Exito creando la tarjeta de credito", "00"),
    CARD_IDENTIFIER_ERROR(501, "Error: Card no pudo generar el identificador", "99");

    private final int code;

    private final String message;

    private final String operationCode;
}
