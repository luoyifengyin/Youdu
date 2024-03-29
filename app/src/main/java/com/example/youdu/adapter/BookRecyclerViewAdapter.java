package com.example.youdu.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.youdu.CatalogActivity;
import com.example.youdu.MainActivity;
import com.example.youdu.R;
import com.example.youdu.bean.BookInfo;
import com.example.youdu.bean.db.Book;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link BookInfo} and makes a call to the
 * specified {@link OnItemClickListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class BookRecyclerViewAdapter extends RecyclerView.Adapter<BookRecyclerViewAdapter.ViewHolder> {

    private final Context mContext;
    private final List<Book> mValues;
    private final OnItemClickListener mListener;

    public BookRecyclerViewAdapter(Context context, List<Book> items, OnItemClickListener listener) {
        mContext = context;
        mValues = items;
        if (listener != null) {
            mListener = listener;
        } else {
            mListener = new OnItemClickListener() {
                @Override
                public void onItemClick(Book book) {
                    Intent intent = new Intent(mContext, CatalogActivity.class);
                    intent.putExtra("book", book);
                    mContext.startActivity(intent);
                }
            };
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        if (mValues.get(position).getCoverPath() != null) {
            Glide.with(mContext).load(MainActivity.URL + mValues.get(position).getCoverPath()).into(holder.mCover);
        }
        holder.mTitleText.setText(mValues.get(position).getTitle());
        holder.mAuthorText.setVisibility(View.GONE);
        if (mValues.get(position).getCode() < 0) {
            holder.mCover.setBackgroundResource(R.drawable.layer_add_book);
            holder.mCover.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.ic_add_book_plus),
                    mContext.getResources().getDimensionPixelSize(R.dimen.ic_add_book_plus),
                    mContext.getResources().getDimensionPixelSize(R.dimen.ic_add_book_plus),
                    mContext.getResources().getDimensionPixelSize(R.dimen.ic_add_book_plus));
            holder.mCover.setImageResource(R.drawable.plus);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onItemClick(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //final View mView;
        final ImageView mCover;
        final TextView mTitleText;
        final TextView mAuthorText;
        Book mItem;

        ViewHolder(View view) {
            super(view);
            //mView = view;
            mCover = view.findViewById(R.id.cover);
            mTitleText = view.findViewById(R.id.title);
            mAuthorText = view.findViewById(R.id.author);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleText.getText() + "'";
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Book book);
    }
}
