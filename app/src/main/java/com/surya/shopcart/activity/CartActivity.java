package com.surya.shopcart.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.surya.shopcart.Product;
import com.surya.shopcart.ProductDetailActivity;
import com.surya.shopcart.R;
import com.surya.shopcart.adapter.CartItemAdapter;
import com.surya.shopcart.cart.EmptyCartActivity;
import com.surya.shopcart.confirmorder.ConfirmOrderActivity;
import com.surya.shopcart.interfaces.OnGetDataListener;
import com.surya.shopcart.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartActivity extends AppCompatActivity implements CartItemAdapter.OnItemClickListener {

    private static final String TAG = Utils.class.getSimpleName();

    public RecyclerView recyclerView;
    CartItemAdapter adapter;
    private ArrayList<Product> cartItemsList;
    private static String userId;
    private static Button proceedButton;
    public TextView grandsubtotalTV;

    public void openCartPage(MenuItem item) {
        Utils.handleMenuCLick(this, item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Home navigation icon
        Utils.addHomeIconNavigation(this, findViewById(R.id.topAppBar));


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        }

        proceedButton = findViewById(R.id.buyButton);
        grandsubtotalTV = findViewById(R.id.grandsubtotal);


        proceedButton.setOnClickListener(view -> {
            Intent confirmOrderActivity = new Intent(this, ConfirmOrderActivity.class);
            startActivity(confirmOrderActivity);
        });

        handleCartItemsForView();

    }

    public void showCartItems() {
        //ViewPager2 Flyer
        if(recyclerView == null){
            recyclerView = findViewById(R.id.rvCartPage);
        }
        adapter = new CartItemAdapter(cartItemsList);
        adapter.setOnItemClickListener((CartItemAdapter.OnItemClickListener) this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        Utils.handleTotalPriceChange(userId, proceedButton, grandsubtotalTV);
    }

    @Override
    public void onItemClick(View view, int position, String type) {
        Product product = cartItemsList.get(position);

        TextView quantity;
        TextView subTotal;
        LinearLayout quantityParentLayout;
        LinearLayout detailsLayout;
        ConstraintLayout entireCard;

        byte quantityByte = 0;

        switch (type) {
            case "image":
                Intent productDetail = new Intent(this, ProductDetailActivity.class);
                productDetail.putExtra("title", product.getTitle());
                productDetail.putExtra("description", product.getDescription());
                productDetail.putExtra("price", product.getPrice());
                productDetail.putExtra("image", product.getImage());
                productDetail.putExtra("id", product.getId());
                startActivity(productDetail);

                break;
            case "reduceQuantity":
                quantityParentLayout = (LinearLayout) view.getParent();
                quantity = (TextView) quantityParentLayout.getChildAt(1);

                detailsLayout = (LinearLayout) quantityParentLayout.getParent();
                subTotal = (TextView) detailsLayout.getChildAt(2);

                quantityByte = Byte.valueOf(quantity.getText()+"");
                handleQuantityLayout(--quantityByte, quantity, subTotal, product, position, false);

                break;
            case "increaseQuantity":
                quantityParentLayout = (LinearLayout) view.getParent();
                quantity = (TextView) quantityParentLayout.getChildAt(1);

                detailsLayout = (LinearLayout) quantityParentLayout.getParent();
                subTotal = (TextView) detailsLayout.getChildAt(2);

                quantityByte = Byte.valueOf(quantity.getText()+"");
                handleQuantityLayout(++quantityByte, quantity, subTotal, product, position, true);

                break;
        }
    }

    public void handleQuantityLayout(byte quantityShort, TextView quantity, TextView subTotal, Product product, int position, boolean isIncrease) {
        if (quantityShort <= 0) {
            cartItemsList.remove(position);
            adapter.notifyItemRemoved(position);

            Utils.setProductQuantityForUser(userId, product.getId(), isIncrease, proceedButton, grandsubtotalTV, this);
        } else {
            if (quantityShort >= 20) {
                quantity.setText("20");
                subTotal.setText("$ " + Utils.getSubtotalStr(quantityShort, product.getPrice()) + " (Subtotal)");

                Utils.setProductQuantityForUser(userId, product.getId(), isIncrease, proceedButton);
                Toast.makeText(getApplicationContext(), "Reached maximum limit!", Toast.LENGTH_SHORT).show();
            } else if (quantityShort < 20) {
                quantity.setText(quantityShort + "");
                subTotal.setText("$ " + Utils.getSubtotalStr(quantityShort, product.getPrice()) +" (Subtotal)");

                Utils.setProductQuantityForUser(userId, product.getId(), isIncrease, proceedButton, grandsubtotalTV, this);
            }
        }
    }

    public void handleCartItemsForView () {
        Utils.getMapDataFromRealTimeDataBase(Utils.getUserCartItemsPath(userId), new OnGetDataListener() {
            @Override
            public void onSuccess(HashMap<String, Object> dataMap) {

                ArrayList<String> products = new ArrayList<>();

                if(dataMap != null && !dataMap.isEmpty()){
                    for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                        products.add(entry.getKey());
                    }
                }

                if(!products.isEmpty()){

                    //Fruits
                    Utils.getFireStoreDataByIds(Utils.fruits, products, new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                cartItemsList = new ArrayList<>();
                                byte quantity;
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map data = document.getData();
                                    quantity = Byte.valueOf(dataMap.get(document.getId())+"");
                                    if(quantity <= 0){
                                        continue;
                                    }

                                    cartItemsList.add(new Product(document.getId(), data.get("image") + "",
                                            data.get("title") + "",
                                            data.get("description") + "",
                                            Double.valueOf(data.get("price") + ""), null, quantity));

                                }

                                if(cartItemsList.size() != products.size()) {
                                    //Veggies
                                    Utils.getFireStoreDataByIds(Utils.veggies, products, new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                byte quantity;
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Map data = document.getData();
                                                    quantity = Byte.valueOf(dataMap.get(document.getId())+"");
                                                    if(quantity <= 0){
                                                        continue;
                                                    }

                                                    cartItemsList.add(new Product( document.getId(), data.get("image")+"",
                                                            data.get("title")+"",
                                                            data.get("description")+"",
                                                            Double.valueOf(data.get("price")+""), null, quantity));
                                                }


                                                if(cartItemsList.size() != products.size()){

                                                    //Veggies
                                                    Utils.getFireStoreDataByIds(Utils.beverages, products, new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                byte quantity;
                                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                                    Map data = document.getData();
                                                                    quantity = Byte.valueOf(dataMap.get(document.getId())+"");
                                                                    if(quantity <= 0){
                                                                        continue;
                                                                    }

                                                                    cartItemsList.add(new Product( document.getId(), data.get("image")+"",
                                                                            data.get("title")+"",
                                                                            data.get("description")+"",
                                                                            Double.valueOf(data.get("price")+""), null, quantity));
                                                                }

                                                                showCartItems();
                                                            } else {
                                                                Log.w("getData", "Error getting documents.", task.getException());
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    showCartItems();
                                                }
                                            } else {
                                                Log.w("getData", "Error getting documents.", task.getException());
                                            }
                                        }
                                    });
                                } else {
                                    showCartItems();
                                }
                            } else {
                                Log.w("getData", "Error getting documents.", task.getException());
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                Intent emptyCartIntent = new Intent(getApplicationContext(), EmptyCartActivity.class);
                startActivity(emptyCartIntent);
                finish();
            }
        });
    }
}