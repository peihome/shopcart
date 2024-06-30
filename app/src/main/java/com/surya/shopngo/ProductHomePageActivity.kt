package com.surya.shopngo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.firebase.auth.FirebaseAuth
import com.surya.shopngo.interfaces.OnGetDataListener
import com.surya.shopngo.utils.Utils
import java.util.Timer
import java.util.TimerTask

class ProductHomePageActivity : AppCompatActivity(), ProductAdapter.OnItemClickListener {
    lateinit var vPager: ViewPager2
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: RecyclerView.Adapter<*>
    lateinit var productList: ArrayList<Product>
    lateinit var flyersList: ArrayList<Flyer>

    init {
        flyersList = ArrayList<Flyer>()
    }

    fun openCartPage(item: MenuItem) {
        Utils.handleMenuCLick(this, item)
    }

    override fun onBackPressed() {
        if (false) {
            super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_list_horizontal)


        // Home navigation icon
        Utils.addHomeIconNavigation(this, findViewById(R.id.topAppBar))
        handleFLyers()
        handleProducts()
        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            userId = currentUser.uid
        }
    }

    private fun handleProducts() {
        handleProduct(Utils.fruits, R.id.productRecyclerView)
        handleProduct(Utils.veggies, R.id.vegetableRV)
        handleProduct(Utils.beverages, R.id.juiceRV)
    }

    private fun handleProduct(path: String, viewId: Int) {
        val currentContext: Context = this
        Utils.getFireStoreData(path) { task ->
            if (task.isSuccessful) {
                productList = ArrayList()
                for (document in task.result) {
                    val data: Map<*, *> = document.getData()
                    productList.add(
                        Product(
                            document.id,
                            data["image"].toString() + "",
                            data["title"].toString() + "",
                            data["description"].toString() + "",
                            (data["price"].toString() + "").toDouble(),
                            path
                        )
                    )
                }
                recyclerView = findViewById(viewId)
                val productAdapter = ProductAdapter(productList)
                productAdapter.setOnItemClickListener(currentContext as ProductAdapter.OnItemClickListener)
                recyclerView.setAdapter(productAdapter)
                recyclerView.setLayoutManager(
                    LinearLayoutManager(
                        currentContext,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                )
            } else {
                Log.w("getData", "Error getting documents.", task.exception)
            }
        }
    }

    private fun handleFLyers() {
        Utils.getFireStoreData(Utils.flyerImages) { task ->
            if (task.isSuccessful) {
                for (document in task.result) {
                    flyersList.add(Flyer(document.getData()["image"].toString() + ""))
                }

                //ViewPager2 Flyer
                vPager = findViewById(R.id.vPager)
                adapter = FlyerAdapter(flyersList)
                vPager.setAdapter(adapter)

                //autoSlideFlyers(adapter, vPager);
            } else {
                Log.w("getData", "Error getting documents.", task.exception)
            }
        }
    }

    override fun onItemClick(
        view: View,
        position: Int,
        productList: ArrayList<Product>,
        type: String?
    ) {
        val product = productList[position]
        val quantity: TextView
        var itemDetailLayout: LinearLayout? = null
        val itemDetailLayoutParent: RelativeLayout
        var quantityByte: Byte = 0
        when (type) {
            "image" -> {
                val productDetail = Intent(this, ProductDetailActivity::class.java)
                productDetail.putExtra("title", product.fetchTitle())
                productDetail.putExtra("description", product.fetchDescription())
                productDetail.putExtra("price", product.fetchPrice())
                productDetail.putExtra("image", product.fetchImage())
                productDetail.putExtra("id", product.fetchId())
                startActivity(productDetail)
            }

            "addButton" -> {
                itemDetailLayoutParent = view.parent as RelativeLayout
                itemDetailLayout = itemDetailLayoutParent.getChildAt(2) as LinearLayout
                itemDetailLayout.visibility = View.VISIBLE
                view.visibility = View.GONE
                val quantityView = itemDetailLayout.getChildAt(1) as TextView
                handleQuantityLayout(1.toByte(), itemDetailLayout, quantityView, product, true)
            }

            "reduceQuantity" -> {
                itemDetailLayout = view.parent as LinearLayout
                quantity = itemDetailLayout.getChildAt(1) as TextView
                quantityByte = (quantity.text.toString() + "").toByte()
                handleQuantityLayout(--quantityByte, itemDetailLayout, quantity, product, false)
            }

            "increaseQuantity" -> {
                itemDetailLayout = view.parent as LinearLayout
                quantity = itemDetailLayout.getChildAt(1) as TextView
                quantityByte = (quantity.text.toString() + "").toByte()
                handleQuantityLayout(++quantityByte, itemDetailLayout, quantity, product, true)
            }
        }
    }

    fun handleQuantityLayout(
        quantity: Byte,
        itemDetailLayout: LinearLayout?,
        quantityView: TextView,
        product: Product?,
        isIncrease: Boolean
    ) {
        val addButton = (itemDetailLayout!!.parent as RelativeLayout).getChildAt(1) as Button
        Utils.getMapDataFromRealTimeDataBase(
            Utils.getUserCartItemsPath(userId),
            object : OnGetDataListener {
                override fun onSuccess(dataMap: HashMap<String?, Any?>?) {
                    if (!dataMap!!.containsKey(quantityView.tag.toString() + "")) {
                        addButton.visibility = View.GONE
                        quantityView.text = "1"
                        itemDetailLayout.visibility = View.VISIBLE
                        Utils.setProductQuantityForUser(
                            userId,
                            quantityView.tag.toString() + "",
                            isIncrease,
                            null
                        )
                        return
                    }
                    var quantityFromRemote =
                        (dataMap[quantityView.tag.toString() + ""].toString() + "").toByte()
                    if (isIncrease) {
                        quantityFromRemote++
                    } else {
                        quantityFromRemote--
                    }
                    if (quantity <= 0 || quantityFromRemote <= 0) {
                        addButton.visibility = View.VISIBLE
                        quantityView.text = "1"
                        itemDetailLayout.visibility = View.GONE
                        Utils.setProductQuantityForUser(
                            userId,
                            quantityView.tag.toString() + "",
                            isIncrease,
                            null
                        )
                    } else if (quantity > 20 || quantityFromRemote > 20) {
                        Toast.makeText(
                            applicationContext,
                            "Reached maximum limit!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        quantityView.text = quantity.toString() + ""
                        Utils.setProductQuantityForUser(
                            userId,
                            quantityView.tag.toString() + "",
                            isIncrease,
                            null
                        )
                    }
                }

                override fun onFailure(e: Exception?) {
                    addButton.visibility = View.GONE
                    quantityView.text = "1"
                    itemDetailLayout.visibility = View.VISIBLE
                    Utils.setProductQuantityForUser(
                        userId,
                        quantityView.tag.toString() + "",
                        isIncrease,
                        null
                    )
                }
            })
    }

    override fun onPause() {
        super.onPause()
        cancelTimers()
    }

    override fun onStop() {
        super.onStop()
        cancelTimers()
    }

    companion object {
        lateinit var handler: Handler
        var currentPage: Int? = 0
        private var scrollTimer: Timer? = null
        private var scrollTimerTask: TimerTask? = null
        private lateinit var userId: String
        private lateinit var updateRunnable: Runnable
        fun autoSlideFlyers(adapter: RecyclerView.Adapter<*>, vPager: ViewPager2) {
            /*After setting the adapter use the timer */
            val totalPages = adapter.itemCount
            updateRunnable = Runnable {
                if (currentPage == totalPages) {
                    currentPage = 0
                }
                currentPage = currentPage!! + 1
                vPager.setCurrentItem(currentPage!!, true)
            }
            scrollTimer = Timer()
            scrollTimerTask = object : TimerTask() {
                override fun run() {
                    handler.post(updateRunnable)
                }
            }

            //scrollTimer.schedule(scrollTimerTask, 5000, 5000);
            vPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                        startTimers(10000, 5000)
                    }
                }
            })
        }

        private fun cancelTimers() {
            if (scrollTimer != null) {
                scrollTimer!!.cancel()
                scrollTimer = null
            }
            if (scrollTimerTask != null) {
                scrollTimerTask!!.cancel()
                scrollTimerTask = null
            }
        }

        private fun startTimers(delay: Long, period: Long) {
            cancelTimers()
            if (scrollTimer == null) {
                scrollTimer = Timer()
            } else {
                scrollTimer = Timer()
            }
            scrollTimerTask = object : TimerTask() {
                override fun run() {
                    handler.post(updateRunnable)
                }
            }
            scrollTimer!!.schedule(scrollTimerTask, delay, period)
        }
    }
}