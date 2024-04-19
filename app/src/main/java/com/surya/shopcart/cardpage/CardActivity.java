package com.surya.shopcart.cardpage;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.surya.shopcart.ProductHomePageActivity;
import com.surya.shopcart.R;
import com.surya.shopcart.checkout.CheckoutActivity;
import com.surya.shopcart.confirmorder.ConfirmOrderActivity;
import com.surya.shopcart.interfaces.OnGetDataListener;
import com.surya.shopcart.utils.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CardActivity extends AppCompatActivity {

    private static final String TAG = CheckoutActivity.class.getSimpleName();

    TextInputEditText holderNameET, expiryDateET, cardNumberET, cvvET;
    String holderName, cardNumber, expiryDate, cvv, userId;
    TextInputLayout cardNumberLayout;

    private static Pattern regex;

    private static HashMap<String, String> vendorVsRegex = new HashMap<>();
    public void openCartPage(MenuItem item) {
        Utils.handleMenuCLick(this, item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_page);

        // Home navigation icon
        Utils.addHomeIconNavigation(this, findViewById(R.id.topAppBar));

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        }


        holderNameET = findViewById(R.id.holderName);
        cardNumberET = findViewById(R.id.cardNumber);
        expiryDateET = findViewById(R.id.expiryDate);
        cvvET = findViewById(R.id.cvv);
        cardNumberLayout = findViewById(R.id.cardNumberLayout);
        Button continueButton = findViewById(R.id.continueButton);
        Button cancelButton = findViewById(R.id.cancelButton);

        prefillFormIfDataExists();

        continueButton.setOnClickListener(view -> {
            holderName = holderNameET.getText().toString();
            cardNumber = cardNumberET.getText().toString();
            expiryDate = expiryDateET.getText().toString();
            cvv = cvvET.getText().toString();

            boolean result = validationResult();

            if(result){

                HashMap<String, Object> cardMap = new HashMap<>();
                cardMap.put("holderName", holderName);
                cardMap.put("cardNumber", cardNumber);
                cardMap.put("expiryDate", expiryDate);
                cardMap.put("vendor", cardNumberLayout.getHint().equals("Card Number") ? "" : cardNumberLayout.getHint());
                cardMap.put("cvv", cvv);

                Utils.addMapDataToRealTimeDataBase(cardMap, Utils.getUserCardPath(userId), new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(getApplicationContext(), "Card details stored!", Toast.LENGTH_LONG).show();

                        //Proceed to card page
                        Intent cardPage = new Intent(getApplicationContext(), ConfirmOrderActivity.class);
                        startActivity(cardPage);
                        finish();

                    }
                });
            }
        });

        cancelButton.setOnClickListener(view -> {
            Intent cardPage = new Intent(getApplicationContext(), ProductHomePageActivity.class);
            startActivity(cardPage);
        });

        cardNumberET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateCardNumber(cardNumberET.getText().toString());
            }
        });

    }

    public void prefillFormIfDataExists(){
        Utils.getMapDataFromRealTimeDataBase(Utils.getUserCardPath(userId), new OnGetDataListener() {
            @Override
            public void onSuccess(HashMap<String, Object> dataMap) {
                try {
                    holderNameET.setText(dataMap.get("holderName").toString());
                    cardNumberET.setText(dataMap.get("cardNumber").toString());
                    expiryDateET.setText(dataMap.get("expiryDate").toString());
                    cvvET.setText(dataMap.get("cvv").toString());
                }catch(Exception e){
                    Log.i(TAG, e.getStackTrace().toString());
                }

            }

            @Override
            public void onFailure(Exception e) {
                Log.i(TAG, e.getStackTrace().toString());
            }
        });

        Utils.getMapDataFromRealTimeDataBase(Utils.vendorVsRegex, new OnGetDataListener() {
            @Override
            public void onSuccess(HashMap<String, Object> dataMap) {
                try {
                    for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                        vendorVsRegex.put(entry.getKey(), entry.getValue()+"");
                    }
                }catch(Exception e){
                    Log.i(TAG, e.getStackTrace().toString());
                }

            }

            @Override
            public void onFailure(Exception e) {
                Log.i(TAG, e.getStackTrace().toString());
            }
        });
    }

    public boolean validationResult(){

        if(!validateFullName(holderName)){
            Toast.makeText(this, "Please enter a valid name!", Toast.LENGTH_LONG).show();
            return false;
        }

        if(!validateCardNumber(cardNumber)){
            Toast.makeText(this, "Please enter a valid card number!", Toast.LENGTH_LONG).show();
            return false;
        }

        if(!validateExpiryDate(expiryDate)){
            Toast.makeText(this, "Please enter a valid expiry date!", Toast.LENGTH_LONG).show();
            return false;
        }

        if(!validateCVV(cvv)){
            Toast.makeText(this, "Please enter a valid cvv!", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public boolean validateCardNumber(String cardNumber){
        for (Map.Entry<String, String> entry : vendorVsRegex.entrySet()) {
            regex = Pattern.compile(entry.getValue());

            if(regex.matcher(cardNumber).matches()){
                cardNumberLayout.setHint(entry.getKey());
                return true;
            }else {
                cardNumberLayout.setHint("Card Number");
            }
        }
        return false;
    }

    public boolean validateExpiryDate(String expiryDate){
        try {
            byte month = Byte.parseByte(expiryDate.charAt(0) +""+ expiryDate.charAt(1));
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            char yearArr[] = (currentYear + "").toCharArray();
            int year = 0;
            if(expiryDate.length() >= 4){
                year = Integer.parseInt(yearArr[0] +""+ yearArr[1] + "" + expiryDate.charAt(2) +""+ expiryDate.charAt(3));
            }else {
                Toast.makeText(this, "Please enter year!", Toast.LENGTH_LONG).show();
                return false;
            }

            if(month>0 && month <=12){
                if(year <= currentYear+4 && year >= currentYear-4){
                    return true;
                }else{
                    Toast.makeText(this, "Invalid Year or Card Expired!", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(this, "Please enter a valid month!", Toast.LENGTH_LONG).show();
            }
        }catch(Exception e){
            Log.i(TAG, e.getStackTrace().toString());
        }

        return false;
    }

    public static boolean validateCVV (String cvv) {
        regex = Pattern.compile("^[0-9]{3}$");

        return regex.matcher(cvv).matches();
    }

    public static boolean validateFullName(String name) {
        String pattern = "^[A-Za-z ]{3,80}$";
        Pattern regex = Pattern.compile(pattern);

        return regex.matcher(name).matches();
    }
}