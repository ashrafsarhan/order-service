package com.codepole.exception;

public class OrderNotFoundException extends NotFoundException {

    public OrderNotFoundException(final String id) {
        super("No order found with id {%s}".formatted(id));
    }
}
