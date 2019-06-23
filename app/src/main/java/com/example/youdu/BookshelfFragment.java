package com.example.youdu;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.youdu.adapter.BookRecyclerViewAdapter;
import com.example.youdu.bean.BookInfo;
import com.example.youdu.bean.UserInfo;
import com.example.youdu.bean.db.Book;
import com.example.youdu.net.BookshelfApi;
import com.example.youdu.util.LocalDataManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BookshelfFragment extends Fragment {
    public static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 3;
    private BookRecyclerViewAdapter.OnItemClickListener mListener;
    private IndexActivity mActivity;
    private Retrofit retrofit;

    private UserInfo mUserInfo;
    private List<Book> mBookList = new ArrayList<>();
    private BookRecyclerViewAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BookshelfFragment() {
        retrofit = new Retrofit.Builder()
                .baseUrl(MainActivity.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static BookshelfFragment newInstance(int columnCount) {
        BookshelfFragment fragment = new BookshelfFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT, mColumnCount);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookshelf, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            adapter = new BookRecyclerViewAdapter(context, mBookList, mListener);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mUserInfo = LocalDataManager.getInstance(getContext()).getUserInfo();
        mBookList = LocalDataManager.getInstance(getContext()).getBookCollection();
        adapter.notifyDataSetChanged();
        requestBookshelf();
    }

    private void requestBookshelf(){
        retrofit.create(BookshelfApi.class)
                .request(mUserInfo.getUid())
                .enqueue(new Callback<List<BookInfo>>() {
                    @Override
                    public void onResponse(Call<List<BookInfo>> call, Response<List<BookInfo>> response) {
                        List<BookInfo> list = response.body();
                        LocalDataManager.getInstance(getContext()).handleBookList(list, true);
                        mBookList = LocalDataManager.getInstance(getContext()).getBookCollection();
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<List<BookInfo>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BookRecyclerViewAdapter.OnItemClickListener) {
            mListener = (BookRecyclerViewAdapter.OnItemClickListener) context;
        }
        mActivity = (IndexActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        //mActivity = null;
    }
}