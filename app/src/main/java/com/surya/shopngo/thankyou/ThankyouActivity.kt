package com.surya.shopngo.thankyou

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.surya.shopngo.ProductHomePageActivity
import com.surya.shopngo.R
import com.surya.shopngo.utils.Utils

class ThankyouActivity : AppCompatActivity() {
    var userId: String? = null
    fun openCartPage(item: MenuItem?) {
        Utils.handleMenuCLick(this, item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thankyou)

        // Home navigation icon
        Utils.addHomeIconNavigation(this, findViewById(R.id.topAppBar))
        val homePage = findViewById<Button>(R.id.homePage)
        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            userId = currentUser.uid
        }
        homePage.setOnClickListener { view: View? ->
            //Proceed to home page
            val productPage = Intent(applicationContext, ProductHomePageActivity::class.java)
            startActivity(productPage)
            finish()
        }
    }
}