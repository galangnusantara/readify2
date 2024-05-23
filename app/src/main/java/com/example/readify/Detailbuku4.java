package com.example.readify;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Detailbuku4 extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_buku4);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            // Redirect to login activity if no user is authenticated
            // startActivity(new Intent(this, LoginActivity.class));
            Toast.makeText(this, "Please log in to continue.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

        List<String> bookAuthors = Arrays.asList(getResources().getStringArray(R.array.author_names));
        ImageButton bookmarkButton = findViewById(R.id.bookmarkButton);
        bookmarkButton.setOnClickListener(v -> handleButtonClick(
                getString(R.string.title_wilder_girl),
                bookAuthors,
                "bookmark",
                "MyPrefs",
                "SELECTED_BOOK_TITLE"
        ));

        Button membacaButton = findViewById(R.id.p4);
        membacaButton.setOnClickListener(v -> {
            handleButtonClick(
                    getString(R.string.title_wilder_girl),
                    bookAuthors,
                    "History",
                    "MyHistory",
                    "KENANGAN"
            );
            startActivity(new Intent(Detailbuku4.this, Hal4.class));
        });
    }

    private void handleButtonClick(String selectedBookTitle, List<String> bookAuthors, String path, String prefKeyPrefix, String sharedPrefKey) {
        Book selectedBook = findBookDetails(selectedBookTitle, bookAuthors);

        if (selectedBook != null) {
            saveSelectedBookToPreferences(selectedBookTitle, prefKeyPrefix, sharedPrefKey);
            sendSelectedBookToFirebase(selectedBook, path);
            Toast.makeText(this, path.equals("bookmark") ? "Ditambahkan ke Bookmark" : "Ditambahkan ke Membaca", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Book details not found.", Toast.LENGTH_SHORT).show();
        }
    }

    private Book findBookDetails(String selectedBookTitle, List<String> bookAuthors) {
        List<String> bookTitles = Arrays.asList(getResources().getStringArray(R.array.book_titles));
        List<Integer> bookImages = initializeBookImages();

        for (int i = 0; i < bookTitles.size(); i++) {
            if (bookTitles.get(i).equals(selectedBookTitle)) {
                return new Book(selectedBookTitle, bookAuthors.get(i), bookImages.get(i));
            }
        }
        return null;
    }

    private void saveSelectedBookToPreferences(String selectedBookTitle, String prefKeyPrefix, String sharedPrefKey) {
        String userPrefKey = prefKeyPrefix + "_" + currentUser.getUid();
        SharedPreferences sharedPreferences = getSharedPreferences(userPrefKey, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(sharedPrefKey, selectedBookTitle);
        editor.apply();
    }

    private void sendSelectedBookToFirebase(Book book, String path) {
        Query query = mDatabase.child(path).orderByChild("title").equalTo(book.getTitle());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(Detailbuku4.this, "Buku sudah ada di " + path + ".", Toast.LENGTH_SHORT).show();
                } else {
                    mDatabase.child(path).push().setValue(book);
                    Toast.makeText(Detailbuku4.this, "Ditambahkan ke " + path, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error: " + databaseError.getMessage());
            }
        });
    }

    private List<Integer> initializeBookImages() {
        int[] bookImageResources = {
                R.drawable.book1,
                R.drawable.book2,
                R.drawable.book3,
                R.drawable.book4,
                R.drawable.book5,
                R.drawable.book6,
                R.drawable.book7,
                R.drawable.book8,
                R.drawable.book9,
                R.drawable.book10,
                // Add more resources here according to the number of books you have
        };

        List<Integer> bookImages = new ArrayList<>();
        for (int resourceId : bookImageResources) {
            bookImages.add(resourceId);
        }
        return bookImages;
    }

    public static class Book {
        private String title;
        private String author;
        private int imageResource;

        public Book() {
            // Default constructor required for calls to DataSnapshot.getValue(Book.class)
        }

        public Book(String title, String author, int imageResource) {
            this.title = title;
            this.author = author;
            this.imageResource = imageResource;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public int getImageResource() {
            return imageResource;
        }

        public void setImageResource(int imageResource) {
            this.imageResource = imageResource;
        }
    }
}
