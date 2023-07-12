package com.github.jcbsm.bridge.exceptions;

public class InvalidMinecraftName extends Exception{
    public InvalidMinecraftName(String username){
        super("The minecraft account '" + "' does not exist.");
    }
}
