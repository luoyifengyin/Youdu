package com.example.youdu.net;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RegisterApi {
    @FormUrlEncoded
    @POST("register.php")
    Call<Boolean> request(@Field("username") String username, @Field("password") String password);
}
