package co.com.andressierra.model.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MessagesEnum {

    CARD_CREATED(201, "Exito creando la tarjeta", "21"),
    CARD_ENROLLED(200, "Exito enrolando la tarjeta", "22"),
    CARD_FOUND(200, "Exito consultando la tarjeta", "23"),
    CARD_DELETED(200, "Se ha eliminado la tarjeta", "24"),
    TRANSACTION_CREATED(201, "Compra exitosa", "25"),
    TRANSACTION_CANCELLED(200, "Compra anulada", "26"),
    TRANSACTION_FOUND(200, "Exito consultando la transaccion", "27"),
    CARD_NOT_FOUND(404, "Tarjeta no existe", "41"),
    CARD_NOT_ENROLLED(400, "Tarjeta no enrolada", "42"),
    INVALID_REFERENCE(400, "Numero de referencia invalido", "43"),
    TRANSACTION_CANNOT_CANCEL(400, "No se puede anular transaccion por su antiguedad", "44"),
    BAD_REQUEST(400, "Datos de entrada invalidos", "40"),
    INVALID_VALIDATION_NUMBER(400, "Numero de validacion invalido", "45"),
    CARD_ALREADY_EXISTS(409, "La tarjeta ya existe en el sistema", "46"),
    TRANSACTION_ALREADY_EXISTS(409, "El numero de referencia ya existe", "47"),
    PERSISTENCE_ERROR(409, "Hubo un error desconocido en persistencia", "48"),
    CARD_IDENTIFIER_ERROR(501, "Error: Card no pudo generar el identificador", "51"),
    UNKNOWN_ERROR(500, "ERROR: Se ha encontrado un error desconocido, estaremos reparandolo muy pronto", "52");

    private final int code;

    private final String message;

    private final String operationCode;

    public static MessagesEnum findByOpCode(String operationCode) {
        for (MessagesEnum value : values()) {
            if (operationCode.equals(value.operationCode)) {
                return value;
            }
        }
        return null;
    }
}
