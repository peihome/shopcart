package com.surya.shopngo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.surya.shopngo.utils.Utils

class LoginActivity : AppCompatActivity() {
    override fun onBackPressed() {
        if (false) {
            super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Initialise FireBase
        FirebaseApp.initializeApp(this)
        val email = findViewById<EditText>(R.id.email)

        //Setting email from signup page
        val password = findViewById<EditText>(R.id.password)

        //Login Button Activity
        val loginBtn = findViewById<Button>(R.id.register)
        val productPageIntent = Intent(this, ProductHomePageActivity::class.java)
        loginBtn.setOnClickListener { view: View? ->
            isValidEmailAndPassword(
                email.getText().toString(), password.getText().toString()
            )
        }


        //Intent productPage = findViewById(R.id.)

        //Signup Page Activity
        val createAccount = findViewById<TextView>(R.id.createAccount)
        val signupPage = Intent(this, SignupActivity::class.java)
        createAccount.setOnClickListener { view: View? -> startActivity(signupPage) }
    }

    fun isValidEmailAndPassword(email: String, password: String) {
        if (email.isEmpty()) {
            Toast.makeText(
                applicationContext,
                getString(R.string.user_email_empty_error_msg),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (!Utils.isValidEmail(email)) {
            Toast.makeText(
                applicationContext,
                getString(R.string.user_email_invalid_error_msg),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (password.isEmpty()) {
            Toast.makeText(
                applicationContext,
                getString(R.string.user_password_empty_error_msg),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        isValidUser(email, password)
    }

    fun isValidUser(email: String?, password: String?) {
        val context: Context = this // Capture the activity context
        Toast.makeText(context, getString(R.string.user_signin_process), Toast.LENGTH_SHORT).show()
        try {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            context,
                            getString(R.string.user_signin_success),
                            Toast.LENGTH_SHORT
                        ).show()
                        val productPageIntent = Intent(context, ProductHomePageActivity::class.java)
                        context.startActivity(productPageIntent)
                        (context as Activity).finish()
                    } else {
                        Toast.makeText(
                            context,
                            getString(R.string.user_signin_failure),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}