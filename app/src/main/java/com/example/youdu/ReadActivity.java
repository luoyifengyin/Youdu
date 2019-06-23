package com.example.youdu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.youdu.net.ChapterContentApi;
import com.example.youdu.util.LocalDataManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReadActivity extends AppCompatActivity {
    public static final String ARG_CHAPTER_CODE = "chapter-code";
    public static final String ARG_CONTENT_PATH = "content-path";
    private Retrofit retrofit;
    private TextView mContentTv;
    private int chapterCode;
    private String contentPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        retrofit = new Retrofit.Builder()
                .baseUrl(MainActivity.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mContentTv = findViewById(R.id.content);
        chapterCode = getIntent().getIntExtra(ARG_CHAPTER_CODE, 0);
        contentPath = getIntent().getStringExtra(ARG_CONTENT_PATH);

        String content = LocalDataManager.getInstance(this).getChapterContent(chapterCode);
        if (content != null) {
            mContentTv.setText(content);
        }

        ChapterContentApi service = retrofit.create(ChapterContentApi.class);
        if (contentPath != null) {
            service.requestContentByUrl(contentPath)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, final Response<String> response) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    handleContent(response.body());
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
        } else if (chapterCode > 0) {
            service.requestContentById(chapterCode)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, final Response<String> response) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    handleContent(response.body());
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

    private void handleContent(String content) {
        mContentTv.setText(content);
        LocalDataManager.getInstance(this).saveNovelContent(chapterCode, content);
    }
}
