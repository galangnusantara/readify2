package com.example.readify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registrasi extends AppCompatActivity {

    private EditText Username, Email, Password;
    private Button buttonRegister;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrasi);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference(); // Inisialisasi Realtime Database

        Username = findViewById(R.id.Username);
        Email = findViewById(R.id.Email);
        Password = findViewById(R.id.Password);
        buttonRegister = findViewById(R.id.buttonRegistrasi);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = Username.getText().toString();
                final String email = Email.getText().toString();
                String password = Password.getText().toString();

                if (username.isEmpty() || email.isEmpty() || password.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Ada Data yang Masih Kosong!!",Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Registrasi.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, save the username to the database
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user != null) {
                                            String userId = user.getUid();
                                            mDatabase.child("users").child(userId).child("username").setValue(username);
                                        }

                                        Toast.makeText(Registrasi.this, "Registrasi berhasil", Toast.LENGTH_SHORT).show();
                                        Intent register = new Intent(Registrasi.this, Login.class);
                                        startActivity(register);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(Registrasi.this, "Registrasi gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}
