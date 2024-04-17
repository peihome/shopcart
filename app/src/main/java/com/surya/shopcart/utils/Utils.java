package com.surya.shopcart.utils;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
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
import com.surya.shopcart.Flyer;
import com.surya.shopcart.FlyerAdapter;
import com.surya.shopcart.Product;
import com.surya.shopcart.ProductAdapter;
import com.surya.shopcart.ProductHomePageActivity;
import com.surya.shopcart.R;
import com.surya.shopcart.activity.CartActivity;
import com.surya.shopcart.interfaces.OnGetDataListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Utils {
    private static final String appRoot = "shopCart";
    private static final String products = appRoot + "/products";
    private static final String users = appRoot + "/users";
    public static final String fruits = products + "/fruits";
    public static final String veggies = products + "/veggies";
    public static final String beverages = products + "/beverages";
    public static final String flyers = appRoot + "/flyers";
    public static final String flyerImages = flyers + "/images";

    private static final String TAG = Utils.class.getSimpleName();

    public static JSONObject getProducts() {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Create your JSON data
        Map<String, Object> jsonData = new HashMap<>();
        jsonData.put("key1", "value1");
        jsonData.put("key2", "value2");
        // Add more data as needed

        // Store JSON data in Firestore
        firestore.collection("collection").document("document")
                .set(jsonData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Data successfully saved
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle errors
                    }
                });


        return null;
    }


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

    public static void getMapDataFromRealTimeDataBase(String path, final OnGetDataListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(path);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue();

                if (value instanceof HashMap) {
                    @SuppressWarnings("unchecked")
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

    public static void setProductQuantityForUser(String userId, String productId, short quantity){

        String path = getUserDetailPath(userId);

        Utils.getMapDataFromRealTimeDataBase(path, new OnGetDataListener() {

            @Override
            public void onSuccess(HashMap<String, Object> productVsQuantity) {

                int quantityFromRemote = 0;

                quantityFromRemote = Short.valueOf(productVsQuantity.getOrDefault(productId, 0)+"");
                quantityFromRemote = quantityFromRemote + quantity;

                if((quantity + quantityFromRemote) < 0){
                    quantityFromRemote = 0;
                }else if((quantity + quantityFromRemote) >= 20){
                    quantityFromRemote = 20;
                }
                if(quantityFromRemote == 0){
                    productVsQuantity.remove(productId);
                }

                Utils.addMapDataToRealTimeDataBase(productVsQuantity, path);
            }

            @Override
            public void onFailure(Exception e) {
                HashMap<String, Object> productVsQuantity = new HashMap<>();
                productVsQuantity.put(productId, 1);

                Utils.addMapDataToRealTimeDataBase(productVsQuantity, path);
            }
        });

    }

    public static String getProductQuantityPath(String userId, String productId) {
        return Utils.users + '/' + userId + "/products/" + productId;
    }

    public static String getUserDetailPath(String userId) {
        return Utils.users + '/' + userId + "/products";
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

}
