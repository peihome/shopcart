package com.surya.shopngo.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.surya.shopngo.R
import com.surya.shopngo.utils.Utils


class LoginActivity : AppCompatActivity() {

    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth
    private val RC_SIGN_IN = 1011
    private val TAG = LoginActivity::class.java.simpleName
    private lateinit var progressDialog: ProgressDialog

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
                email.text.toString(), password.text.toString()
            )
        }

        //Google SignIn
        val googleBtn = findViewById<Button>(R.id.googleSignIn)
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        mAuth = FirebaseAuth.getInstance()

        googleBtn.setOnClickListener { view: View? ->
            handleGoogleSignIn();
        }

        //Signup Page Activity
        val createAccount = findViewById<TextView>(R.id.createAccount)
        val signupPage = Intent(this, SignupActivity::class.java)
        createAccount.setOnClickListener { view: View? -> startActivity(signupPage) }
    }

    fun handleGoogleSignIn() {
        signOutAndSignIn()
    }

    fun signOutAndSignIn() {
        progressDialog = ProgressDialog(this).apply {
            setMessage("Signing In...")
        }
        progressDialog.show()

        mGoogleSignInClient.signOut().addOnCompleteListener(
            this
        ) {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            progressDialog.dismiss()
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                account.idToken?.let { firebaseAuthWithGoogle(it) }
            } catch (e: ApiException) {
                Log.d(TAG, Log.getStackTraceString(e));
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val context: Context = this // Capture the activity context
        val credential = GoogleAuthProvider.getCredential(idToken, /*accessToken=*/ null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    navigateToProductPage(context);
                } else {
                    Toast.makeText(
                        context,
                        getString(R.string.user_signin_failure),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
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
                        navigateToProductPage(context);
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

    fun navigateToProductPage(context: Context) {
        Toast.makeText(
            context,
            getString(R.string.user_signin_success),
            Toast.LENGTH_SHORT
        ).show()
        val productPageIntent = Intent(context, ProductHomePageActivity::class.java)
        context.startActivity(productPageIntent)
        (context as Activity).finish()
    }
}