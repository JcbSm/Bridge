package com.github.jcbsm.bridge.mojang.entities;

import java.util.UUID;

public class Player extends Entity {
    public String url = "";
    String name = null;
    String id = null;
    String error = null;
    String errorMessage = null;
    String path = null;

    public Player(String username){
        this.name = username;
    }


    public UUID getUUID() {
        return UUID.fromString(this.id);
    }

    public String getUsername() {
        return name;
    }

    public void setUsername(String name) { this.name = name; }


    public boolean exists() {
        if (error == null || errorMessage == null){ return false; }
        return true;
    }

    @Override
    public String getURL() {
        return String.format("https://api.mojang.com/users/profiles/minecraft/%s", this.name);
    }

}
