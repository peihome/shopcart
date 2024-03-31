package com.surya.shopcart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import java.util.ArrayList;

public class ProductHomePageActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    ArrayList<Product> productList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_list_horizontal);

        Product pizza = new Product("pizza", "Cheese Pizza 18 inch", "A simple garnish of fresh herbs, and you've got perfection on a plate.", 12.99);
        Product doughnut = new Product("tomato", "Chocolate Doughnut", "Indulge your taste buds with our irresistible doughnut collection, now on sale!", 6.99);
        Product pizza2 = new Product("pizza", "Cheese Pizza 28 inch", "A simple garnish of fresh herbs, and you've got perfection on a plate.", 12.99);

        productList.add(pizza);
        productList.add(doughnut);
        productList.add(pizza2);

        recyclerView = findViewById(R.id.productRecyclerView);
        adapter = new ProductAdapter(productList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }
}


/*

public class ProductHomePageActivity extends AppCompatActivity {

    ViewPager2 vPager;
    RecyclerView.Adapter adapter;
    ArrayList<Product> productList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_list_horizontal);

        Product pizza = new Product("pizza", "Cheese Pizza 18 inch", "A simple garnish of fresh herbs, and you've got perfection on a plate.", 12.99);
        Product doughnut = new Product("tomato", "Chocolate Doughnut", "Indulge your taste buds with our irresistible doughnut collection, now on sale!", 6.99);
        Product pizza2 = new Product("pizza", "Cheese Pizza 28 inch", "A simple garnish of fresh herbs, and you've got perfection on a plate.", 12.99);

        productList.add(pizza);
        productList.add(doughnut);
        productList.add(pizza2);

        vPager = findViewById(R.id.vPager);
        adapter = new ProductAdapter(productList);
        vPager.setAdapter(adapter);
    }
}

 */