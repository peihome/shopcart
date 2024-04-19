package com.surya.shopcart;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.surya.shopcart.confirmorder.ConfirmOrderActivity;
import com.surya.shopcart.interfaces.OnGetDataListener;
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
    static Handler handler = new Handler();
    ArrayList<Product> productList = new ArrayList<>();
    ArrayList<Flyer> flyersList = new ArrayList<>();

    private static int currentPage = 0;
    private static Timer scrollTimer;
    private static TimerTask scrollTimerTask;
    private static String userId;

    private static Runnable updateRunnable;

    public void openCartPage(MenuItem item) {
        Utils.handleMenuCLick(this, item);
    }

    @Override
    public void onBackPressed() {
        if(false){
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_list_horizontal);


        // Home navigation icon
        Utils.addHomeIconNavigation(this, findViewById(R.id.topAppBar));

        handleFLyers();
        handleProducts();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            userId = currentUser.getUid();
        }
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
                        productList.add(new Product( document.getId(), data.get("image")+"",
                                data.get("title")+"",
                                data.get("description")+"",
                                Double.valueOf(data.get("price")+""), path));
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
                    vPager.setAdapter(adapter);

                    //autoSlideFlyers(adapter, vPager);

                } else {
                    Log.w("getData", "Error getting documents.", task.getException());
                }
            }
        });
    }

    public static void autoSlideFlyers(RecyclerView.Adapter adapter, ViewPager2 vPager){
        /*After setting the adapter use the timer */
        int totalPages = adapter.getItemCount();
        updateRunnable = new Runnable() {
            public void run() {
                if (currentPage == totalPages) {
                    currentPage = 0;
                }
                vPager.setCurrentItem(currentPage++, true);
            }
        };

        scrollTimer = new Timer();
        scrollTimerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(updateRunnable);
            }
        };

        //scrollTimer.schedule(scrollTimerTask, 5000, 5000);

        vPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    startTimers(10000, 5000);
                }
            }
        });
    }

    @Override
    public void onItemClick(View view, int position, ArrayList<Product> productList, String type) {
        Product product = productList.get(position);

        TextView quantity;
        LinearLayout itemDetailLayout = null;
        RelativeLayout itemDetailLayoutParent;
        byte quantityByte = 0;

        switch(type) {

            case "image":
                Intent productDetail = new Intent(this, ProductDetailActivity.class);
                productDetail.putExtra("title", product.getTitle());
                productDetail.putExtra("description", product.getDescription());
                productDetail.putExtra("price", product.getPrice());
                productDetail.putExtra("image", product.getImage());
                productDetail.putExtra("id", product.getId());
                startActivity(productDetail);
                break;

            case "addButton":
                itemDetailLayoutParent = (RelativeLayout) view.getParent();
                itemDetailLayout = (LinearLayout) itemDetailLayoutParent.getChildAt(2);
                itemDetailLayout.setVisibility(View.VISIBLE);
                view.setVisibility(View.GONE);

                TextView quantityView =  (TextView) itemDetailLayout.getChildAt(1);
                handleQuantityLayout((byte) 1, itemDetailLayout, quantityView, product, true);

                break;
            case "reduceQuantity":
                itemDetailLayout = (LinearLayout) view.getParent();
                quantity = (TextView) itemDetailLayout.getChildAt(1);
                quantityByte = Byte.valueOf(quantity.getText()+"");
                handleQuantityLayout(--quantityByte, itemDetailLayout, quantity, product, false);

                break;
            case "increaseQuantity":
                itemDetailLayout = (LinearLayout) view.getParent();
                quantity = (TextView) itemDetailLayout.getChildAt(1);
                quantityByte = Byte.valueOf(quantity.getText()+"");
                handleQuantityLayout(++quantityByte, itemDetailLayout, quantity, product, true);

                break;
        }
    }

    public void handleQuantityLayout(byte quantity, LinearLayout itemDetailLayout, TextView quantityView, Product product, boolean isIncrease) {

        Button addButton = (Button) ((RelativeLayout) itemDetailLayout.getParent()).getChildAt(1);

        Utils.getMapDataFromRealTimeDataBase(Utils.getUserCartItemsPath(userId), new OnGetDataListener() {
            @Override
            public void onSuccess(HashMap<String, Object> dataMap) {

                if(!dataMap.containsKey(quantityView.getTag()+"")){
                    addButton.setVisibility(View.GONE);
                    quantityView.setText("1");
                    itemDetailLayout.setVisibility(View.VISIBLE);

                    Utils.setProductQuantityForUser(userId, quantityView.getTag()+"", isIncrease, null);

                    return;
                }

                byte quantityFromRemote = Byte.valueOf(dataMap.get(quantityView.getTag()+"")+"");
                if(isIncrease){
                    quantityFromRemote++;
                }else {
                    quantityFromRemote--;
                }

                if (quantity <= 0 || quantityFromRemote <= 0) {
                    addButton.setVisibility(View.VISIBLE);
                    quantityView.setText("1");
                    itemDetailLayout.setVisibility(View.GONE);

                    Utils.setProductQuantityForUser(userId, quantityView.getTag()+"", isIncrease, null);
                } else if (quantity > 20 || quantityFromRemote > 20) {
                    Toast.makeText(getApplicationContext(), "Reached maximum limit!", Toast.LENGTH_SHORT).show();
                } else {
                    quantityView.setText(quantity + "");
                    Utils.setProductQuantityForUser(userId, quantityView.getTag()+"", isIncrease, null);
                }
            }

            @Override
            public void onFailure(Exception e) {
                addButton.setVisibility(View.GONE);
                quantityView.setText("1");
                itemDetailLayout.setVisibility(View.VISIBLE);

                Utils.setProductQuantityForUser(userId, quantityView.getTag()+"", isIncrease, null);

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelTimers();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cancelTimers();
    }

    private static void cancelTimers() {
        if (scrollTimer != null) {
            scrollTimer.cancel();
            scrollTimer = null;
        }
        if (scrollTimerTask != null) {
            scrollTimerTask.cancel();
            scrollTimerTask = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //startTimers(5000, 5000);
    }

    private static void startTimers(long delay, long period) {
        cancelTimers();
        if(scrollTimer == null){
            scrollTimer = new Timer();
        }else {
            scrollTimer = new Timer();
        }
        scrollTimerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(updateRunnable);
            }
        };
        scrollTimer.schedule(scrollTimerTask, delay, period);
    }
}