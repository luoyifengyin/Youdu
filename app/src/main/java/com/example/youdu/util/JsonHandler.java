package com.example.youdu.util;

import android.content.Context;

public class JsonHandler {
    private static JsonHandler jsonHandler;
    private Context context;

    private JsonHandler(Context context) {
        this.context = context.getApplicationContext();
    }

    public static JsonHandler with(Context context) {
        if (jsonHandler == null) {
            synchronized (JsonHandler.class) {
                if (jsonHandler == null) {
                    jsonHandler = new JsonHandler(context);
                }
            }
        }
        return jsonHandler;
    }
}
