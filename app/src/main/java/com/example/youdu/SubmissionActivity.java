package com.example.youdu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.example.youdu.adapter.BookRecyclerViewAdapter;
import com.example.youdu.bean.db.Book;
import com.example.youdu.net.SubmissionApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SubmissionActivity extends AppCompatActivity {
    private Retrofit retrofit;
    private Button mBackBtn;
    private List<Book> mBookList;
    private BookRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        retrofit = new Retrofit.Builder()
                .baseUrl(MainActivity.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mBackBtn = findViewById(R.id.btn_back);
        RecyclerView recyclerView = findViewById(R.id.book_list);
        mAdapter = new BookRecyclerViewAdapter(this, mBookList, new BookRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Book book) {
                if (book.getCode() > 0) {
                    Intent intent = new Intent(SubmissionActivity.this, CatalogActivity.class);
                    intent.putExtra("book", book);
                    intent.putExtra("is-submission", true);
                    startActivity(intent);
                } else {
                    
                }
            }
        });
        recyclerView.setAdapter(mAdapter);

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    void requestBookList(int userId) {
        retrofit.create(SubmissionApi.class)
                .request(userId)
                .enqueue(new Callback<List<Book>>() {
                    @Override
                    public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                        mBookList = response.body();
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<List<Book>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }
}
