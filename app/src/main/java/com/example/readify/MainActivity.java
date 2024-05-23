package com.example.readify;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Switch switchPushNotifications;
    private TextView Email, Username;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference(); // Inisialisasi Realtime Database

        Button buttonTermsConditions = findViewById(R.id.button1);
        Button buttonPengaturanakun = findViewById(R.id.button2);
        Button buttonBookmark = findViewById(R.id.button);
        switchPushNotifications = findViewById(R.id.switchPushNotifications);
        Username = findViewById(R.id.user_name);
        Email = findViewById(R.id.user_gmail);

        // Menampilkan Username dan Email
        showUserInfo();

        // Terms and Conditions button click listener
        buttonTermsConditions.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, terms_condition.class);
            startActivity(intent);
        });
        buttonPengaturanakun.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });
        buttonBookmark.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Bookmark.class);
            startActivity(intent);
        });

        // Switch for Push Notifications toggle listener
        switchPushNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Change the switch track color to red when 'on'
                switchPushNotifications.getTrackDrawable().setTint(ContextCompat.getColor(MainActivity.this, android.R.color.holo_red_dark));
            } else {
                // Change the switch track color to the default color when 'off'
                switchPushNotifications.getTrackDrawable().setTint(ContextCompat.getColor(MainActivity.this, R.color.trackColor)); // Define the original color in colors.xml
            }
            // Implement your logic for push notification toggle here
        });
    }

    private void showUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            String userId = user.getUid();
            DatabaseReference userRef = mDatabase.child("users").child(userId).child("username");

            // Menampilkan Email di TextView dengan prefix "Email: "
            setEmailText(email);

            // Mendapatkan username dari Firebase Realtime Database
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String username = snapshot.getValue(String.class);
                    if (username != null) {
                        // Menampilkan Username di TextView dengan prefix "Username: "
                        setUsernameText(username);
                    } else {
                        setUsernameText("Username tidak tersedia");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Gagal mengambil data username", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Jika tidak ada pengguna yang masuk, tampilkan pesan default
            setEmailText("Email tidak tersedia");
            setUsernameText("Username tidak tersedia");
        }
    }

    private void setEmailText(String email) {
        Email.setText(getString(R.string.email_label, email));
    }

    private void setUsernameText(String username) {
        Username.setText(getString(R.string.username_label, username));
    }


}
