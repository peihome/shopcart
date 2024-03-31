package com.surya.shopcart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //To be removed
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