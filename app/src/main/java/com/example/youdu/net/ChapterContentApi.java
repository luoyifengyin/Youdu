package com.example.youdu.net;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ChapterContentApi {
    @FormUrlEncoded
    @POST("chapter_content.php")
    Call<String> requestContentById(@Field("chapter_id") int chapterId);

    @FormUrlEncoded
    @POST("novel_content/{path}")
    Call<String> requestContentByUrl(@Path("path") String path);
}
