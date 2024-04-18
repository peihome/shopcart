package com.surya.shopcart.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.surya.shopcart.Product;
import com.surya.shopcart.ProductAdapter;
import com.surya.shopcart.R;
import com.surya.shopcart.utils.Utils;

import java.util.ArrayList;

import okhttp3.internal.Util;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder> {

    private OnItemClickListener listener;

    private ArrayList<Product> cartItemsList;

    FirebaseStorage storage = FirebaseStorage.getInstance();

    public CartItemAdapter(ArrayList<Product> cartItemsList) {
        this.cartItemsList = cartItemsList;
    }

    public void setOnItemClickListener(CartItemAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
    @NonNull
    @Override
    public CartItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
        return new CartItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = cartItemsList.get(position);

        StorageReference storageRef = storage.getReference();
        StorageReference pathReference = storageRef.child(product.getImage());

        pathReference.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get().load(uri).into(holder.image);
        }).addOnFailureListener(exception -> {
            Log.e("TAG", "Failed to load image from Firebase Storage", exception);
        });

        holder.title.setText(product.getTitle());
        holder.price.setText("$ "+product.getPrice() +" /lb");
        holder.quantity.setText(product.getQuantity()+"");
        holder.subTotal.setText("$ "+Utils.getSubtotalStr(product.getQuantity(), product.getPrice()) + " (Subtotal)");
    }

    @Override
    public int getItemCount() {
        return cartItemsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView title;
        TextView quantity;
        TextView price;
        TextView reduceQuantity;
        TextView increaseQuantity;
        TextView subTotal;

        public ViewHolder(View view){
            super(view);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);
            quantity = itemView.findViewById(R.id.quantity);
            reduceQuantity = itemView.findViewById(R.id.reduceQuantity);
            increaseQuantity = itemView.findViewById(R.id.increaseQuantity);
            subTotal = itemView.findViewById(R.id.subTotal);

            image.setOnClickListener(view1 -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(view1, position, "image");
                    }
                }
            });

            reduceQuantity.setOnClickListener(view1 -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(view1, position, "reduceQuantity");
                    }
                }
            });

            increaseQuantity.setOnClickListener(view1 -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(view1, position, "increaseQuantity");
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, String type);
    }
}
