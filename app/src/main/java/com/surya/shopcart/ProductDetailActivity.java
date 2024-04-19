package com.surya.shopcart;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.surya.shopcart.interfaces.OnGetDataListener;
import com.surya.shopcart.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductDetailActivity extends AppCompatActivity {

    public static String userId;

    public void openCartPage(MenuItem item) {
        Utils.handleMenuCLick(this, item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.productdetail);

        // Home navigation icon
        Utils.addHomeIconNavigation(this, findViewById(R.id.topAppBar));

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        }

        TextView title = findViewById(R.id.title);
        TextView description = findViewById(R.id.description);
        TextView price = findViewById(R.id.price);
        TextView priceText = findViewById(R.id.priceText);
        Button addButton = findViewById(R.id.addButton);
        TextView increaseQuantity = findViewById(R.id.increaseQuantity);
        TextView reduceQuantity = findViewById(R.id.reduceQuantity);
        TextView quantity = findViewById(R.id.quantity);
        LinearLayout quantityLayout = findViewById(R.id.quantityLayout);
        String productId = getIntent().getStringExtra("id");

        ImageView closeicon = findViewById(R.id.closeButton);
        closeicon.setOnClickListener(v-> {
            finish();
        });

        addButton.setOnClickListener(view1 -> {
            onItemClick(addButton, quantityLayout, quantity, productId, "addButton");
        });

        reduceQuantity.setOnClickListener(view1 -> {
            onItemClick(addButton, quantityLayout, quantity, productId, "reduceQuantity");
        });

        increaseQuantity.setOnClickListener(view1 -> {
            onItemClick(addButton, quantityLayout, quantity, productId, "increaseQuantity");
        });


        title.setText(getIntent().getStringExtra("title"));
        description.setText(getIntent().getStringExtra("description"));
        price.setText("Now: $ " + getIntent().getDoubleExtra("price", 0)+" /lb");
        priceText.setText("$ "+getIntent().getDoubleExtra("price", 0)+" /lb");

        ViewPager2 viewPager2 = findViewById(R.id.productDetailViewPager);
        ArrayList<String> images = new ArrayList<>();
        images.add(getIntent().getStringExtra("image"));

        viewPager2.setAdapter(new ProductDetailImageAdapter(images));
    }

    public void onItemClick(Button addButton, LinearLayout quantityLayout, TextView quantity, String productId, String type) {
        byte quantityByte;
        switch(type) {
            case "addButton":
                handleQuantityLayout((byte) 1, addButton, quantityLayout, quantity, productId, true);
                break;
            case "reduceQuantity":
                quantityByte = Byte.valueOf(quantity.getText()+"");
                handleQuantityLayout(--quantityByte, addButton, quantityLayout, quantity, productId, false);

                break;
            case "increaseQuantity":
                quantityByte = Byte.valueOf(quantity.getText()+"");
                handleQuantityLayout(++quantityByte, addButton, quantityLayout, quantity, productId, true);

                break;
        }
    }

    public void handleQuantityLayout(byte quantity, Button addButton, LinearLayout quantityLayout, TextView quantityView, String productId,  boolean isIncrease) {

        Utils.getMapDataFromRealTimeDataBase(Utils.getUserCartItemsPath(userId), new OnGetDataListener() {
            @Override
            public void onSuccess(HashMap<String, Object> dataMap) {

                byte quantityFromRemote = Byte.valueOf(dataMap.get(productId)+"");
                if(isIncrease){
                    quantityFromRemote++;
                }else {
                    quantityFromRemote--;
                }

                if (quantity <= 0 || quantityFromRemote <= 0) {
                    addButton.setVisibility(View.VISIBLE);
                    quantityView.setText("1");
                    quantityLayout.setVisibility(View.GONE);

                    Utils.setProductQuantityForUser(userId, productId, isIncrease, null);
                } else if (quantity > 20 || quantityFromRemote > 20) {
                    //Utils.setProductQuantityForUser(userId, productId, isIncrease, null);
                    Toast.makeText(getApplicationContext(), "Reached maximum limit!", Toast.LENGTH_SHORT).show();
                } else {
                    quantityView.setText(quantity + "");
                    addButton.setVisibility(View.GONE);
                    quantityLayout.setVisibility(View.VISIBLE);
                    Utils.setProductQuantityForUser(userId, productId, isIncrease, null);
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

}



