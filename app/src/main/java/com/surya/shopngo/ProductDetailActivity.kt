package com.surya.shopngo

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.surya.shopngo.interfaces.OnGetDataListener
import com.surya.shopngo.utils.Utils

class ProductDetailActivity : AppCompatActivity() {
    fun openCartPage(item: MenuItem) {
        Utils.handleMenuCLick(this, item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.productdetail)

        // Home navigation icon
        Utils.addHomeIconNavigation(this, findViewById(R.id.topAppBar))
        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            userId = currentUser.uid
        }
        val title = findViewById<TextView>(R.id.title)
        val description = findViewById<TextView>(R.id.description)
        val price = findViewById<TextView>(R.id.price)
        val priceText = findViewById<TextView>(R.id.priceText)
        val addButton = findViewById<Button>(R.id.addButton)
        val increaseQuantity = findViewById<TextView>(R.id.increaseQuantity)
        val reduceQuantity = findViewById<TextView>(R.id.reduceQuantity)
        val quantity = findViewById<TextView>(R.id.quantity)
        val quantityLayout = findViewById<LinearLayout>(R.id.quantityLayout)
        val productId = intent.getStringExtra("id")
        val closeicon = findViewById<ImageView>(R.id.closeButton)
        closeicon.setOnClickListener { v: View? -> finish() }
        addButton.setOnClickListener { view1: View? ->
            onItemClick(
                addButton,
                quantityLayout,
                quantity,
                productId,
                "addButton"
            )
        }
        reduceQuantity.setOnClickListener { view1: View? ->
            onItemClick(
                addButton,
                quantityLayout,
                quantity,
                productId,
                "reduceQuantity"
            )
        }
        increaseQuantity.setOnClickListener { view1: View? ->
            onItemClick(
                addButton,
                quantityLayout,
                quantity,
                productId,
                "increaseQuantity"
            )
        }
        title.text = intent.getStringExtra("title")
        description.text = intent.getStringExtra("description")
        price.text = "Now: $ " + intent.getDoubleExtra("price", 0.0) + " /lb"
        priceText.text = "$ " + intent.getDoubleExtra("price", 0.0) + " /lb"
        val viewPager2 = findViewById<ViewPager2>(R.id.productDetailViewPager)
        val images = ArrayList<String?>()
        images.add(intent.getStringExtra("image"))
        viewPager2.setAdapter(ProductDetailImageAdapter(images))
    }

    fun onItemClick(
        addButton: Button,
        quantityLayout: LinearLayout,
        quantity: TextView,
        productId: String?,
        type: String?
    ) {
        var quantityByte: Byte
        when (type) {
            "addButton" -> handleQuantityLayout(
                1.toByte(),
                addButton,
                quantityLayout,
                quantity,
                productId!!,
                true
            )

            "reduceQuantity" -> {
                quantityByte = (quantity.text.toString() + "").toByte()
                handleQuantityLayout(
                    --quantityByte,
                    addButton,
                    quantityLayout,
                    quantity,
                    productId!!,
                    false
                )
            }

            "increaseQuantity" -> {
                quantityByte = (quantity.text.toString() + "").toByte()
                handleQuantityLayout(
                    ++quantityByte,
                    addButton,
                    quantityLayout,
                    quantity,
                    productId!!,
                    true
                )
            }
        }
    }

    fun handleQuantityLayout(
        quantity: Byte,
        addButton: Button,
        quantityLayout: LinearLayout,
        quantityView: TextView,
        productId: String,
        isIncrease: Boolean
    ) {
        Utils.getMapDataFromRealTimeDataBase(
            Utils.getUserCartItemsPath(userId),
            object : OnGetDataListener {
                override fun onSuccess(dataMap: HashMap<String?, Any?>?) {
                    var quantityFromRemote = (dataMap!![productId].toString() + "").toByte()
                    if (isIncrease) {
                        quantityFromRemote++
                    } else {
                        quantityFromRemote--
                    }
                    if (quantity <= 0 || quantityFromRemote <= 0) {
                        addButton.visibility = View.VISIBLE
                        quantityView.text = "1"
                        quantityLayout.visibility = View.GONE
                        Utils.setProductQuantityForUser(userId, productId, isIncrease, null)
                    } else if (quantity > 20 || quantityFromRemote > 20) {
                        //Utils.setProductQuantityForUser(userId, productId, isIncrease, null);
                        Toast.makeText(
                            applicationContext,
                            "Reached maximum limit!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        quantityView.text = quantity.toString() + ""
                        addButton.visibility = View.GONE
                        quantityLayout.visibility = View.VISIBLE
                        Utils.setProductQuantityForUser(userId, productId, isIncrease, null)
                    }
                }

                override fun onFailure(e: Exception?) {
                    addButton.visibility = View.GONE
                    quantityView.text = "1"
                    quantityLayout.visibility = View.VISIBLE
                    Utils.setProductQuantityForUser(userId, productId, isIncrease, null)
                }
            })
    }

    companion object {
        lateinit var userId: String
    }
}
