package com.surya.shopcart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private ArrayList<Product> productList;
    private OnItemClickListener listener;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    public ProductAdapter(ArrayList<Product> productList) {
        this.productList = productList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Product product = productList.get(position);

        StorageReference storageRef = storage.getReference();
        StorageReference pathReference = storageRef.child(product.getImage());

        pathReference.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get().load(uri).into(holder.image);
        }).addOnFailureListener(exception -> {
            Log.e("TAG", "Failed to load image from Firebase Storage", exception);
        });

        holder.title.setText(product.getTitle());
        holder.price.setText(product.getPrice() + "");
        holder.description.setText(product.getDescription());

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView title;
        TextView description;
        TextView price;

        public ViewHolder(View view){
            super(view);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);
            description = itemView.findViewById(R.id.description);

            view.setOnClickListener(view1 -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position, productList);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, ArrayList<Product> productList);
    }
}