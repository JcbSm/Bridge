package com.github.jcbsm.bridge.exceptions;

public class DatabaseNoResultException extends Exception{
    public DatabaseNoResultException(String field, String value) {
        super("No value " + value + " found in field "+ field);
    }
}
