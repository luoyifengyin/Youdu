package com.example.youdu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.youdu.net.ChapterContentApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReadActivity extends AppCompatActivity {
    Retrofit retrofit;
    TextView mContentTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        retrofit = new Retrofit.Builder()
                .baseUrl(MainActivity.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mContentTv = findViewById(R.id.content);
        int chapterId = getIntent().getIntExtra("chapter-id", 0);
        String contentPath = getIntent().getStringExtra("content-path");
        ChapterContentApi service = retrofit.create(ChapterContentApi.class);
        if (contentPath != null) {
            service.requestContentByUrl(contentPath)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, final Response<String> response) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mContentTv.setText(response.body());
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            t.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ReadActivity.this, "加载失败……", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
        } else if (chapterId > 0) {
            service.requestContentById(chapterId)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, final Response<String> response) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mContentTv.setText(response.body());
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ReadActivity.this, "加载失败……", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
        }
    }
}
