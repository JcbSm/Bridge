package com.github.jcbsm.bridge.mojang;

import com.github.jcbsm.bridge.DatabaseClient;
import com.github.jcbsm.bridge.mojang.entities.Entity;
import com.google.gson.Gson;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MojangRequest<T extends Entity> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private OkHttpClient httpClient = new OkHttpClient();
    private Gson gson = new Gson();
    private T entity;

    public MojangRequest(T entity) {
        this.entity = entity;
    }

    public T execute() throws IOException{

        Request request = new Request.Builder().url(entity.getURL()).build();
        Call call = httpClient.newCall(request);
        ResponseBody response = call.execute().body();
        // sanamorii: might be dangerous - but i have no idea what to do otherwise :^)
        return gson.fromJson(response.string(), (Class<T>) this.entity.getClass() );

    }

}
