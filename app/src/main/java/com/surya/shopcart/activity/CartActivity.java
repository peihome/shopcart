package com.surya.shopcart.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.surya.shopcart.FlyerAdapter;
import com.surya.shopcart.Product;
import com.surya.shopcart.ProductAdapter;
import com.surya.shopcart.ProductDetailActivity;
import com.surya.shopcart.ProductHomePageActivity;
import com.surya.shopcart.R;
import com.surya.shopcart.adapter.CartItemAdapter;
import com.surya.shopcart.interfaces.OnGetDataListener;
import com.surya.shopcart.utils.Utils;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartActivity extends AppCompatActivity implements CartItemAdapter.OnItemClickListener {

    private static final String TAG = Utils.class.getSimpleName();

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    private ArrayList<Product> cartItemsList;
    private static String userId;

    private Button buyButton;

    public void openCartPage(MenuItem item) {
        switch(item.getItemId()){
            case R.id.cartIcon:
                if (this.getClass().equals(CartActivity.class)) {
                    return;
                }
                startActivity(new Intent(this, CartActivity.class));
                break;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Toolbar menuBar = findViewById(R.id.topAppBar);
        menuBar.setNavigationIcon(R.drawable.ic_home);
        menuBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!getClass().equals(ProductHomePageActivity.class)) {
                    startActivity(new Intent(getApplicationContext(), ProductHomePageActivity.class));
                }
            }
        });


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        buyButton = findViewById(R.id.buyButton);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Utils.getUserDetailPath(currentUser.getUid()));

        Utils.getMapDataFromRealTimeDataBase(Utils.getUserDetailPath(currentUser.getUid()), new OnGetDataListener() {
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
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map data = document.getData();
                                    boolean quantity = cartItemsList.add(new Product(document.getId(), data.get("image") + "",
                                            data.get("title") + "",
                                            data.get("description") + "",
                                            Double.valueOf(data.get("price") + ""), null, Byte.valueOf(dataMap.get(document.getId())+"")));
                                }

                                if(cartItemsList.size() != products.size()) {
                                    //Veggies
                                    Utils.getFireStoreDataByIds(Utils.veggies, products, new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Map data = document.getData();
                                                    cartItemsList.add(new Product( document.getId(), data.get("image")+"",
                                                            data.get("title")+"",
                                                            data.get("description")+"",
                                                            Double.valueOf(data.get("price")+""), null, Byte.valueOf(dataMap.get(document.getId())+"")));
                                                }


                                                if(cartItemsList.size() != products.size()){

                                                    //Veggies
                                                    Utils.getFireStoreDataByIds(Utils.beverages, products, new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                                    Map data = document.getData();
                                                                    cartItemsList.add(new Product( document.getId(), data.get("image")+"",
                                                                            data.get("title")+"",
                                                                            data.get("description")+"",
                                                                            Double.valueOf(data.get("price")+""), null, Byte.valueOf(dataMap.get(document.getId())+"")));
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

            }
        });
    }

    public void showCartItems() {
        //ViewPager2 Flyer
        recyclerView = findViewById(R.id.rvCartPage);
        adapter = new CartItemAdapter(cartItemsList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        buyButton.setText("Proceed to Buy ($12)");
    }

    @Override
    public void onItemClick(View view, int position, String type) {
        Product product = cartItemsList.get(position);

        TextView quantity;
        TextView subTotal;
        LinearLayout quantityParentLayout;
        LinearLayout detailsLayout;
        ConstraintLayout entireCard;

        short quantityShort = 0;

        switch (type) {
            case "image":
                Intent productDetail = new Intent(this, ProductDetailActivity.class);
                productDetail.putExtra("title", product.getTitle());
                productDetail.putExtra("description", product.getDescription());
                productDetail.putExtra("price", product.getPrice());
                productDetail.putExtra("image", product.getImage());
                startActivity(productDetail);

                break;
            case "reduceQuantity":
                quantityParentLayout = (LinearLayout) view.getParent();
                quantity = (TextView) quantityParentLayout.getChildAt(1);

                detailsLayout = (LinearLayout) quantityParentLayout.getParent();
                subTotal = (TextView) detailsLayout.getChildAt(2);

                quantityShort = Short.valueOf(quantity.getText()+"");

                handleQuantityLayout(--quantityShort, quantity, subTotal, product, position);

                break;
            case "increaseQuantity":
                quantityParentLayout = (LinearLayout) view.getParent();
                quantity = (TextView) quantityParentLayout.getChildAt(1);

                detailsLayout = (LinearLayout) quantityParentLayout.getParent();
                subTotal = (TextView) detailsLayout.getChildAt(2);

                quantityShort = Short.valueOf(quantity.getText()+"");

                handleQuantityLayout(++quantityShort, quantity, subTotal, product, position);

                break;
        }

        buyButton.setText("Proceed to buy");

        Intent productDetail = new Intent(this, ProductDetailActivity.class);

        productDetail.putExtra("title", product.getTitle());
        productDetail.putExtra("description", product.getDescription());
        productDetail.putExtra("price", product.getPrice());
        productDetail.putExtra("image", product.getImage());

        startActivity(productDetail);
    }

    public void handleQuantityLayout(short quantityShort, TextView quantity, TextView subTotal, Product product, int position) {
        if (quantityShort <= 0) {
            cartItemsList.remove(position);
            adapter.notifyItemRemoved(position);

            Utils.setProductQuantityForUser(userId, product.getId(), quantityShort);
        } else {
            if (quantityShort >= 20) {
                quantity.setText("20");
                subTotal.setText(Math.round(quantityShort * product.getPrice())+"");

                Utils.setProductQuantityForUser(userId, product.getId(), quantityShort);
                Toast.makeText(getApplicationContext(), "Reached maximum limit!", Toast.LENGTH_LONG).show();
            } else if (quantityShort < 20) {
                quantity.setText(quantityShort + "");
                subTotal.setText(Math.round(quantityShort * product.getPrice())+"");

                Utils.setProductQuantityForUser(userId, product.getId(), quantityShort);
            }
        }
    }



    public void getCartItems(String userId) {

        String path = Utils.getUserDetailPath(userId);
        Utils.getFireStoreDataFromSubCollection(path, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    cartItemsList = new ArrayList<>();

                    DocumentSnapshot document = task.getResult();
                    Log.i(TAG, document.get("ADnoMz2LA07LtAgdGSE8")+"");

                    if(document.exists()){
                        Log.i(TAG, document.getData()+"");
                    }


                } else {
                    Log.w("getData", "Error getting documents.", task.getException());
                }
            }
        });
    }
}