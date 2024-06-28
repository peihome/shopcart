package com.surya.shopngo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class OTPActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        val loginTextView = findViewById<TextView>(R.id.createAccount)
        val loginActivityIntent = Intent(this, LoginActivity::class.java)
        loginTextView.setOnClickListener { view: View? -> startActivity(loginActivityIntent) }
    }
}