package com.dz.coop.common.exception;

/**
 * @author panqz 2018-12-04 10:03 AM
 */

public class BookException extends RuntimeException {

    private static final String REGEX_PLACEHOLDER = "\\{}";

    public BookException(String message, String... params) {
        super(replaceHolder(message, params));
    }

    public BookException() {
    }

    public BookException(String message) {
        super(message);
    }

    public BookException(Throwable cause) {
        super(cause);
    }

    public BookException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public BookException(String message, Throwable cause) {
        super(message, cause);
    }

    private static String replaceHolder(String message, String... params) {
        for (String param : params) {
            message = message.replaceFirst(REGEX_PLACEHOLDER, param);
        }
        return message;
    }
}
