package com.example.youdu.util;

import com.example.youdu.bean.User;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonHandler {
    public static User handleUserResponse(JSONObject object){
        try {
            return new Gson().fromJson(object.getJSONObject("user").toString(), User.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
