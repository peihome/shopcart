package com.surya.shopcart.utils;


import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Utils {
    private static final String appRoot = "shopCart";
    private static final String products = appRoot + "/products";
    public static final String fruits = products + "/fruits";
    public static final String veggies = products + "/veggies";
    public static final String beverages = products + "/beverages";
    public static final String flyers = appRoot + "/flyers";
    public static final String flyerImages = flyers + "/images";

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

}
