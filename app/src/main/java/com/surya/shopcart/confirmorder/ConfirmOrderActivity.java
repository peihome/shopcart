package com.surya.shopcart.confirmorder;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.surya.shopcart.R;
import com.surya.shopcart.activity.CartActivity;
import com.surya.shopcart.interfaces.OnGetDataListener;
import com.surya.shopcart.utils.Utils;

import java.util.HashMap;

import okhttp3.internal.Util;

public class ConfirmOrderActivity extends AppCompatActivity {

    String userId;

    TextView nameTV, streetAddressTV, addressLine2TV, holderNameTV, vendorTV, cardNumberTV, orderTotalTV, taxTV, subTotalTV;

    Button changeCard, changeAddress;
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

        //Textviews
        nameTV = findViewById(R.id.name);
        streetAddressTV = findViewById(R.id.streetAddress);
        addressLine2TV = findViewById(R.id.addressLine2);
        holderNameTV = findViewById(R.id.holderName);
        vendorTV = findViewById(R.id.vendor);
        cardNumberTV = findViewById(R.id.cardNumber);
        orderTotalTV = findViewById(R.id.orderTotal);
        taxTV = findViewById(R.id.tax);
        subTotalTV = findViewById(R.id.subTotal);


        populateAddressFields();
        populateCardDetails();
        populateTotalPrice();

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

            }
        });
    }

    public void populateTotalPrice(){
        
    }

}