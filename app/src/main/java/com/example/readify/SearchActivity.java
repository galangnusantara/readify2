package com.example.readify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView recyclerViewBooks;
    private BookAdapter bookAdapter;
    private List<String> bookAuthors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize UI components
        searchView = findViewById(R.id.searchEditText);
        recyclerViewBooks = findViewById(R.id.recycler_view_books);

        // Initialize RecyclerView
        recyclerViewBooks.setLayoutManager(new LinearLayoutManager(this));
        bookAdapter = new BookAdapter();
        recyclerViewBooks.setAdapter(bookAdapter);

        // Initialize bookAuthors list
        bookAuthors = Arrays.asList(getResources().getStringArray(R.array.author_names));

        // Set SearchView listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (isInitialMatch(query)) {
                    Toast.makeText(SearchActivity.this, "Searching for: " + query, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SearchActivity.this, "No matching book found.", Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    // If search query is empty, display all book titles
                    List<String> bookTitles = Arrays.asList(getResources().getStringArray(R.array.book_titles));
                    List<Integer> bookImages = initializeBookImages();
                    bookAdapter.setBooks(bookTitles, bookAuthors, bookImages);
                    showAllViews(); // Show all views that were hidden during search
                } else {
                    hideAllViews(); // Hide all unnecessary views during search
                    showMatchingBooks(newText.toLowerCase());
                }
                return true;
            }
        });

        // Set item click listener for book adapter
        bookAdapter.setOnItemClickListener(position -> {
            String selectedBook = bookAdapter.getBookTitle(position);
            Toast.makeText(SearchActivity.this, "Selected book: " + selectedBook, Toast.LENGTH_SHORT).show();
            startDetailActivity(selectedBook);
        });
    }

    // Method to hide all views that are not needed during search
    private void hideAllViews() {
        recyclerViewBooks.setVisibility(View.GONE);
    }

    // Method to show all views that were hidden during search
    private void showAllViews() {
        recyclerViewBooks.setVisibility(View.VISIBLE);
    }

    // Method to check if search has an initial match
    private boolean isInitialMatch(String query) {
        return query.length() >= 1;
    }

    // Method to display books matching the search query
    private void showMatchingBooks(String query) {
        query = query.toLowerCase();

        List<String> matchingTitles = new ArrayList<>();
        List<Integer> matchingImages = new ArrayList<>();
        List<String> bookTitles = Arrays.asList(getResources().getStringArray(R.array.book_titles));
        List<Integer> bookImages = initializeBookImages();
        for (int i = 0; i < bookTitles.size(); i++) {
            String title = bookTitles.get(i);
            if (title.toLowerCase().contains(query)) {
                matchingTitles.add(title);
                matchingImages.add(bookImages.get(i));
            }
        }
        bookAdapter.setBooks(matchingTitles, bookAuthors, matchingImages);

        recyclerViewBooks.setVisibility(matchingTitles.isEmpty() ? View.GONE : View.VISIBLE);
    }

    // Method to start detail activity for the selected book
    private void startDetailActivity(String selectedBook) {
        int[] bookTitleIds = {
                R.string.title_the_hobbit,
                R.string.title_last_train_to_istanbul,
                R.string.title_utopia,
                R.string.title_night_spinner,
                R.string.title_wilder_girl,
                R.string.title_kimi_no_nawa,
                R.string.title_kafka_on_the_shore,
                R.string.title_about_you,
                R.string.title_human_act,
                R.string.title_matahari
        };

        Class[] detailActivities = {
                Detailbuku.class,
                Detailbuku1.class,
                Detailbuku2.class,
                Detailbuku3.class,
                Detailbuku4.class,
                Detailbuku5.class,
                Detailbuku6.class,
                Detailbuku7.class,
                Detailbuku8.class,
                Detailbuku9.class
        };

        for (int i = 0; i < bookTitleIds.length; i++) {
            if (selectedBook.equals(getString(bookTitleIds[i]))) {
                Intent intent = new Intent(SearchActivity.this, detailActivities[i]);
                intent.putExtra("selected_book_title", selectedBook);
                startActivity(intent);
                break;
            }
        }
    }

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
                R.drawable.book10
                // Add more resources here according to the number of books you have
        };

        // Add resource IDs to bookImages list
        List<Integer> bookImages = new ArrayList<>();
        for (int resourceId : bookImageResources) {
            bookImages.add(resourceId);
        }
        return bookImages;
    }
}
