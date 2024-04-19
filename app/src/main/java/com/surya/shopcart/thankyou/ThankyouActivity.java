package com.surya.shopcart.thankyou;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.surya.shopcart.ProductHomePageActivity;
import com.surya.shopcart.R;
import com.surya.shopcart.confirmorder.ConfirmOrderActivity;
import com.surya.shopcart.utils.Utils;

public class ThankyouActivity extends AppCompatActivity {


    String userId;
    public void openCartPage(MenuItem item) {
        Utils.handleMenuCLick(this, item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thankyou);

        // Home navigation icon
        Utils.addHomeIconNavigation(this, findViewById(R.id.topAppBar));


        Button homePage = findViewById(R.id.homePage);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            userId = currentUser.getUid();
        }

        homePage.setOnClickListener(view -> {
            //Proceed to home page
            Intent productPage = new Intent(getApplicationContext(), ProductHomePageActivity.class);
            startActivity(productPage);
            finish();
        });

    }
}