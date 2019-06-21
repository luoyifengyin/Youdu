package com.example.youdu.net;

import com.example.youdu.bean.VolumeInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface CatalogApi {
    @FormUrlEncoded
    @POST("catalog.php")
    Call<List<VolumeInfo>> request(@Field("book_id") int bookId);
}
