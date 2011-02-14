package example.layout.element.util;

public class NoCurrentPageException extends Exception {

    public NoCurrentPageException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoCurrentPageException(String message) {
        super(message);
    }

    public NoCurrentPageException(Throwable cause) {
        super(cause);
    }

}
