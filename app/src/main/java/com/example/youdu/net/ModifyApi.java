package com.example.youdu.net;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ModifyApi {
    @FormUrlEncoded
    @POST("modify.php")
    Call<Boolean> request(@Field("uid") int uid, @Field("old_pwd_md5") String oldPwdMd5,
                          @Field("new_name") String newName, @Field("new_pwd") String newPwdMd5);
}
