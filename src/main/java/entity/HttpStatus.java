package entity;

public enum HttpStatus {
    NOT_FOUND (404),
    INTERNAL_SERVER_ERROR (500),
    OK (200);

    private int code;
    HttpStatus(int code) {
        this.code = code;
    }
}
