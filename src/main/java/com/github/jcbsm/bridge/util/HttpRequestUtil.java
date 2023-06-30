package com.github.jcbsm.bridge.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class HttpRequestUtil {

    private final static OkHttpClient client = new OkHttpClient();

    public static String get(String url) throws IOException {

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public static Object parse(String json) {
        // Not yet implemented
        return null;
    }
}
