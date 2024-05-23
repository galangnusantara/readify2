package com.example.readify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class terms_condition extends AppCompatActivity {

    private TextView tvContent;
    private Button btnAccept, btnDecline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms_condition);

        tvContent = findViewById(R.id.tvContent);
        btnAccept = findViewById(R.id.btnAccept);
        btnDecline = findViewById(R.id.btnDecline);

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptTerms();
            }
        });

        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declineTerms();
            }
        });
    }

    private void acceptTerms() {
        // Intent to go back to MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void declineTerms() {
        // Close the application
        finish();
        System.exit(0);
    }
}
