package com.surya.shopcart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.surya.shopcart.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ProductHomePageActivity extends AppCompatActivity implements ProductAdapter.OnItemClickListener{

    ViewPager2 vPager;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    Handler handler = new Handler();
    ArrayList<Product> productList = new ArrayList<>();
    ArrayList<Flyer> flyersList = new ArrayList<>();

    private static int currentPage = 0;
    private static Timer scrollTimer;
    private static TimerTask scrollTimerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_list_horizontal);

        handleFLyers();
        handleProducts();

    }

    private void handleProducts(){
        handleProduct(Utils.fruits, R.id.productRecyclerView);
        handleProduct(Utils.veggies, R.id.vegetableRV);
        handleProduct(Utils.beverages, R.id.juiceRV);
    }

    private void handleProduct(String path, int viewId){
        Context currentContext = this;
        Utils.getFireStoreData(path, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    productList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map data = document.getData();
                        productList.add(new Product(data.get("image")+"",
                                data.get("title")+"",
                                data.get("description")+"",
                                Double.valueOf(data.get("price")+"")));
                    }

                    recyclerView = findViewById(viewId);
                    ProductAdapter productAdapter = new ProductAdapter(productList);
                    productAdapter.setOnItemClickListener((ProductAdapter.OnItemClickListener) currentContext);
                    recyclerView.setAdapter(productAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(currentContext, LinearLayoutManager.HORIZONTAL, false));

                } else {
                    Log.w("getData", "Error getting documents.", task.getException());
                }
            }
        });
    }

    private void handleFLyers(){
        Utils.getFireStoreData(Utils.flyerImages, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        flyersList.add(new Flyer(document.getData().get("image")+""));
                    }

                    //ViewPager2 Flyer
                    vPager = findViewById(R.id.vPager);
                    adapter = new FlyerAdapter(flyersList);
                    vPager.setAdapter(new FlyerAdapter(flyersList));

                    autoSlideFlyers(adapter, vPager, handler);

                } else {
                    Log.w("getData", "Error getting documents.", task.getException());
                }
            }
        });
    }

    public static void autoSlideFlyers(RecyclerView.Adapter adapter, ViewPager2 vPager, Handler handler){
        /*After setting the adapter use the timer */
        int totalPages = adapter.getItemCount();
        final Runnable updateRunnable = new Runnable() {
            public void run() {
                if (currentPage == totalPages) {
                    currentPage = 0;
                }
                vPager.setCurrentItem(currentPage++, true);
            }
        };

        scrollTimer = new Timer();
        scrollTimerTask = new TimerTask() { // task to be scheduled
            @Override
            public void run() {
                handler.post(updateRunnable);
            }
        };

        scrollTimer.schedule(scrollTimerTask, 5000, 5000);

        // Set a listener for user interaction with ViewPager2
        vPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (state == ViewPager2.SCROLL_STATE_DRAGGING || state == ViewPager2.SCROLL_STATE_SETTLING) {
                    // User is interacting with the ViewPager2, so pause the timer
                    scrollTimerTask.cancel();
                } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    // ViewPager2 is idle, restart the timer
                    scrollTimerTask = new TimerTask() {
                        @Override
                        public void run() {
                            handler.post(updateRunnable);
                        }
                    };
                    scrollTimer.schedule(scrollTimerTask, 5000, 5000);
                }
            }
        });
    }

    @Override
    public void onItemClick(int position, ArrayList<Product> productList) {
        Product product = productList.get(position);

        Intent productDetail = new Intent(this, ProductDetailActivity.class);

        productDetail.putExtra("title", product.getTitle());
        productDetail.putExtra("description", product.getDescription());
        productDetail.putExtra("price", product.getPrice());
        productDetail.putExtra("image", product.getImage());

        startActivity(productDetail);

    }
}