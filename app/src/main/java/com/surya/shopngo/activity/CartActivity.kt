package com.surya.shopngo.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.surya.shopngo.dataclass.Product
import com.surya.shopngo.R
import com.surya.shopngo.adapter.CartItemAdapter
import com.surya.shopngo.interfaces.OnGetDataListener
import com.surya.shopngo.utils.Utils

class CartActivity : AppCompatActivity(), CartItemAdapter.OnItemClickListener {
    var recyclerView: RecyclerView? = null
    var adapter: CartItemAdapter? = null
    private var cartItemsList: ArrayList<Product>? = null
    var grandsubtotalTV: TextView? = null
    private lateinit var progressDialog: ProgressDialog

    fun openCartPage(item: MenuItem) {
        Utils.handleMenuCLick(this, item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // Home navigation icon
        Utils.addHomeIconNavigation(this, findViewById(R.id.topAppBar))
        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            userId = currentUser.uid
        }
        proceedButton = findViewById(R.id.buyButton)
        grandsubtotalTV = findViewById(R.id.grandsubtotal)
        proceedButton.setOnClickListener(View.OnClickListener { view: View? ->
            val confirmOrderActivity = Intent(this, ConfirmOrderActivity::class.java)
            startActivity(confirmOrderActivity)
        })

        progressDialog = ProgressDialog(this).apply {
            setMessage("Loading Cart...")
        }
        handleCartItemsForView()
    }

    fun showCartItems() {
        //ViewPager2 Flyer
        if (recyclerView == null) {
            recyclerView = findViewById(R.id.rvCartPage)
        }
        adapter = CartItemAdapter(cartItemsList!!)
        adapter!!.setOnItemClickListener(this as CartItemAdapter.OnItemClickListener)
        recyclerView!!.setAdapter(adapter)
        recyclerView!!.setLayoutManager(
            LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
            )
        )
        Utils.handleTotalPriceChange(userId, proceedButton, grandsubtotalTV)
    }

    override fun onItemClick(view: View?, position: Int, type: String?) {
        val product = cartItemsList!![position]
        val quantity: TextView
        val subTotal: TextView
        val quantityParentLayout: LinearLayout
        val detailsLayout: LinearLayout
        var entireCard: ConstraintLayout
        var quantityByte: Byte = 0
        when (type) {
            "image" -> {
                val productDetail = Intent(this, ProductDetailActivity::class.java)
                productDetail.putExtra("title", product.title)
                productDetail.putExtra("description", product.description)
                productDetail.putExtra("price", product.price)
                productDetail.putExtra("image", product.image)
                productDetail.putExtra("id", product.id)
                startActivity(productDetail)
            }

            "reduceQuantity" -> {
                quantityParentLayout = view!!.parent as LinearLayout
                quantity = quantityParentLayout.getChildAt(1) as TextView
                detailsLayout = quantityParentLayout.parent as LinearLayout
                subTotal = detailsLayout.getChildAt(2) as TextView
                quantityByte = (quantity.text.toString() + "").toByte()
                handleQuantityLayout(--quantityByte, quantity, subTotal, product, position, false)
            }

            "increaseQuantity" -> {
                quantityParentLayout = view!!.parent as LinearLayout
                quantity = quantityParentLayout.getChildAt(1) as TextView
                detailsLayout = quantityParentLayout.parent as LinearLayout
                subTotal = detailsLayout.getChildAt(2) as TextView
                quantityByte = (quantity.text.toString() + "").toByte()
                handleQuantityLayout(++quantityByte, quantity, subTotal, product, position, true)
            }
        }
    }

    fun handleQuantityLayout(
        quantityShort: Byte,
        quantity: TextView,
        subTotal: TextView,
        product: Product,
        position: Int,
        isIncrease: Boolean
    ) {
        if (quantityShort <= 0) {
            cartItemsList!!.removeAt(position)
            adapter!!.notifyItemRemoved(position)
            Utils.setProductQuantityForUser(
                userId,
                product.id,
                isIncrease,
                proceedButton,
                grandsubtotalTV,
                this
            )
        } else {
            if (quantityShort >= 20) {
                quantity.text = "20"
                subTotal.text = "$ " + Utils.getSubtotalStr(
                    quantityShort,
                    product.price
                ) + " (Subtotal)"
                Utils.setProductQuantityForUser(userId, product.id, isIncrease, proceedButton)
                Toast.makeText(applicationContext, "Reached maximum limit!", Toast.LENGTH_SHORT)
                    .show()
            } else if (quantityShort < 20) {
                quantity.text = quantityShort.toString() + ""
                subTotal.text = "$ " + Utils.getSubtotalStr(
                    quantityShort,
                    product.price
                ) + " (Subtotal)"
                Utils.setProductQuantityForUser(
                    userId,
                    product.id,
                    isIncrease,
                    proceedButton,
                    grandsubtotalTV,
                    this
                )
            }
        }
    }

    fun handleCartItemsForView() {
        progressDialog.show()
        Utils.getMapDataFromRealTimeDataBase(
            Utils.getUserCartItemsPath(userId),
            object : OnGetDataListener {
                override fun onSuccess(dataMap: HashMap<String?, Any?>?) {
                    progressDialog.dismiss()
                    val products = ArrayList<String?>()
                    if (dataMap != null && !dataMap.isEmpty()) {
                        for ((key) in dataMap) {
                            products.add(key)
                        }
                    }
                    if (!products.isEmpty()) {

                        //Fruits
                        Utils.getFireStoreDataByIds(Utils.fruits, products) { task ->
                            if (task.isSuccessful) {
                                cartItemsList = ArrayList()
                                var quantity: Byte
                                for (document in task.result!!) {
                                    val data: Map<*, *> = document.getData()
                                    quantity = (dataMap!![document.id].toString() + "").toByte()
                                    if (quantity <= 0) {
                                        continue
                                    }
                                    cartItemsList!!.add(
                                        Product(
                                            document.id,
                                            data["image"].toString() + "",
                                            data["title"].toString() + "",
                                            data["description"].toString() + "",
                                            (data["price"].toString() + "").toDouble(),
                                            null,
                                            quantity
                                        )
                                    )
                                }
                                if (cartItemsList!!.size != products.size) {
                                    //Veggies
                                    Utils.getFireStoreDataByIds(Utils.veggies, products) { task ->
                                        if (task.isSuccessful) {
                                            var quantity: Byte
                                            for (document in task.result!!) {
                                                val data: Map<*, *> = document.getData()
                                                quantity =
                                                    (dataMap!![document.id].toString() + "").toByte()
                                                if (quantity <= 0) {
                                                    continue
                                                }
                                                cartItemsList!!.add(
                                                    Product(
                                                        document.id,
                                                        data["image"].toString() + "",
                                                        data["title"].toString() + "",
                                                        data["description"].toString() + "",
                                                        (data["price"].toString() + "").toDouble(),
                                                        null,
                                                        quantity
                                                    )
                                                )
                                            }
                                            if (cartItemsList!!.size != products.size) {

                                                //Veggies
                                                Utils.getFireStoreDataByIds(
                                                    Utils.beverages,
                                                    products
                                                ) { task ->
                                                    if (task.isSuccessful) {
                                                        var quantity: Byte
                                                        for (document in task.result!!) {
                                                            val data: Map<*, *> = document.getData()
                                                            quantity =
                                                                (dataMap!![document.id].toString() + "").toByte()
                                                            if (quantity <= 0) {
                                                                continue
                                                            }
                                                            cartItemsList!!.add(
                                                                Product(
                                                                    document.id,
                                                                    data["image"].toString() + "",
                                                                    data["title"].toString() + "",
                                                                    data["description"].toString() + "",
                                                                    (data["price"].toString() + "").toDouble(),
                                                                    null,
                                                                    quantity
                                                                )
                                                            )
                                                        }
                                                        showCartItems()
                                                    } else {
                                                        Log.w(
                                                            "getData",
                                                            "Error getting documents.",
                                                            task.exception
                                                        )
                                                    }
                                                }
                                            } else {
                                                showCartItems()
                                            }
                                        } else {
                                            Log.w(
                                                "getData",
                                                "Error getting documents.",
                                                task.exception
                                            )
                                        }
                                    }
                                } else {
                                    showCartItems()
                                }
                            } else {
                                Log.w("getData", "Error getting documents.", task.exception)
                            }
                        }
                    }
                }

                override fun onFailure(e: Exception?) {
                    progressDialog.dismiss()
                    val emptyCartIntent = Intent(applicationContext, EmptyCartActivity::class.java)
                    startActivity(emptyCartIntent)
                    finish()
                }
            })
    }

    companion object {
        private val TAG = Utils::class.java.simpleName
        private lateinit var userId: String
        private lateinit var proceedButton: Button
    }
}