package co.com.andressierra.model.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MessagesEnum {

    CARD_CREATED(201, "Exito creando la tarjeta de credito", "00");

    private final int code;

    private final String message;

    private final String operationCode;
}
