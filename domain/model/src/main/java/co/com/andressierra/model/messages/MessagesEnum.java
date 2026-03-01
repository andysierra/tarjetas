package co.com.andressierra.model.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MessagesEnum {

    CARD_CREATED(201, "Exito creando la tarjeta", "00"),
    CARD_ENROLLED(200, "Exito enrolando la tarjeta", "00"),
    CARD_FOUND(200, "Exito consultando la tarjeta", "00"),
    CARD_DELETED(200, "Se ha eliminado la tarjeta", "00"),
    TRANSACTION_CREATED(201, "Compra exitosa", "00"),
    TRANSACTION_CANCELLED(200, "Compra anulada", "00"),
    CARD_NOT_FOUND(404, "Tarjeta no existe", "01"),
    CARD_NOT_ENROLLED(400, "Tarjeta no enrolada", "02"),
    INVALID_REFERENCE(400, "Numero de referencia invalido", "01"),
    TRANSACTION_CANNOT_CANCEL(400, "No se puede anular transaccion", "02"),
    INVALID_VALIDATION_NUMBER(400, "Numero de validacion invalido", "02"),
    CARD_ALREADY_EXISTS(409, "La tarjeta ya existe en el sistema", "01"),
    TRANSACTION_ALREADY_EXISTS(409, "El numero de referencia ya existe", "01"),
    PERSISTENCE_ERROR(409, "Hubo un error desconocido en persistencia", "01"),
    CARD_IDENTIFIER_ERROR(501, "Error: Card no pudo generar el identificador", "99"),
    UNKNOWN_ERROR(500, "ERROR: Se ha encontrado un error desconocido, estaremos reparandolo muy pronto", "99");

    private final int code;

    private final String message;

    private final String operationCode;

    public static MessagesEnum findByClientCode(int clientCode) {
        for (MessagesEnum value : values()) {
            if (value.code == clientCode) {
                return value;
            }
        }
        return null;
    }
}
