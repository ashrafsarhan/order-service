package com.codepole.exception;

public class ItemNotFoundException extends NotFoundException {
    public ItemNotFoundException(String id) {
        super("No item found with id {%s}".formatted(id));
    }
}
