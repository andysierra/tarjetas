package co.com.andressierra.model.exception;

public class TechnicalException extends CustomException{
    public TechnicalException(String message, String code, int clientCode, Throwable throwable, String... details) {
        super(message, code, clientCode, throwable, details);
    }
}
