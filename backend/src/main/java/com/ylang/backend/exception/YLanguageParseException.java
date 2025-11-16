package com.ylang.backend.exception;

/**
 * Exception thrown when Y language parsing fails
 */
public class YLanguageParseException extends RuntimeException {
    
    public YLanguageParseException(String message) {
        super(message);
    }
    
    public YLanguageParseException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public YLanguageParseException(Throwable cause) {
        super(cause);
    }
}