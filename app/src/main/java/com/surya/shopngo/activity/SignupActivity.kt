package com.surya.shopngo.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.surya.shopngo.R
import com.surya.shopngo.utils.Utils

class SignupActivity : AppCompatActivity() {
    var errMsg: String? = null
    var result = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        val email = findViewById<TextView>(R.id.email)
        val password = findViewById<TextView>(R.id.password)
        val repeatPassword = findViewById<TextView>(R.id.repeatPassword)


        //Verify OTP
        //Intent otpActivityIntent = new Intent(this, OTPActivity.class);
        val loginActivityIntent = Intent(this, LoginActivity::class.java)
        val productHomePage = Intent(this, ProductHomePageActivity::class.java)
        val registerBtn = findViewById<Button>(R.id.register)
        registerBtn.setOnClickListener { view: View? ->
            validateAndCreateUser(
                email.text.toString(),
                password.text.toString(),
                repeatPassword.text.toString()
            )
        }


        //Login Activity
        val loginTextView = findViewById<TextView>(R.id.createAccount)
        loginTextView.setOnClickListener { view: View? -> startActivity(loginActivityIntent) }
    }

    fun validateAndCreateUser(email: String, password: String, retypedPassword: String) {
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
        if (!Utils.isValidPassword(password)) {
            Toast.makeText(
                applicationContext,
                getString(R.string.user_password_invalid_error_msg),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (retypedPassword.isEmpty()) {
            Toast.makeText(
                applicationContext,
                getString(R.string.user_retyped_password_empty_error_msg),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (password != retypedPassword) {
            Toast.makeText(
                applicationContext,
                getString(R.string.user_password_not_same_msg),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        createUser(email, password)
    }

    fun createUser(email: String?, password: String?) {
        val context: Context = this
        try {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            context,
                            R.string.user_account_creation_success,
                            Toast.LENGTH_SHORT
                        ).show()
                        val loginPageIntent = Intent(context, LoginActivity::class.java)
                        loginPageIntent.putExtra("email", email)
                        context.startActivity(loginPageIntent)
                        (context as Activity).finish() // Close the login activity to prevent going back to it with the back button
                    } else {
                        Toast.makeText(
                            context,
                            R.string.user_account_creation_failure,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}