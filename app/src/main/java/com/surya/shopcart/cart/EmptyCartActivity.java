package com.surya.shopcart.cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.surya.shopcart.ProductHomePageActivity;
import com.surya.shopcart.R;
import com.surya.shopcart.utils.Utils;

public class EmptyCartActivity extends AppCompatActivity {


    public void openCartPage(MenuItem item) {
        Utils.handleMenuCLick(this, item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_cart);

        // Home navigation icon
        Utils.addHomeIconNavigation(this, findViewById(R.id.topAppBar));

        Button homePage = findViewById(R.id.homePage);
        homePage.setOnClickListener(view -> {
            Intent homePageIntent = new Intent(this, ProductHomePageActivity.class);
            startActivity(homePageIntent);
            finish();
        });
    }
}