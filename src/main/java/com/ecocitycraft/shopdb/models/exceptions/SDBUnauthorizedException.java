package com.ecocitycraft.shopdb.models.exceptions;

public class SDBUnauthorizedException extends RuntimeException {
    public SDBUnauthorizedException(String message) {
        super(message);
    }
}
