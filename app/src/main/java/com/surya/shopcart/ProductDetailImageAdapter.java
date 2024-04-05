package com.surya.shopcart;

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

public class ProductDetailImageAdapter extends RecyclerView.Adapter<ProductDetailImageAdapter.ViewHolder> {

    private ArrayList<String> productImages;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    public ProductDetailImageAdapter(ArrayList<String> productImages) {
        this.productImages = productImages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.productdetail_image, parent, false);
        return new ProductDetailImageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        StorageReference storageRef = storage.getReference();
        StorageReference pathReference = storageRef.child(productImages.get(position));

        pathReference.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get().load(uri).into(holder.image);
        }).addOnFailureListener(exception -> {
            Log.e("TAG", "Failed to load image from Firebase Storage", exception);
        });

    }

    @Override
    public int getItemCount() {
        return productImages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;

        public ViewHolder(View view){
            super(view);
            image = itemView.findViewById(R.id.productDetailImage);
        }
    }
}
