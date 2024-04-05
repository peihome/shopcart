package com.surya.shopcart;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;

public class ProductDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.productdetail);

        TextView title = findViewById(R.id.title);
        TextView description = findViewById(R.id.description);
        TextView price = findViewById(R.id.price);
        TextView priceText = findViewById(R.id.priceText);

        ImageView closeicon = findViewById(R.id.closeButton);
        closeicon.setOnClickListener(v-> {
            finish();
        });

        title.setText(getIntent().getStringExtra("title"));
        description.setText(getIntent().getStringExtra("description"));
        price.setText("Now: $" + getIntent().getDoubleExtra("price", 0)+"");
        priceText.setText("$"+getIntent().getDoubleExtra("price", 0)+"");

        ViewPager2 viewPager2 = findViewById(R.id.productDetailViewPager);
        ArrayList<String> images = new ArrayList<>();
        images.add(getIntent().getStringExtra("image"));

        viewPager2.setAdapter(new ProductDetailImageAdapter(images));
    }
}

