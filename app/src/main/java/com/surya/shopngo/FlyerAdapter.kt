package com.surya.shopngo

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.surya.shopngo.FlyerAdapter.ViewPagerHolder

class FlyerAdapter(private val flyersList: ArrayList<Flyer>) :
    RecyclerView.Adapter<ViewPagerHolder>() {
    var storage = FirebaseStorage.getInstance()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.flyer_layout, parent, false)
        return ViewPagerHolder(view)
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        val flyer: Flyer = flyersList[position]
        val storageRef = storage.getReference()
        val pathReference = storageRef.child(flyer.fetchImage())
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
        return flyersList.size
    }

    inner class ViewPagerHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        var image: ImageView

        init {
            image = itemView.findViewById(R.id.flyerImage)
        }
    }
}
