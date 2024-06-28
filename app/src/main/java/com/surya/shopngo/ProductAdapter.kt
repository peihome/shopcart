package com.surya.shopngo

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class ProductAdapter(private val productList: ArrayList<Product>) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {
    private var listener: OnItemClickListener? = null
    var storage = FirebaseStorage.getInstance()
    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.product_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position]
        val storageRef = storage.getReference()
        val pathReference = storageRef.child(product.getImage())
        pathReference.getDownloadUrl()
            .addOnSuccessListener { uri: Uri? -> Picasso.get().load(uri).into(holder.image) }
            .addOnFailureListener { exception: Exception? ->
                Log.e(
                    "TAG",
                    "Failed to load image from Firebase Storage",
                    exception
                )
            }
        holder.title.text = product.getTitle()
        holder.price.text = "$ " + product.getPrice() + " /lb"
        holder.description.text = product.getDescription()
        holder.quantity.tag = product.getId()
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        var image: ImageView
        var title: TextView
        var description: TextView
        var price: TextView
        var addButton: Button
        var reduceQuantity: TextView
        var increaseQuantity: TextView
        var quantity: TextView

        init {
            image = itemView.findViewById(R.id.image)
            title = itemView.findViewById(R.id.title)
            price = itemView.findViewById(R.id.price)
            description = itemView.findViewById(R.id.description)
            addButton = itemView.findViewById(R.id.addButton)
            reduceQuantity = itemView.findViewById(R.id.reduceQuantity)
            increaseQuantity = itemView.findViewById(R.id.increaseQuantity)
            quantity = itemView.findViewById(R.id.quantity)
            image.setOnClickListener { view1: View ->
                if (listener != null) {
                    val position = getAdapterPosition()
                    if (position != RecyclerView.NO_POSITION) {
                        listener!!.onItemClick(view1, position, productList, "image")
                    }
                }
            }
            reduceQuantity.setOnClickListener { view1: View ->
                if (listener != null) {
                    val position = getAdapterPosition()
                    if (position != RecyclerView.NO_POSITION) {
                        listener!!.onItemClick(view1, position, productList, "reduceQuantity")
                    }
                }
            }
            increaseQuantity.setOnClickListener { view1: View ->
                if (listener != null) {
                    val position = getAdapterPosition()
                    if (position != RecyclerView.NO_POSITION) {
                        listener!!.onItemClick(view1, position, productList, "increaseQuantity")
                    }
                }
            }
            addButton.setOnClickListener { view1: View ->
                if (listener != null) {
                    val position = getAdapterPosition()
                    if (position != RecyclerView.NO_POSITION) {
                        listener!!.onItemClick(view1, position, productList, "addButton")
                    }
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int, productList: ArrayList<Product>, type: String?)
    }
}