package com.example.youdu.net;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginApi {
    @FormUrlEncoded
    @POST("login.php")
    Call<JSONObject> request(@Field("username") String username, @Field("password") String password);
}
