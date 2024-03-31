package com.surya.shopcart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class OTPActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        TextView loginTextView = findViewById(R.id.createAccount);
        Intent loginActivityIntent = new Intent(this, LoginActivity.class);
        loginTextView.setOnClickListener(view -> {
            startActivity(loginActivityIntent);
        });
    }
}