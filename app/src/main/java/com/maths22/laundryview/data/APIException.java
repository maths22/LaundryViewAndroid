package com.maths22.laundryview.data;

/**
 * Created by maths22 on 11/5/15.
 */
public class APIException extends Exception {
    public APIException() {
        super();
    }

    public APIException(String message) {
        super(message);
    }

    public APIException(String message, Throwable cause) {
        super(message, cause);
    }

    public APIException(Throwable cause) {
        super(cause);
    }
}
