package com.example.readify;


import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<String> bookTitles;
    private List<String> bookAuthors;
    private List<Integer> bookImages;
    private OnItemClickListener itemClickListener;
    private OnItemLongClickListener itemLongClickListener;

    public BookAdapter() {

    }

    public void setBooks(List<String> bookTitles, List<String> bookAuthors, List<Integer> bookImages) {
        this.bookTitles = bookTitles;
        this.bookAuthors = bookAuthors;
        this.bookImages = bookImages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view, itemClickListener, itemLongClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        holder.bind(bookTitles.get(position), bookAuthors.get(position), bookImages.get(position));
    }

    @Override
    public int getItemCount() {
        return bookTitles == null ? 0 : bookTitles.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.itemLongClickListener = listener;
    }

    public String getBookTitle(int position) {
        if (bookTitles != null && position >= 0 && position < bookTitles.size()) {
            return bookTitles.get(position);
        } else {
            return null;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    static class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView bookTitleTextView;
        private TextView bookAuthorTextView;
        private ImageView bookImageView;
        private OnItemClickListener itemClickListener;
        private OnItemLongClickListener itemLongClickListener;

        public BookViewHolder(@NonNull View itemView, OnItemClickListener clickListener, OnItemLongClickListener longClickListener) {
            super(itemView);
            bookTitleTextView = itemView.findViewById(R.id.book_title);
            bookAuthorTextView = itemView.findViewById(R.id.author_name);
            bookImageView = itemView.findViewById(R.id.book_image);
            itemClickListener = clickListener;
            itemLongClickListener = longClickListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(String bookTitle, String bookAuthor, int bookImageResource) {
            bookTitleTextView.setText(bookTitle);
            bookAuthorTextView.setText(bookAuthor);
            Glide.with(itemView.getContext()).load(bookImageResource).into(bookImageView);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(position);
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (itemLongClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    itemLongClickListener.onItemLongClick(position);
                    return true;
                }
            }
            return false;
        }
    }
}
