package com.example.youdu.net;

import com.example.youdu.bean.BookInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface SearchBookApi {
    @FormUrlEncoded
    @POST("search_book.php")
    Call<List<BookInfo>> request(@Field("keyword") String keyword);
}
