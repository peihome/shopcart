package com.surya.shopngo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)

        //Login Page Activity
        val clickToStart = findViewById<Button>(R.id.clickToStart)
        val loginPage = Intent(this, LoginActivity::class.java)
        clickToStart.setOnClickListener { view: View? -> startActivity(loginPage) }
    }

    override fun onStart() {
        super.onStart()
        Log.i("OnStart", "This is the Start Phase")
    }

    override fun onPause() {
        super.onPause()
        Log.i("OnPause", "This is the Pause Phase")
    }
}