package com.example.readify;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {

    private EditText currentUsername, currentEmail, newPassword, confirmPassword;
    private Button buttonSave;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        currentUsername = findViewById(R.id.currentUsername);
        currentEmail = findViewById(R.id.currentEmail);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        buttonSave = findViewById(R.id.buttonSave);

        // Menampilkan informasi pengguna saat ini
        showCurrentUserInfo();

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newUsernameStr = currentUsername.getText().toString();
                String newPasswordStr = newPassword.getText().toString();
                String confirmPasswordStr = confirmPassword.getText().toString();

                if (TextUtils.isEmpty(newUsernameStr) && TextUtils.isEmpty(newPasswordStr)) {
                    Toast.makeText(EditProfileActivity.this, "Please enter new username or password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!TextUtils.isEmpty(newPasswordStr) && !newPasswordStr.equals(confirmPasswordStr)) {
                    Toast.makeText(EditProfileActivity.this, "Password and Confirm Password do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    if (!TextUtils.isEmpty(newUsernameStr)) {
                        updateUsername(user, newUsernameStr);
                    }

                    if (!TextUtils.isEmpty(newPasswordStr)) {
                        updatePassword(user, newPasswordStr);
                    }
                }
            }
        });
    }

    private void showCurrentUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            String userId = user.getUid();
            DatabaseReference userRef = mDatabase.child("users").child(userId).child("username");

            // Menampilkan Email di EditText
            currentEmail.setText(email);

            // Mendapatkan username dari Firebase Realtime Database
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String username = snapshot.getValue(String.class);
                    if (username != null) {
                        currentUsername.setText(username);
                    } else {
                        currentUsername.setHint("Username tidak tersedia");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(EditProfileActivity.this, "Gagal mengambil data username", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Jika tidak ada pengguna yang masuk, tampilkan pesan default
            currentEmail.setHint("Email tidak tersedia");
            currentUsername.setHint("Username tidak tersedia");
        }
    }

    private void updateUsername(FirebaseUser user, String newUsername) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newUsername)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Update username in Realtime Database
                            String userId = user.getUid();
                            mDatabase.child("users").child(userId).child("username").setValue(newUsername);

                            Toast.makeText(EditProfileActivity.this, "Username updated", Toast.LENGTH_SHORT).show();
                            // Tampilkan kembali info pengguna setelah pembaruan selesai
                            showCurrentUserInfo();
                        } else {
                            Toast.makeText(EditProfileActivity.this, "Failed to update username", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updatePassword(FirebaseUser user, String newPassword) {
        user.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditProfileActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
                        } else {
                            // Handle reauthentication if necessary
                            reauthenticateAndChangePassword(user, newPassword);
                        }
                    }
                });
    }

    private void reauthenticateAndChangePassword(FirebaseUser user, String newPassword) {
        String email = user.getEmail();
        String currentPassword = ""; // You need to get the current password from the user securely

        if (email != null && !currentPassword.isEmpty()) {
            AuthCredential credential = EmailAuthProvider.getCredential(email, currentPassword);
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                user.updatePassword(newPassword)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(EditProfileActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
                                                    // Tampilkan kembali info pengguna setelah pembaruan selesai
                                                    showCurrentUserInfo();
                                                } else {
                                                    Toast.makeText(EditProfileActivity.this, "Failed to update password after reauthentication", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                Toast.makeText(EditProfileActivity.this, "Reauthentication failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(EditProfileActivity.this, "Current password is required for reauthentication", Toast.LENGTH_SHORT).show();
        }
    }
}
