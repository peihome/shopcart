package com.surya.shopngo.activity

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.surya.shopngo.R

class ProductDetailImageAdapter(private val productImages: ArrayList<String?>) :
    RecyclerView.Adapter<ProductDetailImageAdapter.ViewHolder>() {
    var storage = FirebaseStorage.getInstance()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.productdetail_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val storageRef = storage.getReference()
        val pathReference = storageRef.child(productImages[position]!!)
        pathReference.getDownloadUrl()
            .addOnSuccessListener { uri: Uri? -> Picasso.get().load(uri).into(holder.image) }
            .addOnFailureListener { exception: Exception? ->
                Log.e(
                    "TAG",
                    "Failed to load image from Firebase Storage",
                    exception
                )
            }
    }

    override fun getItemCount(): Int {
        return productImages.size
    }

    inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        var image: ImageView

        init {
            image = itemView.findViewById(R.id.productDetailImage)
        }
    }
}
