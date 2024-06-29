package com.surya.shopngo.cardpage

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.surya.shopngo.ProductHomePageActivity
import com.surya.shopngo.R
import com.surya.shopngo.checkout.CheckoutActivity
import com.surya.shopngo.confirmorder.ConfirmOrderActivity
import com.surya.shopngo.interfaces.OnGetDataListener
import com.surya.shopngo.utils.Utils
import java.util.Calendar
import java.util.regex.Pattern

class CardActivity : AppCompatActivity() {
    lateinit var holderNameET: TextInputEditText
    lateinit var expiryDateET: TextInputEditText
    lateinit var cardNumberET: TextInputEditText
    lateinit var cvvET: TextInputEditText
    lateinit var holderName: String
    lateinit var cardNumber: String
    lateinit var expiryDate: String
    lateinit var cvv: String
    lateinit var userId: String
    lateinit var cardNumberLayout: TextInputLayout
    fun openCartPage(item: MenuItem) {
        Utils.handleMenuCLick(this, item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.card_page)

        // Home navigation icon
        Utils.addHomeIconNavigation(this, findViewById(R.id.topAppBar))
        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            userId = currentUser.uid
        }
        holderNameET = findViewById(R.id.holderName)
        cardNumberET = findViewById(R.id.cardNumber)
        expiryDateET = findViewById(R.id.expiryDate)
        cvvET = findViewById(R.id.cvv)
        cardNumberLayout = findViewById(R.id.cardNumberLayout)
        val continueButton = findViewById<Button>(R.id.continueButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)
        prefillFormIfDataExists()
        continueButton.setOnClickListener { view: View? ->
            holderName = holderNameET.getText().toString()
            cardNumber = cardNumberET.getText().toString()
            expiryDate = expiryDateET.getText().toString()
            cvv = cvvET.getText().toString()
            val result = validationResult()
            if (result) {
                val cardMap = HashMap<String?, Any?>()
                cardMap["holderName"] = holderName
                cardMap["cardNumber"] = cardNumber
                cardMap["expiryDate"] = expiryDate
                cardMap["vendor"] =
                    (if (cardNumberLayout.hint == "Card Number") "" else cardNumberLayout.hint)!!
                cardMap["cvv"] = cvv
                Utils.addMapDataToRealTimeDataBase(cardMap, Utils.getUserCardPath(userId)) {
                    Toast.makeText(applicationContext, "Card details stored!", Toast.LENGTH_LONG)
                        .show()

                    //Proceed to card page
                    val cardPage = Intent(applicationContext, ConfirmOrderActivity::class.java)
                    startActivity(cardPage)
                    finish()
                }
            }
        }
        cancelButton.setOnClickListener { view: View? ->
            val cardPage = Intent(applicationContext, ProductHomePageActivity::class.java)
            startActivity(cardPage)
        }
        cardNumberET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateCardNumber(cardNumberET.getText().toString())
            }
        })
    }

    fun prefillFormIfDataExists() {
        Utils.getMapDataFromRealTimeDataBase(
            Utils.getUserCardPath(userId),
            object : OnGetDataListener {
                override fun onSuccess(dataMap: HashMap<String?, Any?>?) {
                    try {
                        holderNameET.setText(dataMap!!["holderName"].toString())
                        cardNumberET.setText(dataMap["cardNumber"].toString())
                        expiryDateET.setText(dataMap["expiryDate"].toString())
                        cvvET.setText(dataMap["cvv"].toString())
                    } catch (e: Exception) {
                        Log.i(TAG, e.stackTrace.toString())
                    }
                }

                override fun onFailure(e: Exception?) {
                    Log.i(TAG, e!!.stackTrace.toString())
                }
            })
        Utils.getMapDataFromRealTimeDataBase(Utils.vendorVsRegex, object : OnGetDataListener {
            override fun onSuccess(dataMap: HashMap<String?, Any?>?) {
                try {
                    for ((key, value) in dataMap!!) {
                        vendorVsRegex[key] = value.toString() + ""
                    }
                } catch (e: Exception) {
                    Log.i(TAG, e.stackTrace.toString())
                }
            }

            override fun onFailure(e: Exception?) {
                Log.i(TAG, e!!.stackTrace.toString())
            }
        })
    }

    fun validationResult(): Boolean {
        if (!validateFullName(holderName)) {
            Toast.makeText(this, "Please enter a valid name!", Toast.LENGTH_LONG).show()
            return false
        }
        if (!validateCardNumber(cardNumber)) {
            Toast.makeText(this, "Please enter a valid card number!", Toast.LENGTH_LONG).show()
            return false
        }
        if (!validateExpiryDate(expiryDate)) {
            Toast.makeText(this, "Please enter a valid expiry date!", Toast.LENGTH_LONG).show()
            return false
        }
        if (!validateCVV(cvv)) {
            Toast.makeText(this, "Please enter a valid cvv!", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    fun validateCardNumber(cardNumber: String?): Boolean {
        for ((key, value) in vendorVsRegex) {
            regex = Pattern.compile(value)
            if (regex.matcher(cardNumber).matches()) {
                cardNumberLayout.hint = key
                return true
            } else {
                cardNumberLayout.hint = "Card Number"
            }
        }
        return false
    }

    fun validateExpiryDate(expiryDate: String?): Boolean {
        try {
            val month = (expiryDate!![0].toString() + "" + expiryDate[1]).toByte()
            val currentYear = Calendar.getInstance()[Calendar.YEAR]
            val currentMonth = Calendar.getInstance()[Calendar.MONTH]
            val yearArr = (currentYear.toString() + "").toCharArray()
            var year = 0
            year = if (expiryDate.length >= 4) {
                (yearArr[0].toString() + "" + yearArr[1] + "" + expiryDate[2] + "" + expiryDate[3]).toInt()
            } else {
                Toast.makeText(this, "Please enter year!", Toast.LENGTH_LONG).show()
                return false
            }
            if (month > currentMonth && month <= 12) {
                if (year >= currentYear && year <= currentYear + 4) {
                    return true
                } else {
                    Toast.makeText(this, "Invalid Year or Card Expired!", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Please enter a valid month!", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Log.i(TAG, e.stackTrace.toString())
        }
        return false
    }

    companion object {
        private val TAG = CheckoutActivity::class.java.simpleName
        private lateinit var regex: Pattern
        private val vendorVsRegex = HashMap<String?, String>()
        fun validateCVV(cvv: String?): Boolean {
            regex = Pattern.compile("^[0-9]{3}$")
            return regex.matcher(cvv).matches()
        }

        fun validateFullName(name: String?): Boolean {
            val pattern = "^[A-Za-z ]{3,80}$"
            val regex = Pattern.compile(pattern)
            return regex.matcher(name).matches()
        }
    }
}