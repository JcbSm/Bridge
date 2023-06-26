package com.github.jcbsm.bridge.util;

import java.util.HashMap;
import java.util.Map;

public class ConfigHandler {

    private final Map<String, Object> cache = new HashMap<>();

    public Map<String, Object> getCache() {
        return cache;
    }
}
