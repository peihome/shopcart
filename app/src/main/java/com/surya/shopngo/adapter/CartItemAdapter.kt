package com.surya.shopngo.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.surya.shopngo.dataclass.Product
import com.surya.shopngo.R
import com.surya.shopngo.utils.Utils

class CartItemAdapter : RecyclerView.Adapter<CartItemAdapter.ViewHolder> {
    private var listener: OnItemClickListener? = null
    private var cartItemsList: ArrayList<Product>
    var storage = FirebaseStorage.getInstance()
    var showQuantityLayout = true

    constructor(cartItemsList: ArrayList<Product>) {
        this.cartItemsList = cartItemsList
    }

    constructor(cartItemsList: ArrayList<Product>, showQuantityLayout: Boolean) {
        this.cartItemsList = cartItemsList
        this.showQuantityLayout = showQuantityLayout
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.cart_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = cartItemsList[position]
        val storageRef = storage.getReference()
        val pathReference = storageRef.child(product.image)
        pathReference.getDownloadUrl()
            .addOnSuccessListener { uri: Uri? -> Picasso.get().load(uri).into(holder.image) }
            .addOnFailureListener { exception: Exception? ->
                Log.e(
                    "TAG",
                    "Failed to load image from Firebase Storage",
                    exception
                )
            }
        holder.title.text = product.title
        holder.price.text = "$ " + product.price + " /lb"
        holder.quantity.text = product.quantity.toString() + ""
        holder.subTotal.text =
            "$ " + Utils.getSubtotalStr(product.quantity, product.price) + " (Subtotal)"
        if (!showQuantityLayout) {
            holder.quantityLayout.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return cartItemsList.size
    }

    inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        var image: ImageView
        var title: TextView
        var quantity: TextView
        var price: TextView
        var reduceQuantity: TextView
        var increaseQuantity: TextView
        var subTotal: TextView
        var quantityLayout: LinearLayout

        init {
            image = itemView.findViewById(R.id.image)
            title = itemView.findViewById(R.id.title)
            price = itemView.findViewById(R.id.price)
            quantity = itemView.findViewById(R.id.quantity)
            reduceQuantity = itemView.findViewById(R.id.reduceQuantity)
            increaseQuantity = itemView.findViewById(R.id.increaseQuantity)
            subTotal = itemView.findViewById(R.id.grandTotal)
            quantityLayout = itemView.findViewById(R.id.quantityLayout)
            image.setOnClickListener { view1: View? ->
                if (listener != null) {
                    val position = getAdapterPosition()
                    if (position != RecyclerView.NO_POSITION) {
                        listener!!.onItemClick(view1, position, "image")
                    }
                }
            }
            reduceQuantity.setOnClickListener { view1: View? ->
                if (listener != null) {
                    val position = getAdapterPosition()
                    if (position != RecyclerView.NO_POSITION) {
                        listener!!.onItemClick(view1, position, "reduceQuantity")
                    }
                }
            }
            increaseQuantity.setOnClickListener { view1: View? ->
                if (listener != null) {
                    val position = getAdapterPosition()
                    if (position != RecyclerView.NO_POSITION) {
                        listener!!.onItemClick(view1, position, "increaseQuantity")
                    }
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(view: View?, position: Int, type: String?)
    }
}
