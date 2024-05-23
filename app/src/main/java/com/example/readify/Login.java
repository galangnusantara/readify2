package com.example.readify;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult; // Add this import statement

public class Login extends AppCompatActivity {

    private EditText Email, Password;
    private Button buttonLogin, buttonGoogle, buttonFb;
    private TextView textViewRegistrasi;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inisialisasi komponen UI
        Email = findViewById(R.id.Email);
        Password = findViewById(R.id.Password);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonGoogle = findViewById(R.id.buttongoogle);
        buttonFb = findViewById(R.id.buttonfb);
        textViewRegistrasi = findViewById(R.id.buttonRegistrasi);

        mAuth = FirebaseAuth.getInstance();

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mendapatkan nilai dari input email dan password
                String email = Email.getText().toString();
                String password = Password.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    // Jika input kosong, tampilkan pesan kesalahan
                    Toast.makeText(Login.this, "Mohon isi semua field!", Toast.LENGTH_SHORT).show();
                } else {
                    // Melakukan login menggunakan FirebaseAuth
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Toast.makeText(Login.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                                        Intent masuk = new Intent(Login.this, Beranda.class);
                                        startActivity(masuk);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(Login.this, "Login gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        textViewRegistrasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ketika tombol Registrasi ditekan, arahkan ke halaman Registrasi
                Intent intent = new Intent(Login.this, Registrasi.class);
                startActivity(intent);
            }
        });

        // Inisialisasi tombol Google
        buttonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Membuat Intent untuk membuka Google di browser
                Uri uri = Uri.parse("https://www.google.com");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        // Inisialisasi tombol Facebook
        buttonFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Membuat Intent untuk membuka Facebook di browser
                Uri uri = Uri.parse("https://www.facebook.com");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }
}
