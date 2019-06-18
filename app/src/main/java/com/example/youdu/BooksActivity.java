package com.example.youdu;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.youdu.bean.User;

public class BooksActivity extends AppCompatActivity {

    private User user;

    private FragmentTabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);
        user = (User) getIntent().getSerializableExtra("user");
        mTabHost = findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        Bundle bundle = new Bundle();
        bundle.putSerializable(BookshelfFragment.ARG_USER, user);
        mTabHost.addTab(createTabSpec("bookshelf", "书架"), BookshelfFragment.class, bundle);

    }

    public User getUser(){
        return user;
    }

    private TabHost.TabSpec createTabSpec(String tag, String name){
        return mTabHost.newTabSpec(tag).setIndicator(createTabView(name));
    }

    private View createTabView(String tag){
        View view = getLayoutInflater().inflate(R.layout.view_tab, mTabHost.getTabWidget());
        TextView tv = view.findViewById(R.id.tag);
        tv.setText(tag);
        return view;
    }
}
