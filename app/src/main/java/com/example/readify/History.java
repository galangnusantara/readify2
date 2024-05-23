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

public class History extends AppCompatActivity implements BookAdapter.OnItemLongClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

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
        setContentView(R.layout.activity_history);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            // Tangani kasus di mana tidak ada pengguna yang terautentikasi
            // Alihkan ke aktivitas login atau tampilkan pesan
            // finish();
            return;
        }

        // Inisialisasi SharedPreferences
        String userPrefKey = "MyHistory_" + currentUser.getUid();
        sharedPreferences = getSharedPreferences(userPrefKey, MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        // Inisialisasi Firebase Database dengan URL yang diberikan
        mDatabase = FirebaseDatabase.getInstance("https://bookmark-9bf54-default-rtdb.firebaseio.com/").getReference().child("users").child(currentUser.getUid());

        // Inisialisasi RecyclerView
        recyclerViewBooks = findViewById(R.id.recycler_view_books);
        recyclerViewBooks.setLayoutManager(new LinearLayoutManager(this));

        // Inisialisasi daftar penulis buku
        bookAuthors = Arrays.asList(getResources().getStringArray(R.array.author_names));

        // Inisialisasi bookAdapter
        bookAdapter = new BookAdapter();
        recyclerViewBooks.setAdapter(bookAdapter);
        bookAdapter.setOnItemLongClickListener(this);

        // Set listener untuk menangani klik item biasa pada RecyclerView
        bookAdapter.setOnItemClickListener(new BookAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String selectedBookTitle = bookAdapter.getBookTitle(position);
                startDetailActivity(selectedBookTitle);
            }
        });

        // Tampilkan buku yang dipilih jika diterima dari intent atau dari SharedPreferences
        displaySelectedBook();

        // Inisialisasi tombol hapus
        clearButton = findViewById(R.id.btn_clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSelectedBookTitle(v);
            }
        });
    }


    @Override
    public void onItemLongClick(int position) {
        // Ambil judul buku di posisi yang diklik
        String selectedBookTitle = bookAdapter.getBookTitle(position);

        // Tampilkan dialog konfirmasi untuk penghapusan buku
        showDeleteConfirmationDialog(selectedBookTitle);
    }

    private void showDeleteConfirmationDialog(String bookTitle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah Anda yakin ingin menghapus buku ini?")
                .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Hapus buku dari riwayat dan Firebase Realtime Database
                        deleteBook(bookTitle);
                    }
                })
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Pengguna membatalkan dialog
                        dialog.dismiss();
                    }
                });
        // Buat dan tampilkan AlertDialog
        builder.create().show();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Tangani perubahan di SharedPreferences
        if (key.equals("Kenangan")) {
            // Tampilkan buku yang dipilih saat "Kenangan" berubah
            displaySelectedBook();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hapus listener perubahan SharedPreferences untuk mencegah kebocoran memori
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    private void deleteBook(String bookTitle) {
        // Hapus buku dari riwayat di sini
        deleteBookFromHistory(bookTitle);
        // Hapus buku dari Firebase Realtime Database
        deleteBookFromFirebase(bookTitle);
    }

    private void displaySelectedBook() {
        // Bersihkan daftar yang ada
        matchingTitles.clear();
        matchingAuthors.clear();
        matchingImages.clear();

        // Ambil judul buku dan gambar
        List<String> bookTitles = Arrays.asList(getResources().getStringArray(R.array.book_titles));
        List<Integer> bookImages = initializeBookImages();

        // Set untuk menyimpan buku yang sudah ditampilkan
        Set<String> displayedBooks = new HashSet<>();

        // Ambil data dari Firebase Realtime Database
        mDatabase.child("History").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String title = snapshot.child("title").getValue(String.class);
                    if (bookTitles.contains(title) && !displayedBooks.contains(title)) {
                        // Jika buku ada dalam daftar aplikasi dan belum ditampilkan, tambahkan ke daftar yang sesuai
                        int index = bookTitles.indexOf(title);
                        matchingTitles.add(title);
                        matchingAuthors.add(bookAuthors.get(index));
                        matchingImages.add(bookImages.get(index));
                        // Tambahkan buku yang ditampilkan ke set
                        displayedBooks.add(title);
                    }
                }
                // Perbarui RecyclerView dengan buku yang sesuai
                bookAdapter.setBooks(matchingTitles, matchingAuthors, matchingImages);
                // Tampilkan atau sembunyikan RecyclerView berdasarkan apakah ada buku yang sesuai
                recyclerViewBooks.setVisibility(matchingTitles.isEmpty() ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Tangani kesalahan di sini
            }
        });
    }

    // Metode lain tetap tidak berubah

    // Metode untuk inisialisasi gambar buku
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
                // Tambahkan lebih banyak sumber daya di sini sesuai dengan jumlah buku yang Anda miliki
        };

        // Tambahkan ID sumber daya ke daftar bookImages
        List<Integer> bookImages = new ArrayList<>();
        for (int resourceId : bookImageResources) {
            bookImages.add(resourceId);
        }
        return bookImages;
    }

    private void deleteBookFromHistory(String bookTitle) {
        // Hapus buku dari riwayat di sini
        // Misalnya, Anda bisa menghapusnya dari SharedPreferences atau Firebase Realtime Database
        // Anda dapat mengimplementasikan logika penghapusan berdasarkan kebutuhan spesifik Anda
        // Untuk tujuan demonstrasi, mari kita asumsikan menghapusnya dari SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(bookTitle);
        editor.apply();

        // Beritahu pengguna bahwa buku telah dihapus
        Toast.makeText(this, "Buku dihapus dari riwayat", Toast.LENGTH_SHORT).show();

        // Perbarui UI setelah penghapusan
        displaySelectedBook();
    }

    private void deleteBookFromFirebase(String bookTitle) {
        mDatabase.child("History").orderByChild("title").equalTo(bookTitle).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error != null) {
                                // Tangani kesalahan di sini
                                Log.e("DeleteBook", "Gagal menghapus buku dari Firebase: " + error.getMessage());
                            } else {
                                // Perbarui UI setelah penghapusan
                                displaySelectedBook();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Tangani kesalahan di sini
                Log.e("DeleteBook", "Gagal menghapus buku dari Firebase: " + databaseError.getMessage());
            }
        });
    }

    public void clearSelectedBookTitle(View view) {
        // Hapus judul buku yang dip
        // Hapus semua riwayat dari Firebase Realtime Database
        mDatabase.child("History").removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error != null) {
                    // Tangani kesalahan di sini
                    Log.e("ClearHistory", "Gagal menghapus riwayat: " + error.getMessage());
                    Toast.makeText(History.this, "Gagal menghapus riwayat", Toast.LENGTH_SHORT).show();
                } else {
                    // Tampilkan notifikasi "Semua riwayat dihapus"
                    Toast.makeText(History.this, "Semua riwayat dihapus", Toast.LENGTH_SHORT).show();

                    // Perbarui UI
                    displaySelectedBook();
                }
            }
        });
    }

    private void startDetailActivity(String selectedBook) {
        // Map judul buku ke kelas aktivitas detail yang sesuai
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

        // Temukan kelas yang sesuai untuk buku yang dipilih
        Class<?> detailClass = bookDetailMap.get(selectedBook);

        if (detailClass != null) {
            // Mulai aktivitas detail menggunakan kelas yang sesuai
            Intent intent = new Intent(this, detailClass);
            intent.putExtra("BOOK_TITLE", selectedBook);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Aktivitas detail tidak ditemukan untuk buku yang dipilih", Toast.LENGTH_SHORT).show();
        }
    }

}