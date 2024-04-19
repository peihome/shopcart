package com.surya.shopcart.utils;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.surya.shopcart.ProductHomePageActivity;
import com.surya.shopcart.R;
import com.surya.shopcart.activity.CartActivity;
import com.surya.shopcart.cart.EmptyCartActivity;
import com.surya.shopcart.interfaces.OnGetDataListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class Utils {
    private static final String appRoot = "shopCart";
    public static final String province = appRoot + "/province";
    public static final String vendorVsRegex = appRoot + "/vendorVsRegex";
    private static final String products = appRoot + "/products";
    private static final String users = appRoot + "/users";
    public static final String fruits = products + "/fruits";
    public static final String veggies = products + "/veggies";
    public static final String beverages = products + "/beverages";
    public static final String flyers = appRoot + "/flyers";
    public static final String flyerImages = flyers + "/images";

    private static final String TAG = Utils.class.getSimpleName();

    public static short totalQuantity=0;
    public static float taxFloat=0;
    public static float discount=0;

    public static void addDataToFireStore(HashMap<String, Object> obj, String path, OnCompleteListener<Void> onCompleteListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Add a new document with a generated ID
        db.collection(path)
                .add(obj)
                .addOnSuccessListener(documentReference -> {
                    onCompleteListener.onComplete(Tasks.forResult(null)); // Complete the task successfully
                })
                .addOnFailureListener(e -> {
                    onCompleteListener.onComplete(Tasks.forException(e)); // Complete the task with an error
                });
    }

    public static void getFireStoreData(String path, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(path)
                .get()
                .addOnSuccessListener(documentReference -> {
                    onCompleteListener.onComplete(Tasks.forResult(documentReference)); // Complete the task successfully
                })
                .addOnFailureListener(e -> {
                    onCompleteListener.onComplete(Tasks.forException(e)); // Complete the task with an error
                });
    }

    public static void getFireStoreDataFromSubCollection(String path, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.document(path)
                .get()
                .addOnSuccessListener(documentReference -> {
                    onCompleteListener.onComplete(Tasks.forResult(documentReference)); // Complete the task successfully
                })
                .addOnFailureListener(e -> {
                    onCompleteListener.onComplete(Tasks.forException(e)); // Complete the task with an error
                });
    }

    public static void getFireStoreDataById(String path, String id, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(path)
                .document(id)
                .get()
                .addOnSuccessListener(documentReference -> {
                    onCompleteListener.onComplete(Tasks.forResult(documentReference)); // Complete the task successfully
                })
                .addOnFailureListener(e -> {
                    onCompleteListener.onComplete(Tasks.forException(e)); // Complete the task with an error
                });
    }

    public static void getFireStoreDataByIds(String path, List<String> documentIds, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(path)
                .whereIn(FieldPath.documentId(), documentIds)
                .get()
                .addOnCompleteListener(onCompleteListener);
    }

    public static void updateFirestoreDataById(String path, String id, Map<String, Object> obj, OnCompleteListener<Void> onCompleteListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(path).document(id)
                .update(obj)
                .addOnCompleteListener(onCompleteListener);
    }

    public static void addMapDataToRealTimeDataBase(HashMap<String, Object> obj, String path){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(path);

        myRef.setValue(obj);
    }

    public static void addMapDataToRealTimeDataBase(HashMap<String, Object> obj, String path, OnSuccessListener<Void> onSuccessListener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(path);

        myRef.setValue(obj)
                .addOnSuccessListener(onSuccessListener);
    }

    public static void getMapDataFromRealTimeDataBase(String path, final OnGetDataListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(path);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue();

                if (value instanceof HashMap) {
                    HashMap<String, Object> data = (HashMap<String, Object>) value;
                    listener.onSuccess(data);
                } else {
                    listener.onFailure(new Exception("Data is not in expected format"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
                listener.onFailure(databaseError.toException());
            }
        });
    }


    public static void deleteDataFromRealTimeDatabase(String path, OnSuccessListener<Void> onSuccessListener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference(path).removeValue().addOnSuccessListener(onSuccessListener);
    }

    public static void setProductQuantityForUser(String userId, String productId, boolean isIncrease, Button proceedButton){
        setProductQuantityForUser(userId, productId, isIncrease, proceedButton, null);
    }
    public static void setProductQuantityForUser(String userId, String productId, boolean isIncrease, Button proceedButton, TextView grandsubtotal){
        setProductQuantityForUser(userId, productId, isIncrease, proceedButton, grandsubtotal, null);
    }
    public static void setProductQuantityForUser(String userId, String productId, boolean isIncrease, Button proceedButton, TextView grandsubtotal, Context context){

        Utils.getMapDataFromRealTimeDataBase(getUserCartItemsPath(userId), new OnGetDataListener() {

            @Override
            public void onSuccess(HashMap<String, Object> productVsQuantity) {

                byte quantityFromRemote = Byte.valueOf(productVsQuantity.getOrDefault(productId, 0)+"");

                if(isIncrease){
                    quantityFromRemote++;
                }else{
                    quantityFromRemote--;
                }

                if(quantityFromRemote <= 0){
                    productVsQuantity.remove(productId);
                }else if(quantityFromRemote >= 20){
                    quantityFromRemote = 20;
                    productVsQuantity.put(productId, (int)quantityFromRemote);
                }else{
                    productVsQuantity.put(productId, (int)quantityFromRemote);
                }

                Utils.addMapDataToRealTimeDataBase(productVsQuantity, getUserCartItemsPath(userId));

                Utils.handleTotalPriceChange(userId, proceedButton, grandsubtotal, null, null, context);
            }

            @Override
            public void onFailure(Exception e) {
                HashMap<String, Object> productVsQuantity = new HashMap<>();
                productVsQuantity.put(productId, 1);

                Utils.addMapDataToRealTimeDataBase(productVsQuantity, getUserCartItemsPath(userId));
            }
        });

    }

    public static String getUserCartItemsPath(String userId) {
        return Utils.users + '/' + userId + "/products";
    }

    public static String getUserAddressPath(String userId) {
        return Utils.users + '/' + userId + "/address";
    }

    public static String getUserCardPath(String userId) {
        return Utils.users + '/' + userId + "/card";
    }

    public static void addHomeIconNavigation(Context context, Toolbar menuBar){
        menuBar.setNavigationIcon(R.drawable.ic_home);
        menuBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!context.getClass().equals(ProductHomePageActivity.class)) {
                    context.startActivity(new Intent(context, ProductHomePageActivity.class));
                }
            }
        });
    }

    public static void handleMenuCLick(Context context, MenuItem item){
        switch(item.getItemId()){
            case R.id.cartIcon:
                if (context.getClass().equals(CartActivity.class)) {
                    return;
                }
                context.startActivity(new Intent(context, CartActivity.class));
                break;
        }
    }

    public static String getSubtotalStr(byte quantity, double price){
        return Math.round(quantity * price * 100.0) / 100.0 +"";
    }

    public static float getSubtotalFloat(byte quantity, double price){
        return (float) (Math.round(quantity * price * 100.0) / 100.0);
    }

    public static void handleTotalPriceChange(String userId, Button proceedToBuy) {
        handleTotalPriceChange(userId, proceedToBuy, null, null, null);
    }

    public static void handleTotalPriceChange(String userId, Button proceedToBuy, TextView total){
        handleTotalPriceChange(userId, proceedToBuy, total, null, null);
    }

    public static void handleTotalPriceChange(String userId, Button proceedToBuy, TextView total, TextView tax, TextView subTotal){
        handleTotalPriceChange(userId, proceedToBuy, total, tax, subTotal, null);
    }
    public static void handleTotalPriceChange(String userId, Button proceedToBuy, TextView total, TextView tax, TextView subTotal, Context context){
        handleTotalPriceChange(userId, proceedToBuy, total, tax, subTotal, context, null);
    }
    public static void handleTotalPriceChange(String userId, Button proceedToBuy, TextView total, TextView tax, TextView subTotal, Context context, TextView discountTV){

        totalQuantity = 0;
        taxFloat = 0;
        discount = 0;
        Utils.getMapDataFromRealTimeDataBase(Utils.getUserCartItemsPath(userId), new OnGetDataListener() {
            @Override
            public void onSuccess(HashMap<String, Object> dataMap) {

                ArrayList<String> products = new ArrayList<>();

                if (dataMap != null && !dataMap.isEmpty()) {
                    for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                        products.add(entry.getKey());
                        totalQuantity += Byte.parseByte(entry.getValue()+"");
                    }
                }

                if (!products.isEmpty()) {

                    //Fruits
                    Utils.getFireStoreDataByIds(Utils.fruits, products, new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                ArrayList<Float> subTotals = new ArrayList<>();
                                byte quantity = 0;
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    quantity = Byte.valueOf(dataMap.get(document.getId()) + "");
                                    double price = Double.valueOf(document.get("price") + "");
                                    if (quantity <= 0) {
                                        continue;
                                    }

                                    handleCalculation(document, quantity, price, subTotals);
                                }

                                if (subTotals.size() != products.size()) {
                                    //Veggies
                                    Utils.getFireStoreDataByIds(Utils.veggies, products, new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                byte quantity = 0;
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    quantity = Byte.valueOf(dataMap.get(document.getId()) + "");
                                                    double price = Double.valueOf(document.get("price") + "");
                                                    if (quantity <= 0) {
                                                        continue;
                                                    }
                                                    handleCalculation(document, quantity, price, subTotals);
                                                }

                                                if (subTotals.size() != products.size()) {

                                                    //Veggies
                                                    Utils.getFireStoreDataByIds(Utils.beverages, products, new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                byte quantity = 0;
                                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                                    quantity = Byte.valueOf(dataMap.get(document.getId()) + "");
                                                                    double price = Double.valueOf(document.get("price") + "");
                                                                    if (quantity <= 0) {
                                                                        continue;
                                                                    }
                                                                    handleCalculation(document, quantity, price, subTotals);
                                                                }
                                                                setTotalAmount(subTotals, proceedToBuy, total, tax, subTotal, discountTV);
                                                            } else {
                                                                Log.w("getData", "Error getting documents.", task.getException());
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    setTotalAmount(subTotals, proceedToBuy, total, tax, subTotal, discountTV);
                                                }
                                            } else {
                                                Log.w("getData", "Error getting documents.", task.getException());
                                            }
                                        }
                                    });
                                } else {
                                    setTotalAmount(subTotals, proceedToBuy, total, tax, subTotal, discountTV);
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
                if(context != null){
                    ArrayList<Float> subTotals = new ArrayList<>();
                    setTotalAmount(subTotals, proceedToBuy, total, tax, subTotal);
                    Intent emptyCart = new Intent(context, EmptyCartActivity.class);
                    context.startActivity(emptyCart);
                }
            }
        });
    }

    public static void setTotalAmount(ArrayList<Float> subTotals, Button proceedButton, TextView total, TextView tax, TextView subTotalView) {
        setTotalAmount(subTotals, proceedButton, total, tax, subTotalView, null);
    }

    public static void setTotalAmount(ArrayList<Float> subTotals, Button proceedButton, TextView total, TextView tax, TextView subTotalView, TextView discountTV) {
        float totalAmount = 0;
        for(float subTotal : subTotals){
            totalAmount += subTotal;
        }

        String itemStr = "("+ totalQuantity +" items)";
        if(totalQuantity == 1){
            itemStr = "("+ subTotals.size() +" item)";
        }

        if(proceedButton != null){
            proceedButton.setText("Proceed to checkout "+itemStr);
        }

        if(tax != null){
            //tax.setText("$ "+getSubtotalStr((byte)1, totalAmount * 0.13));
            tax.setText("$ "+taxFloat);
        }

        if(discountTV != null){
            discountTV.setText("$ "+discount);
        }

        if(subTotalView != null){
            subTotalView.setText("$ "+getSubtotalStr((byte)1, totalAmount));
        }

        if(total != null){
            total.setText("$ " + getSubtotalStr((byte)1, totalAmount + taxFloat - discount));
        }
    }

    public static String getMaskedCardNumberForDisplay(String cardNumber){
        StringBuilder cardNumberSB = new StringBuilder(cardNumber);
        for(int i=0;i<cardNumber.length()-4;i++){
            cardNumberSB.setCharAt(i, '*');
        }
        return cardNumberSB.toString();
    }

    public static boolean isValidEmail(String email) {
        boolean valid = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException e) {
            valid = false;
        }
        return valid;
    }
    public static boolean isValidPassword(String password) {
        // Minimum 8 chars
        // Maximum 15 chars
        // Atleast 1 upper case
        // Atleast 1 lower case
        // Atleast 1 number
        // Atleast 1 special char
        Pattern regex = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$");

        return regex.matcher(password).matches();
    }

    public static void handleCalculation(QueryDocumentSnapshot document, byte quantity, double price, ArrayList<Float> subTotals){
        float subTotal = Utils.getSubtotalFloat(quantity, price);
        float taxPercent = 0;
        float discountPercent = 0;
        try{
            taxPercent = Float.parseFloat(document.get("tax")+"");
        }catch(Exception e){

        }
        try{
            discountPercent = Float.parseFloat(document.get("discount")+"");
        }catch(Exception e){

        }

        if(document.contains("tax")){
            taxFloat += Utils.getSubtotalFloat((byte)1,subTotal * taxPercent);
        }
        if(document.contains("discount")){
            discount += Utils.getSubtotalFloat((byte)1, subTotal * discountPercent);
        }

        subTotals.add(subTotal);
    }

}
