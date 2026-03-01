package co.com.andressierra.model.exception;

public class BusinessException extends CustomException {

    public BusinessException(String message, String code, int clientCode) {
        super(message, code, clientCode);
    }

    public BusinessException(String message, String code, int clientCode, String[] info) {
        super(message, code, clientCode, null, info);
    }
}