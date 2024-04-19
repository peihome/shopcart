package com.surya.shopcart.confirmorder;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.surya.shopcart.Product;
import com.surya.shopcart.ProductDetailActivity;
import com.surya.shopcart.R;
import com.surya.shopcart.adapter.CartItemAdapter;
import com.surya.shopcart.cardpage.CardActivity;
import com.surya.shopcart.checkout.CheckoutActivity;
import com.surya.shopcart.interfaces.OnGetDataListener;
import com.surya.shopcart.thankyou.ThankyouActivity;
import com.surya.shopcart.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConfirmOrderActivity extends AppCompatActivity implements CartItemAdapter.OnItemClickListener {

    String userId;

    public RecyclerView recyclerView;
    CartItemAdapter adapter;
    private ArrayList<Product> cartItemsList;

    TextView nameTV, streetAddressTV, addressLine2TV, holderNameTV, vendorTV, cardNumberTV, orderTotalTV, taxTV, subTotalTV, discountTV;

    Button changeCard, changeAddress, buyButton;
    public void openCartPage(MenuItem item) {
        Utils.handleMenuCLick(this, item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        // Home navigation icon
        Utils.addHomeIconNavigation(this, findViewById(R.id.topAppBar));

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            userId = currentUser.getUid();
        }

        //Buttons
        changeCard = findViewById(R.id.changeCard);
        changeAddress = findViewById(R.id.changeAddress);
        buyButton = findViewById(R.id.buyButton);

        //Textviews
        nameTV = findViewById(R.id.name);
        streetAddressTV = findViewById(R.id.streetAddress);
        addressLine2TV = findViewById(R.id.addressLine2);
        holderNameTV = findViewById(R.id.holderName);
        vendorTV = findViewById(R.id.vendor);
        cardNumberTV = findViewById(R.id.cardNumber);
        orderTotalTV = findViewById(R.id.orderTotal);
        taxTV = findViewById(R.id.tax);
        subTotalTV = findViewById(R.id.grandTotal);
        discountTV = findViewById(R.id.discount);

        populateCardDetails();
        populateTotalPrice();
        populateAddressFields();

        changeCard.setOnClickListener(view -> {
            Intent changeCardIntent = new Intent(this, CardActivity.class);
            startActivity(changeCardIntent);
            finish();
        });

        changeAddress.setOnClickListener(view -> {
            Intent changeAddress = new Intent(this, CheckoutActivity.class);
            startActivity(changeAddress);
            finish();
        });

        buyButton.setOnClickListener(view -> {

            Utils.deleteDataFromRealTimeDatabase(Utils.getUserCartItemsPath(userId), new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Intent thakyouActivity = new Intent(getApplicationContext(), ThankyouActivity.class);
                    startActivity(thakyouActivity);
                    finish();

                }
            });
        });

    }

    public void populateAddressFields(){
        Utils.getMapDataFromRealTimeDataBase(Utils.getUserAddressPath(userId), new OnGetDataListener() {
            @Override
            public void onSuccess(HashMap<String, Object> dataMap) {
                nameTV.setText(dataMap.get("firstName")+" "+dataMap.get("lastName"));
                streetAddressTV.setText(dataMap.get("streetAddress")+" F.No "+dataMap.get("floorNumber"));
                addressLine2TV.setText(dataMap.get("city")+", "+dataMap.get("province")+", "+dataMap.get("zipcode"));
            }

            @Override
            public void onFailure(Exception e) {
//                Toast.makeText(getApplicationContext(), "Please enter your shipping details!", Toast.LENGTH_SHORT).show();
                Intent cardIntent = new Intent(getApplicationContext(), CheckoutActivity.class);
                startActivity(cardIntent);
                finish();
            }
        });
    }

    public void populateCardDetails() {
        Utils.getMapDataFromRealTimeDataBase(Utils.getUserCardPath(userId), new OnGetDataListener() {
            @Override
            public void onSuccess(HashMap<String, Object> dataMap) {

                holderNameTV.setText(dataMap.get("holderName")+"");
                vendorTV.setText(dataMap.get("vendor")+"");
                if((dataMap.get("vendor")+"").isEmpty()){
                    vendorTV.setVisibility(View.GONE);
                }else{
                    vendorTV.setVisibility(View.VISIBLE);
                }
                cardNumberTV.setText(Utils.getMaskedCardNumberForDisplay(dataMap.get("cardNumber")+""));

            }

            @Override
            public void onFailure(Exception e) {

               // Toast.makeText(getApplicationContext(), "Please enter your card details!", Toast.LENGTH_SHORT).show();
                Intent cardIntent = new Intent(getApplicationContext(), CardActivity.class);
                startActivity(cardIntent);
                finish();

            }
        });
    }

    public void populateTotalPrice(){
        Utils.handleTotalPriceChange(userId,null, findViewById(R.id.orderTotal), findViewById(R.id.tax), findViewById(R.id.grandTotal), null, discountTV);
        handleCartItemsForView();
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

            }
        });
    }

    public void showCartItems() {
        //ViewPager2 Flyer
        recyclerView = findViewById(R.id.rvCartPage);
        adapter = new CartItemAdapter(cartItemsList, false);
        adapter.setOnItemClickListener((CartItemAdapter.OnItemClickListener) this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        Utils.handleTotalPriceChange(userId, findViewById(R.id.buyButton));
    }

    @Override
    public void onItemClick(View view, int position, String type) {
        Product product = cartItemsList.get(position);

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
        }
    }
}