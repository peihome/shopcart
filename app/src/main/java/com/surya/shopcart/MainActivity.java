package com.surya.shopcart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.firebase.FirebaseApp;
import com.surya.shopcart.utils.Utils;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        //To be removed
        //Utils.getProducts();
        startActivity(new Intent(this, ProductHomePageActivity.class));

        //Login Page Activity
        Button clickToStart = findViewById(R.id.clickToStart);
        Intent loginPage = new Intent(this, LoginActivity.class);
        clickToStart.setOnClickListener(view -> {
            startActivity(loginPage);
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.i("OnStart", "This is the Start Phase");
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.i("OnPause", "This is the Pause Phase");
    }
}