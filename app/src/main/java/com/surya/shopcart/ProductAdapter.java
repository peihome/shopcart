package com.surya.shopcart;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        holder.price.setText("$ " + product.getPrice() + " /lb");
        holder.description.setText(product.getDescription());
        holder.quantity.setTag(product.getId());

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
        Button addButton;
        TextView reduceQuantity;
        TextView increaseQuantity;
        TextView quantity;

        public ViewHolder(View view){
            super(view);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);
            description = itemView.findViewById(R.id.description);
            addButton = itemView.findViewById(R.id.addButton);
            reduceQuantity = itemView.findViewById(R.id.reduceQuantity);
            increaseQuantity = itemView.findViewById(R.id.increaseQuantity);
            quantity = itemView.findViewById(R.id.quantity);

            image.setOnClickListener(view1 -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(view1, position, productList, "image");
                    }
                }
            });

            reduceQuantity.setOnClickListener(view1 -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(view1, position, productList, "reduceQuantity");
                    }
                }
            });

            increaseQuantity.setOnClickListener(view1 -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(view1, position, productList, "increaseQuantity");
                    }
                }
            });

            addButton.setOnClickListener(view1 -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(view1, position, productList, "addButton");
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, ArrayList<Product> productList, String type);
    }
}