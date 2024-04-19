package com.surya.shopcart.checkout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.surya.shopcart.R;
import com.surya.shopcart.cardpage.CardActivity;
import com.surya.shopcart.interfaces.OnGetDataListener;
import com.surya.shopcart.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class CheckoutActivity extends AppCompatActivity {

    private static final String TAG = CheckoutActivity.class.getSimpleName();
    private static String firstName, lastName, streetAddress, floorNumber, city, zipcode, province;
    private static boolean validationResult;

    private static String pattern;
    private static Pattern regex;

    private String userId;
    MaterialAutoCompleteTextView proviceTextView;
    Button continueButton;
    TextInputEditText firstNameET;
    TextInputEditText lastNameET;
    TextInputEditText streetAddressET;
    TextInputEditText floorNumberET;
    TextInputEditText cityET;
    TextInputEditText zipcodeET;
    Button cancelButton;
    public void openCartPage(MenuItem item) {
        Utils.handleMenuCLick(this, item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_form);

        // Home navigation icon
        Utils.addHomeIconNavigation(this, findViewById(R.id.topAppBar));

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        }

        proviceTextView = findViewById(R.id.proviceTextView);
        continueButton = findViewById(R.id.continueButton);
        firstNameET = findViewById(R.id.firstName);
        lastNameET = findViewById(R.id.lastName);
        streetAddressET = findViewById(R.id.streetAddress);
        floorNumberET = findViewById(R.id.floorNumber);
        cityET = findViewById(R.id.city);
        zipcodeET = findViewById(R.id.zipcode);
        cancelButton = findViewById(R.id.cancelButton);

        setValuesForProvince();
        prefillFormIfDataExists();


        continueButton.setOnClickListener(view -> {
            firstName = firstNameET.getText().toString().trim();
            lastName = lastNameET.getText().toString().trim();
            streetAddress = streetAddressET.getText().toString().trim();
            floorNumber = floorNumberET.getText().toString().trim();
            city = cityET.getText().toString().trim();
            zipcode = zipcodeET.getText().toString().trim().toUpperCase();
            province = proviceTextView.getText().toString().trim();

            boolean result = validationResult(firstName, lastName, streetAddress, floorNumber, city, zipcode, province);

            if(result){
                HashMap<String, Object> addressMap = new HashMap<>();
                addressMap.put("firstName", firstName);
                addressMap.put("lastName", lastName);
                addressMap.put("streetAddress", streetAddress);
                addressMap.put("floorNumber", floorNumber);
                addressMap.put("city", city);
                addressMap.put("zipcode", zipcode);
                addressMap.put("province", province);

                Utils.addMapDataToRealTimeDataBase(addressMap, Utils.getUserAddressPath(userId), new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        //Proceed to card page
                        Intent cardPage = new Intent(getApplicationContext(), CardActivity.class);
                        startActivity(cardPage);

                    }
                });
            }
        });

        cancelButton.setOnClickListener(view -> {
            finish();
        });

    }

    public void setValuesForProvince(){
        TextInputLayout provinceTextInputLayout = findViewById(R.id.province);

        List<String> items = Arrays.asList("Alberta", "British Columbia", "Manitoba", "New Brunswick", "Newfoundland", "Northwest Territories", "Nova Scotia", "Nunavut", "Ontario", "Prince Edward Island", "Quebec", "Saskatchewan", "Yukon");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.provice_layout, items);

        MaterialAutoCompleteTextView provinceAutoCompleteTextView = provinceTextInputLayout.findViewById(R.id.proviceTextView);

        provinceAutoCompleteTextView.setAdapter(adapter);
    }

    public boolean validationResult(String firstName, String lastName, String streetAddress, String floorNumber, String city, String zipcode, String province){

        if(!validateName(firstName)){
            Toast.makeText(this, "Please enter a valid first name!", Toast.LENGTH_LONG).show();
            return false;
        }

        if(!validateName(lastName)){
            Toast.makeText(this, "Please enter a valid last name!", Toast.LENGTH_LONG).show();
            return false;
        }

        if(!validateStreetAddress(streetAddress)){
            Toast.makeText(this, "Please enter a valid address!", Toast.LENGTH_LONG).show();
            return false;
        }

        if(!validateFloorNumber(floorNumber)){
            Toast.makeText(this, "Please enter a valid floor number!", Toast.LENGTH_LONG).show();
            return false;
        }

        if(!validateCity(city)){
            Toast.makeText(this, "Please enter a valid city name!", Toast.LENGTH_LONG).show();
            return false;
        }

        if(!validateProvince(province)){
            Toast.makeText(this, "Please choose a province!", Toast.LENGTH_LONG).show();
            return false;
        }

        if(!validateZipCode(zipcode)){
            Toast.makeText(this, "Please enter a proper zipcode!", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    public static boolean validateName(String name) {
        String pattern = "^[A-Za-z]{3,50}$";
        Pattern regex = Pattern.compile(pattern);

        return regex.matcher(name).matches();
    }

    public static boolean validateStreetAddress(String streetAddress) {
        pattern = "^[\\w+(\\s\\w+){2,}]{5,100}$";
        regex = Pattern.compile(pattern);

        return regex.matcher(streetAddress).matches();
    }

    public static boolean validateFloorNumber (String floorNumber) {

        try {
            if(floorNumber.isEmpty()){
                return true;
            }

            byte floorNumberByte = Byte.parseByte(floorNumber);

            if (floorNumberByte >= 0 && floorNumberByte <= 100) {
                return true;
            }
        } catch(Exception e) {
            Log.i(TAG, e.getStackTrace().toString());
        }

        return false;

    }

    public static boolean validateCity (String city) {

        pattern = "^[[a-zA-Z]+(?:[\\s-][a-zA-Z]+)*]{5,40}$";
        regex = Pattern.compile(pattern);

        return regex.matcher(city).matches();

    }


    public static boolean validateZipCode (String zipcode) {

        pattern = "^[ABCEGHJKLMNPRSTVXY]\\d[ABCEGHJKLMNPRSTVWXYZ][ -]?\\d[ABCEGHJKLMNPRSTVWXYZ]\\d$";
        regex = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);

        return regex.matcher(zipcode).matches();

    }

    public static boolean validateProvince(String province) {

        if(!province.isEmpty()){
            return true;
        }

        return false;
    }

    public void prefillFormIfDataExists(){
        Utils.getMapDataFromRealTimeDataBase(Utils.getUserAddressPath(userId), new OnGetDataListener() {
            @Override
            public void onSuccess(HashMap<String, Object> dataMap) {
                try {
                    firstNameET.setText(dataMap.get("firstName").toString());
                    lastNameET.setText(dataMap.get("lastName").toString());
                    streetAddressET.setText(dataMap.get("streetAddress").toString());
                    floorNumberET.setText(dataMap.get("floorNumber").toString());
                    cityET.setText(dataMap.get("city").toString());
                    proviceTextView.setText(dataMap.get("province").toString());
                    zipcodeET.setText(dataMap.get("zipcode").toString());
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

}