package com.surya.shopngo.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.surya.shopngo.R
import com.surya.shopngo.adapter.CartItemAdapter
import com.surya.shopngo.dataclass.Product
import com.surya.shopngo.interfaces.OnGetDataListener
import com.surya.shopngo.utils.Email
import com.surya.shopngo.utils.Utils

class ConfirmOrderActivity : AppCompatActivity(), CartItemAdapter.OnItemClickListener {
    lateinit var userId: String
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: CartItemAdapter
    private lateinit var cartItemsList: ArrayList<Product>
    lateinit var nameTV: TextView
    lateinit var streetAddressTV: TextView
    lateinit var addressLine2TV: TextView
    lateinit var holderNameTV: TextView
    lateinit var vendorTV: TextView
    lateinit var cardNumberTV: TextView
    lateinit var orderTotalTV: TextView
    lateinit var taxTV: TextView
    lateinit var subTotalTV: TextView
    lateinit var discountTV: TextView
    lateinit var changeCard: Button
    lateinit var changeAddress: Button
    lateinit var buyButton: Button
    private lateinit var progressDialog: ProgressDialog
    lateinit var mAuth: FirebaseAuth;

    fun openCartPage(item: MenuItem) {
        Utils.handleMenuCLick(this, item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_order)

        progressDialog = ProgressDialog(this).apply {
            setMessage("Loading...")
        }

        // Home navigation icon
        Utils.addHomeIconNavigation(this, findViewById(R.id.topAppBar))
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            userId = currentUser.uid
        }

        //Buttons
        changeCard = findViewById(R.id.changeCard)
        changeAddress = findViewById(R.id.changeAddress)
        buyButton = findViewById(R.id.buyButton)

        //Textviews
        nameTV = findViewById(R.id.name)
        streetAddressTV = findViewById(R.id.streetAddress)
        addressLine2TV = findViewById(R.id.addressLine2)
        holderNameTV = findViewById(R.id.holderName)
        vendorTV = findViewById(R.id.vendor)
        cardNumberTV = findViewById(R.id.cardNumber)
        orderTotalTV = findViewById(R.id.orderTotal)
        taxTV = findViewById(R.id.tax)
        subTotalTV = findViewById(R.id.grandTotal)
        discountTV = findViewById(R.id.discount)
        populateCardDetails()
        populateTotalPrice()
        populateAddressFields()
        changeCard.setOnClickListener(View.OnClickListener { view: View? ->
            val changeCardIntent = Intent(this, CardActivity::class.java)
            startActivity(changeCardIntent)
            finish()
        })
        changeAddress.setOnClickListener(View.OnClickListener { view: View? ->
            val changeAddress = Intent(this, CheckoutActivity::class.java)
            startActivity(changeAddress)
            finish()
        })
        buyButton.setOnClickListener(View.OnClickListener { view: View? ->
            Utils.deleteDataFromRealTimeDatabase(
                Utils.getUserCartItemsPath(userId)
            ) {
                val thakyouActivity = Intent(applicationContext, ThankyouActivity::class.java)
                startActivity(thakyouActivity)

                //Send confirmation email to user
                //mAuth.currentUser?.email?.let { it1 -> Email.sendEmail(it1) }
                finish()
            }
        })
    }

    fun populateAddressFields() {
        progressDialog.show()
        Utils.getMapDataFromRealTimeDataBase(
            Utils.getUserAddressPath(userId),
            object : OnGetDataListener {
                override fun onSuccess(dataMap: HashMap<String?, Any?>?) {
                    nameTV.text = dataMap!!["firstName"].toString() + " " + dataMap["lastName"]
                    streetAddressTV.text =
                        dataMap["streetAddress"].toString() + " F.No " + dataMap["floorNumber"]
                    addressLine2TV.text =
                        dataMap["city"].toString() + ", " + dataMap["province"] + ", " + dataMap["zipcode"]

                    progressDialog.dismiss()
                }

                override fun onFailure(e: Exception?) {
//                Toast.makeText(getApplicationContext(), "Please enter your shipping details!", Toast.LENGTH_SHORT).show();
                    val cardIntent = Intent(applicationContext, CheckoutActivity::class.java)
                    startActivity(cardIntent)

                    progressDialog.dismiss()
                    finish()
                }
            })
    }

    fun populateCardDetails() {
        Utils.getMapDataFromRealTimeDataBase(
            Utils.getUserCardPath(userId),
            object : OnGetDataListener {
                override fun onSuccess(dataMap: HashMap<String?, Any?>?) {
                    holderNameTV.text = dataMap!!["holderName"].toString() + ""
                    vendorTV.text = dataMap["vendor"].toString() + ""
                    if ((dataMap["vendor"].toString() + "").isEmpty()) {
                        vendorTV.visibility = View.GONE
                    } else {
                        vendorTV.visibility = View.VISIBLE
                    }
                    cardNumberTV.text = Utils.getMaskedCardNumberForDisplay(
                        dataMap["cardNumber"].toString() + ""
                    )
                }

                override fun onFailure(e: Exception?) {

                    // Toast.makeText(getApplicationContext(), "Please enter your card details!", Toast.LENGTH_SHORT).show();
                    val cardIntent = Intent(applicationContext, CardActivity::class.java)
                    startActivity(cardIntent)
                    finish()
                }
            })
    }

    fun populateTotalPrice() {
        Utils.handleTotalPriceChange(
            userId,
            null,
            findViewById(R.id.orderTotal),
            findViewById(R.id.tax),
            findViewById(R.id.grandTotal),
            null,
            discountTV
        )
        handleCartItemsForView()
    }

    fun handleCartItemsForView() {
        Utils.getMapDataFromRealTimeDataBase(
            Utils.getUserCartItemsPath(userId),
            object : OnGetDataListener {
                override fun onSuccess(dataMap: HashMap<String?, Any?>?) {
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
                                    cartItemsList.add(
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
                                if (cartItemsList.size != products.size) {
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
                                                cartItemsList.add(
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
                                            if (cartItemsList.size != products.size) {

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
                                                            cartItemsList.add(
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

                override fun onFailure(e: Exception?) {}
            })
    }

    fun showCartItems() {
        //ViewPager2 Flyer
        recyclerView = findViewById(R.id.rvCartPage)
        adapter = CartItemAdapter(cartItemsList, false)
        adapter.setOnItemClickListener(this as CartItemAdapter.OnItemClickListener)
        recyclerView.setAdapter(adapter)
        recyclerView.setLayoutManager(
            LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
            )
        )
        Utils.handleTotalPriceChange(userId, findViewById(R.id.buyButton))
    }

    override fun onItemClick(view: View?, position: Int, type: String?) {
        val product = cartItemsList[position]
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
        }
    }
}