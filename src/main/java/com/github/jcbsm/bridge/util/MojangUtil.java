package com.github.jcbsm.bridge.util;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class MojangUtil {

    @Nullable
    public static UUID getUserUUID(String username) {
        try {
            HttpURLConnection connection = getGetConnection("https://api.minecraftservices.com/minecraft/profile/lookup/name/" + username);

            int status = connection.getResponseCode();

            Reader streamReader = null;

            if (status > 299) {
                return null;
            } else {
                streamReader = new InputStreamReader(connection.getInputStream());
            }

            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder res = new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null) {
                res.append(line);
            };

            reader.close();
            connection.disconnect();

            return getProperUUID(StringUtils.substringBetween(res.toString(), "{", "}").split(",")[0].split(":")[1].replaceAll("\"", "").trim());

        } catch (IOException e) {
            return null;
        }
    }

    private static HttpURLConnection getGetConnection(String url) throws IOException {
        URL statusURL = new URL(url);

        HttpURLConnection connection = (HttpURLConnection) statusURL.openConnection();
        connection.setRequestMethod("GET");

        return connection;
    }

    @Nullable
    private static UUID getProperUUID(String uuidString){

        if(uuidString.length() != 32){
            return null;
        }

        String first = uuidString.substring(0,8);
        String second = uuidString.substring(8,12);
        String third = uuidString.substring(12,16);
        String fourth = uuidString.substring(16,20);
        String fifth = uuidString.substring(20);

        return UUID.fromString(first+"-"+second+"-"+third+"-"+fourth+"-"+fifth);
    }
}