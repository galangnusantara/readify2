package com.example.readify;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.material.bottomappbar.BottomAppBar;

public class Beranda extends AppCompatActivity {

    private FrameLayout newReleaseContainer;
    private FrameLayout popularContainer;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView hiFela;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beranda);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference(); // Inisialisasi Realtime Database

        // Mendapatkan referensi ke container fragmen
        newReleaseContainer = findViewById(R.id.new_release_container);
        popularContainer = findViewById(R.id.popular_container);

        // Mendapatkan referensi ke ImageView pencarian, penanda buku, dan ImageView riwayat dari layout XML
        AppCompatImageView searchImageView = findViewById(R.id.iv2);
        AppCompatImageView bookmarkImageView = findViewById(R.id.iv3);
        AppCompatImageView historyImageView = findViewById(R.id.iv5);
        ImageView profilImageView = findViewById(R.id.profil);
        hiFela = findViewById(R.id.Aboutyou); // Inisialisasi hiFela

        // Menambahkan listener untuk item pencarian di BottomAppBar
        searchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Beranda.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        // Menambahkan listener untuk item penanda buku di BottomAppBar
        bookmarkImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Beranda.this, Bookmark.class);
                startActivity(intent);
            }
        });

        // Menambahkan listener untuk item profil
        profilImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Beranda.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Menambahkan listener untuk item riwayat di BottomAppBar
        historyImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Beranda.this, History.class);
                startActivity(intent);
            }
        });

        // Mendapatkan referensi ke BottomAppBar
        BottomAppBar bottomAppBar = findViewById(R.id.bottomAppBar);

        // Menambahkan listener untuk item menu di BottomAppBar
        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Tambahkan logika jika diperlukan
                return true;
            }
        });

        // Menambahkan listener untuk tombol "New Release"
        Button newReleaseButton = findViewById(R.id.new_release_button);
        newReleaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewReleaseFragment();
            }
        });

        // Menambahkan listener untuk tombol "Popular"
        Button popularButton = findViewById(R.id.popular_button);
        popularButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopularFragment();
            }
        });

        // Secara default, tampilkan fragmen "New Release" saat activity pertama kali dibuat
        showNewReleaseFragment();

        // Menampilkan username
        showUsername();
    }

    private void showUsername() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = mDatabase.child("users").child(userId).child("username");
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String username = snapshot.getValue(String.class);
                    if (username != null) {
                        hiFela.setText("Hi " + username);
                    } else {
                        hiFela.setText("Hi User");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    hiFela.setText("Hi User");
                }
            });
        } else {
            hiFela.setText("Hi User");
        }
    }

    private void showNewReleaseFragment() {
        // Membuat instance fragmen "NewReleaseFragment"
        NewReleaseFragment newReleaseFragment = new NewReleaseFragment();

        // Mengambil instance FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Memulai transaksi fragmen dengan animasi slide up
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_up, 0);

        // Mengganti fragmen yang ada di container dengan fragmen "NewReleaseFragment"
        transaction.replace(R.id.new_release_container, newReleaseFragment);

        // Menandai transaksi sebagai selesai dan melakukan commit
        transaction.commit();

        // Mengatur visibilitas container sesuai dengan fragmen yang ditampilkan
        newReleaseContainer.setVisibility(View.VISIBLE);
        popularContainer.setVisibility(View.GONE);
    }

    private void showPopularFragment() {
        // Membuat instance fragmen "PopularFragment"
        PopularFragment popularFragment = new PopularFragment();

        // Mengambil instance FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Memulai transaksi fragmen dengan animasi slide up
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_up, 0);

        // Mengganti fragmen yang ada di container dengan fragmen "PopularFragment"
        transaction.replace(R.id.popular_container, popularFragment);

        // Menandai transaksi sebagai selesai dan melakukan commit
        transaction.commit();

        // Mengatur visibilitas container sesuai dengan fragmen yang ditampilkan
        popularContainer.setVisibility(View.VISIBLE);
        newReleaseContainer.setVisibility(View.GONE);
    }
}
