package com.jjunpro.shop.exception;

public class ProductOrderException extends RuntimeException {

    public ProductOrderException(String message) {
        super(message);
    }

    public ProductOrderException(String message, Throwable cause) {
        super(message, cause);
    }
}
