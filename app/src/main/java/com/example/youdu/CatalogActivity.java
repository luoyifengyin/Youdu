package com.example.youdu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.youdu.bean.UserInfo;
import com.example.youdu.bean.VolumeInfo;
import com.example.youdu.bean.db.Book;
import com.example.youdu.net.CatalogApi;
import com.example.youdu.net.StarApi;
import com.example.youdu.util.LocalDataManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CatalogActivity extends AppCompatActivity {
    private Retrofit retrofit;
    private UserInfo mUserInfo;
    private List<Book> mBookCollection;
    private Book mBook;

    private ImageView mCoverImg;
    private TextView mTitleTv;
    private TextView mAuthorTv;
    private TextView mIntroduction;
    private TextView mPublishDateTv;
    private Button mCollectionBtn;
    private Button mReadBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        retrofit = new Retrofit.Builder()
                .baseUrl(MainActivity.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mBook = (Book) getIntent().getSerializableExtra("book");
        mUserInfo = LocalDataManager.getInstance(this).getUser();
        mCoverImg = findViewById(R.id.cover);
        mTitleTv = findViewById(R.id.title);
        mAuthorTv = findViewById(R.id.author);
//        mIntroduction = findViewById(R.id.introduction);
//        mPublishDateTv = findViewById(R.id.publish);
//        mCollectionBtn = findViewById(R.id.btn_collection);
//        mReadBtn = findViewById(R.id.btn_read);

        Glide.with(this).load(MainActivity.URL + mBook.getCoverPath()).into(mCoverImg);
        mTitleTv.setText(mBook.getTitle());
        mAuthorTv.setText(mBook.getAuthorName());
        mIntroduction.setText(mBook.getIntroduction());

        mReadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBook.getReadingChapterId() == 0) {
                    mBook.setReadingChapterId(mBook.getVolumes().get(0).getChapters().get(0).getCode());
                    mBook.save();
                }
                Intent intent = new Intent(CatalogActivity.this, ReadActivity.class);
                intent.putExtra("chapter-id", mBook.getReadingChapterId());
                startActivity(intent);
            }
        });

        if (!mBookCollection.contains(mBook)) mCollectionBtn.setText(R.string.collect);
        else mCollectionBtn.setText(R.string.cancel_collect);
        mCollectionBtn.setOnClickListener(new View.OnClickListener() {
            StarApi service = retrofit.create(StarApi.class);

            @Override
            public void onClick(View v) {
                if (mBookCollection.contains(mBook)) {
                    service.requestCollect(mUserInfo.getUid(), mUserInfo.getPasswordMd5(), mBook.getCode())
                            .enqueue(new Callback<Boolean>() {
                                @Override
                                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                    Boolean res = response.body();
                                    if (res != null && res) {
                                        mBook.setLastTime(System.currentTimeMillis());
                                        LocalDataManager.getInstance(CatalogActivity.this).addBook(mBook);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mCollectionBtn.setText(R.string.cancel_collect);
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onFailure(Call<Boolean> call, Throwable t) {
                                    t.printStackTrace();
                                }
                            });
                } else {
                    service.cancelCollect(mUserInfo.getUid(), mUserInfo.getPasswordMd5(), mBook.getCode())
                            .enqueue(new Callback<Boolean>() {
                                @Override
                                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                    Boolean res = response.body();
                                    if (res != null && res) {
                                        LocalDataManager.getInstance(CatalogActivity.this).removeBook(mBook);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mCollectionBtn.setText(R.string.collect);
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onFailure(Call<Boolean> call, Throwable t) {
                                    t.printStackTrace();
                                }
                            });
                }
            }
        });

        mBook.setVolumes(LocalDataManager.getInstance(this).getCatalog(mBook));
        requestCatalog();
    }

    private void requestCatalog() {
        retrofit.create(CatalogApi.class)
                .request(mBook.getId())
                .enqueue(new Callback<List<VolumeInfo>>() {
                    @Override
                    public void onResponse(Call<List<VolumeInfo>> call, Response<List<VolumeInfo>> response) {
                        LocalDataManager.getInstance(CatalogActivity.this)
                                .handleCatalog(mBook, response.body(), !mBookCollection.contains(mBook));
                    }

                    @Override
                    public void onFailure(Call<List<VolumeInfo>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }
}
