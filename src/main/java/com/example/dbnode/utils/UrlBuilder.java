package com.example.dbnode.utils;


public class UrlBuilder {
    private UrlBuilder(){

    }
    public static String buildUrlString(String hostname, int port, String path) {
        return String.format("http://%s:%d%s", hostname, port, path);
    }

}
