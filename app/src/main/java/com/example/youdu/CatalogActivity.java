package com.example.youdu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.youdu.bean.UserInfo;
import com.example.youdu.bean.VolumeInfo;
import com.example.youdu.bean.db.Book;
import com.example.youdu.bean.db.Chapter;
import com.example.youdu.bean.db.Volume;
import com.example.youdu.net.CatalogApi;
import com.example.youdu.net.StarApi;
import com.example.youdu.util.LocalDataManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.youdu.ReadActivity.ARG_CHAPTER_CODE;
import static com.example.youdu.ReadActivity.ARG_CONTENT_PATH;

public class CatalogActivity extends AppCompatActivity {
    private Retrofit retrofit;
    private UserInfo mUserInfo;
    private List<Book> mBookCollection;
    private Book mBook;
    private boolean isSubmission;

    private ImageView mCoverImg;
    private TextView mTitleTv;
    private TextView mAuthorTv;
    private TextView mClassTv;
    private TextView mCollectionCntTv;
    private TextView mLastUpdateTimeTv;
    private TextView mIntroductionTv;
    private Button mCollectionBtn;
    private Button mReadBtn;
    private Button mBackBtn;
    private LinearLayout mCatalogLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        retrofit = new Retrofit.Builder()
                .baseUrl(MainActivity.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mBook = (Book) getIntent().getSerializableExtra("book");
        isSubmission = getIntent().getBooleanExtra("is-submission", false);
        mUserInfo = LocalDataManager.getInstance(this).getUserInfo();
        mCoverImg = findViewById(R.id.cover);
        mTitleTv = findViewById(R.id.title);
        mAuthorTv = findViewById(R.id.tv_author);
        mClassTv = findViewById(R.id.tv_class);
        mCollectionCntTv = findViewById(R.id.tv_collection_cnt);
        mLastUpdateTimeTv = findViewById(R.id.tv_last_update_time);
        mIntroductionTv = findViewById(R.id.tv_introduction);
        mCollectionBtn = findViewById(R.id.btn_collect);
        mReadBtn = findViewById(R.id.btn_read);
        mBackBtn = findViewById(R.id.btn_back);
        mCatalogLayout = findViewById(R.id.catalog);

        Glide.with(this).load(MainActivity.URL + mBook.getCoverPath()).into(mCoverImg);
        mTitleTv.setText(mBook.getTitle());
        mAuthorTv.setText(mBook.getAuthorName());
        mLastUpdateTimeTv.setText(mBook.getPublishTime());
        mIntroductionTv.setText(mBook.getIntroduction());

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mIntroductionTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIntroductionTv.getEllipsize() != null) {
                    mIntroductionTv.setMaxLines(Integer.MAX_VALUE);
                    mIntroductionTv.setEllipsize(null);
                } else {
                    mIntroductionTv.setMaxLines(getResources().getInteger(R.integer.book_introduction_lines));
                    mIntroductionTv.setEllipsize(TextUtils.TruncateAt.END);
                }
            }
        });

        if (mBook.getReadingChapterId() == 0) mReadBtn.setText(R.string.start_reading);
        else mReadBtn.setText(R.string.continue_reading);
        mReadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBook.getReadingChapterId() == 0) {
                    List<Volume> volumes = mBook.getVolumes();
                    if (volumes.size() > 0) {
                        List<Chapter> chapters = volumes.get(0).getChapters();
                        if (chapters.size() > 0) {
                            mBook.setReadingChapterId(chapters.get(0).getCode());
                            mBook.setLastTime(System.currentTimeMillis());
                            mBook.save();
                        }
                    }
                }
                Intent intent = new Intent(CatalogActivity.this, ReadActivity.class);
                intent.putExtra(ARG_CHAPTER_CODE, mBook.getReadingChapterId());
                startActivity(intent);
                mReadBtn.setText(R.string.continue_reading);
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
        showCatalog();
    }

    private void requestCatalog() {
        retrofit.create(CatalogApi.class)
                .request(mBook.getCode())
                .enqueue(new Callback<List<VolumeInfo>>() {
                    @Override
                    public void onResponse(Call<List<VolumeInfo>> call, Response<List<VolumeInfo>> response) {
                        LocalDataManager.getInstance(CatalogActivity.this)
                                .handleCatalog(mBook, response.body());
                        showCatalog();
                    }

                    @Override
                    public void onFailure(Call<List<VolumeInfo>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    private void showCatalog() {
        for (Volume volume : mBook.getVolumes()) {
            View volumeLayout = LayoutInflater.from(this)
                    .inflate(R.layout.item_volume, mCatalogLayout, false);
            LinearLayout chapterLayout = volumeLayout.findViewById(R.id.chapter_list);
            for (final Chapter chapter : volume.getChapters()) {
                TextView chapterView = (TextView) LayoutInflater.from(this)
                        .inflate(R.layout.item_chapter, chapterLayout, false);
                chapterView.setText(chapter.getTitle());
                chapterView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CatalogActivity.this, ReadActivity.class);
                        intent.putExtra(ARG_CHAPTER_CODE, chapter.getCode());
                        intent.putExtra(ARG_CONTENT_PATH, chapter.getContentPath());
                        startActivity(intent);
                    }
                });
                chapterLayout.addView(chapterView);
            }
            mCatalogLayout.addView(volumeLayout);
        }
    }
}
