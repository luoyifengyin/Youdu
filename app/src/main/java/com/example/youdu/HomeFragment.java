package com.example.youdu;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import com.example.youdu.adapter.BookRecyclerViewAdapter;
import com.example.youdu.bean.BookInfo;
import com.example.youdu.bean.db.Book;
import com.example.youdu.net.SearchBookApi;
import com.example.youdu.util.LocalDataManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    public static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 3;
    private OnFragmentInteractionListener mListener;
    private Retrofit retrofit;
    private SearchView mSearchView;
    private Button mSearchBtn;
    private List<Book> mBookList;
    private BookRecyclerViewAdapter mAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(int columnCount) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT, mColumnCount);
        }
        retrofit = new Retrofit.Builder()
                .baseUrl(MainActivity.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mSearchView = view.findViewById(R.id.search);
        mSearchBtn = view.findViewById(R.id.btn_search);
        RecyclerView recyclerView = view.findViewById(R.id.book_list);
        Context context = recyclerView.getContext();
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        mAdapter = new BookRecyclerViewAdapter(context, mBookList, null);
        recyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(mSearchView.getQuery().toString());
            }
        });
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(mSearchBtn.getText().toString());
            }
        });
    }

    private void search(String keyword) {
        retrofit.create(SearchBookApi.class)
                .request(keyword)
                .enqueue(new Callback<List<BookInfo>>() {
                    @Override
                    public void onResponse(Call<List<BookInfo>> call, Response<List<BookInfo>> response) {
                        mBookList = LocalDataManager.getInstance(getContext()).handleBookList(response.body(), false);
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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
