package com.example.youdu.net;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface StarApi {
    @FormUrlEncoded
    @POST("collect.php")
    Call<Boolean> requestCollect(@Field("user_id") int userId, @Field("user_pwd") String userPwd, @Field("book_id") int bookId);

    @FormUrlEncoded
    @POST("cancel_collect.php")
    Call<Boolean> cancelCollect(@Field("user_id") int userId, @Field("user_pwd") String userPwd, @Field("book_id") int bookId);
}
