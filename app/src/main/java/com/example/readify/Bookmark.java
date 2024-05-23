package com.example.readify;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Bookmark extends AppCompatActivity implements BookAdapter.OnItemClickListener, BookAdapter.OnItemLongClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private RecyclerView recyclerViewBooks;
    private BookAdapter bookAdapter;
    private List<String> bookAuthors;
    private List<String> matchingTitles = new ArrayList<>();
    private List<String> matchingAuthors = new ArrayList<>();
    private List<Integer> matchingImages = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private DatabaseReference mDatabase;
    private Button clearButton;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            // Handle the case where there is no authenticated user
            // Redirect to login activity or show a message
            // finish();
            return;
        }

        // Initialize SharedPreferences
        String userPrefKey = "MyPrefs_" + currentUser.getUid();
        sharedPreferences = getSharedPreferences(userPrefKey, MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        // Initialize Firebase Database with the given URL
        mDatabase = FirebaseDatabase.getInstance("https://bookmark-9bf54-default-rtdb.firebaseio.com/").getReference().child("users").child(currentUser.getUid());

        // Initialize RecyclerView
        recyclerViewBooks = findViewById(R.id.recycler_view_books);
        recyclerViewBooks.setLayoutManager(new LinearLayoutManager(this));

        // Initialize bookAuthors list
        bookAuthors = Arrays.asList(getResources().getStringArray(R.array.author_names));

        // Initialize bookAdapter
        bookAdapter = new BookAdapter();
        recyclerViewBooks.setAdapter(bookAdapter);
        bookAdapter.setOnItemClickListener(this);
        bookAdapter.setOnItemLongClickListener(this);

        // Display selected book if received from intent or from SharedPreferences
        displaySelectedBook();

        // Initialize clear button
        clearButton = findViewById(R.id.btn_clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSelectedBookTitle(v);
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        // Retrieve the book title at the clicked position
        String selectedBookTitle = bookAdapter.getBookTitle(position);
        // Start the detail activity with the selected book
        startDetailActivity(selectedBookTitle);
    }

    @Override
    public void onItemLongClick(int position) {
        // Retrieve the book title at the clicked position
        String selectedBookTitle = bookAdapter.getBookTitle(position);
        // Show confirmation dialog for book deletion
        showDeleteConfirmationDialog(selectedBookTitle);
    }

    private void showDeleteConfirmationDialog(String bookTitle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah Anda yakin ingin menghapus buku ini?")
                .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Delete the book from bookmark and Firebase Realtime Database
                        deleteBook(bookTitle);
                    }
                })
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.dismiss();
                    }
                });
        // Create the AlertDialog object and show it
        builder.create().show();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Handle changes in SharedPreferences
        if (key.equals("SELECTED_BOOK_TITLE")) {
            // Display selected book when "SELECTED_BOOK_TITLE" changes
            displaySelectedBook();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the SharedPreferences change listener to prevent memory leaks
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    private void deleteBook(String bookTitle) {
        // Delete the book from bookmark here
        deleteBookFromBookmark(bookTitle);
        // Delete the book from Firebase Realtime Database
        deleteBookFromFirebase(bookTitle);
    }

    private void displaySelectedBook() {
        // Clear existing lists
        matchingTitles.clear();
        matchingAuthors.clear();
        matchingImages.clear();

        // Retrieve book titles and images
        List<String> bookTitles = Arrays.asList(getResources().getStringArray(R.array.book_titles));
        List<Integer> bookImages = initializeBookImages();

        // Set to store already displayed books
        Set<String> displayedBooks = new HashSet<>();

        // Retrieve data from Firebase Realtime Database
        mDatabase.child("bookmark").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String title = snapshot.child("title").getValue(String.class);
                    if (bookTitles.contains(title) && !displayedBooks.contains(title)) {
                        // If the book exists in the app's list and is not already displayed, add it to the matching lists
                        int index = bookTitles.indexOf(title);
                        matchingTitles.add(title);
                        matchingAuthors.add(bookAuthors.get(index));
                        matchingImages.add(bookImages.get(index));
                        // Add the displayed book to the set
                        displayedBooks.add(title);
                    }
                }
                // Update RecyclerView with matching books
                bookAdapter.setBooks(matchingTitles, matchingAuthors, matchingImages);
                // Show or hide RecyclerView based on whether matching books exist
                recyclerViewBooks.setVisibility(matchingTitles.isEmpty() ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }

    // Other methods remain unchanged

    // Method to initialize book images
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

        // Add resource IDs to bookImages list
        List<Integer> bookImages = new ArrayList<>();
        for (int resourceId : bookImageResources) {
            bookImages.add(resourceId);
        }
        return bookImages;
    }

    private void deleteBookFromBookmark(String bookTitle) {
        // Delete the book from bookmark here
        // For example, you can remove it from SharedPreferences or Firebase Realtime Database
        // You can implement the deletion logic based on your specific requirements
        // For demonstration purposes, let's assume removing it from SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(bookTitle);
        editor.apply();

        // Notify the user that the book has been deleted
        Toast.makeText(this, "Buku dihapus dari bookmark", Toast.LENGTH_SHORT).show();

        // Update UI after deletion
        displaySelectedBook();
    }

    private void deleteBookFromFirebase(String bookTitle) {
        mDatabase.child("bookmark").orderByChild("title").equalTo(bookTitle).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error != null) {
                                // Handle errors here
                                Log.e("DeleteBook", "Failed to delete book from Firebase: " + error.getMessage());
                            } else {
                                // Update UI after deletion
                                displaySelectedBook();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
                Log.e("DeleteBook", "Failed to delete book from Firebase: " + databaseError.getMessage());
            }
        });
    }

    public void clearSelectedBookTitle(View view) {
        // Clear selected book title from SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("SELECTED_BOOK_TITLE");
        editor.apply();

        // Remove all bookmarked books from Firebase Realtime Database
        mDatabase.child("bookmark").removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error != null) {
                    // Handle errors here
                    Log.e("ClearBookmark", "Failed to clear bookmark: " + error.getMessage());
                    Toast.makeText(Bookmark.this, "Gagal menghapus buku dari bookmark", Toast.LENGTH_SHORT).show();
                } else {
                    // Show notification "Semua buku dihapus"
                    Toast.makeText(Bookmark.this, "Semua buku dihapus", Toast.LENGTH_SHORT).show();

                    // Update UI
                    displaySelectedBook();
                }
            }
        });
    }

    private void startDetailActivity(String selectedBook) {
        // Map book titles to their respective detail activities
        Map<String, Class<?>> bookDetailMap = new HashMap<>();
        bookDetailMap.put(getString(R.string.title_the_hobbit), Detailbuku.class);
        bookDetailMap.put(getString(R.string.title_last_train_to_istanbul), Detailbuku1.class);
        bookDetailMap.put(getString(R.string.title_utopia), Detailbuku2.class);
        bookDetailMap.put(getString(R.string.title_night_spinner), Detailbuku3.class);
        bookDetailMap.put(getString(R.string.title_wilder_girl), Detailbuku4.class);
        bookDetailMap.put(getString(R.string.title_kimi_no_nawa), Detailbuku5.class);
        bookDetailMap.put(getString(R.string.title_kafka_on_the_shore), Detailbuku6.class);
        bookDetailMap.put(getString(R.string.title_about_you), Detailbuku7.class);
        bookDetailMap.put(getString(R.string.title_human_act), Detailbuku8.class);
        bookDetailMap.put(getString(R.string.title_matahari), Detailbuku9.class);

        // Find the corresponding class for the selected book
        Class<?> detailClass = bookDetailMap.get(selectedBook);

        if (detailClass != null) {
            // Start the detail activity using the corresponding class
            Intent intent = new Intent(this, detailClass);
            intent.putExtra("BOOK_TITLE", selectedBook);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Detail activity not found for the selected book", Toast.LENGTH_SHORT).show();
        }
    }


}
