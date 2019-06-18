package com.example.youdu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.youdu.bean.Book;
import com.example.youdu.bean.User;
import com.example.youdu.net.BookshelfApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnItemClickListener}
 * interface.
 */
public class BookshelfFragment extends Fragment {

    public static final String ARG_COLUMN_COUNT = "column-count";
    public static final String ARG_USER = "user";
    //private static final String BOOKSHELF = "bookshelf";
    private static final String BOOK_TITLE = "book_title";
    private static final String BOOK_AUTHOR = "book_author";
    private static final String BOOK_COVER_URL = "book_cover_url";
    private int mColumnCount = 3;
    private OnItemClickListener mListener;
    private BooksActivity mActivity;
    private Retrofit retrofit;

    private User user;
    private List<Book> mBookList = new ArrayList<>();

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
    public static BookshelfFragment newInstance(int columnCount, User user) {
        BookshelfFragment fragment = new BookshelfFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putSerializable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT, mColumnCount);
            user = (User) getArguments().getSerializable("user");
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
            recyclerView.setAdapter(new BookRecyclerViewAdapter(context, mBookList, mListener));
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        user = mActivity.getUser();
        SharedPreferences prefs = mActivity.getSharedPreferences(user.name, 0);
        Set<String> set = prefs.getStringSet(BOOK_TITLE, null);
        if (set != null){

        }
        else {
            requestBookshelf();
        }
    }

    private void requestBookshelf(){
        retrofit.create(BookshelfApi.class)
                .request(user.uid)
                .enqueue(new Callback<List<Book>>() {
                    @Override
                    public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                        mBookList = response.body();
                    }

                    @Override
                    public void onFailure(Call<List<Book>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemClickListener) {
            mListener = (OnItemClickListener) context;
        }
        else {
            mListener = new OnItemClickListener() {
                @Override
                public void onItemClick(Book item) {
                    Intent intent = new Intent(getContext(), CatalogActivity.class);
                    intent.putExtra("book", item);
                    startActivity(intent);
                }
            };
        }
        mActivity = (BooksActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        //mActivity = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnItemClickListener {
        // TODO: Update argument type and name
        void onItemClick(Book item);
    }
}
