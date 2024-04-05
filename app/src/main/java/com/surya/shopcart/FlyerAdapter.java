package com.surya.shopcart;

import static com.google.firebase.storage.FirebaseStorage.*;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FlyerAdapter extends RecyclerView.Adapter<FlyerAdapter.ViewPagerHolder> {

    FirebaseStorage storage = FirebaseStorage.getInstance();
    private ArrayList<Flyer> flyersList;
    public FlyerAdapter(ArrayList<Flyer> flyersList){
        this.flyersList = flyersList;
    }
    @NonNull
    @Override
    public ViewPagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flyer_layout, parent, false);
        return new FlyerAdapter.ViewPagerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerHolder holder, int position) {
        Flyer flyer = flyersList.get(position);

        StorageReference storageRef = storage.getReference();
        StorageReference pathReference = storageRef.child(flyer.getImage());

        pathReference.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get().load(uri).into(holder.image);
        }).addOnFailureListener(exception -> {
            Log.e("TAG", "Failed to load image from Firebase Storage", exception);
        });
    }

    @Override
    public int getItemCount() {
        return flyersList.size();
    }

    class ViewPagerHolder extends RecyclerView.ViewHolder{
        ImageView image;

        public ViewPagerHolder(View view){
            super(view);
            image = itemView.findViewById(R.id.flyerImage);
        }
    }
}
