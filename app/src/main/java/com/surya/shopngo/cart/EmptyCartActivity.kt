package com.surya.shopngo.cart

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.surya.shopngo.ProductHomePageActivity
import com.surya.shopngo.R
import com.surya.shopngo.utils.Utils

class EmptyCartActivity : AppCompatActivity() {
    fun openCartPage(item: MenuItem) {
        Utils.handleMenuCLick(this, item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empty_cart)

        // Home navigation icon
        Utils.addHomeIconNavigation(this, findViewById(R.id.topAppBar))
        val homePage = findViewById<Button>(R.id.homePage)
        homePage.setOnClickListener { view: View? ->
            val homePageIntent = Intent(this, ProductHomePageActivity::class.java)
            startActivity(homePageIntent)
            finish()
        }
    }
}