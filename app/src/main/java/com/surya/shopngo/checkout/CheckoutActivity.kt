package com.surya.shopngo.checkout

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.surya.shopngo.ProductHomePageActivity
import com.surya.shopngo.R
import com.surya.shopngo.confirmorder.ConfirmOrderActivity
import com.surya.shopngo.interfaces.OnGetDataListener
import com.surya.shopngo.utils.Utils
import java.util.Locale
import java.util.regex.Pattern

class CheckoutActivity : AppCompatActivity() {
    lateinit private var userId: String
    lateinit var proviceTextView: MaterialAutoCompleteTextView
    lateinit var continueButton: Button
    lateinit var firstNameET: TextInputEditText
    lateinit var lastNameET: TextInputEditText
    lateinit var streetAddressET: TextInputEditText
    lateinit var floorNumberET: TextInputEditText
    lateinit var cityET: TextInputEditText
    lateinit var zipcodeET: TextInputEditText
    lateinit var cancelButton: Button
    fun openCartPage(item: MenuItem) {
        Utils.handleMenuCLick(this, item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.checkout_form)

        // Home navigation icon
        Utils.addHomeIconNavigation(this, findViewById(R.id.topAppBar))
        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            userId = currentUser.uid
        }
        proviceTextView = findViewById(R.id.proviceTextView)
        continueButton = findViewById(R.id.continueButton)
        firstNameET = findViewById(R.id.firstName)
        lastNameET = findViewById(R.id.lastName)
        streetAddressET = findViewById(R.id.streetAddress)
        floorNumberET = findViewById(R.id.floorNumber)
        cityET = findViewById(R.id.city)
        zipcodeET = findViewById(R.id.zipcode)
        cancelButton = findViewById(R.id.cancelButton)
        setValuesForProvince()
        prefillFormIfDataExists()
        continueButton.setOnClickListener(View.OnClickListener { view: View? ->
            firstName = firstNameET.getText().toString().trim { it <= ' ' }
            lastName = lastNameET.getText().toString().trim { it <= ' ' }
            streetAddress = streetAddressET.getText().toString().trim { it <= ' ' }
            floorNumber = floorNumberET.getText().toString().trim { it <= ' ' }
            city = cityET.getText().toString().trim { it <= ' ' }
            zipcode =
                zipcodeET.getText().toString().trim { it <= ' ' }.uppercase(Locale.getDefault())
            province = proviceTextView.getText().toString().trim { it <= ' ' }
            val result = validationResult(
                firstName,
                lastName,
                streetAddress,
                floorNumber,
                city,
                zipcode,
                province
            )
            if (result) {
                val addressMap = HashMap<String?, Any?>()
                addressMap["firstName"] = firstName
                addressMap["lastName"] = lastName
                addressMap["streetAddress"] = streetAddress
                addressMap["floorNumber"] = floorNumber
                addressMap["city"] = city
                addressMap["zipcode"] = zipcode
                addressMap["province"] = province
                Utils.addMapDataToRealTimeDataBase(
                    addressMap,
                    Utils.getUserAddressPath(userId)
                ) { //Proceed to card page
                    val cardPage = Intent(applicationContext, ConfirmOrderActivity::class.java)
                    startActivity(cardPage)
                }
            }
        })
        cancelButton.setOnClickListener(View.OnClickListener { view: View? ->
            //Proceed to card page
            val cardPage = Intent(applicationContext, ProductHomePageActivity::class.java)
            startActivity(cardPage)
            finish()
        })
    }

    fun setValuesForProvince() {
        val provinceTextInputLayout = findViewById<TextInputLayout>(R.id.province)
        val items: List<String> = mutableListOf(
            "Alberta",
            "British Columbia",
            "Manitoba",
            "New Brunswick",
            "Newfoundland",
            "Northwest Territories",
            "Nova Scotia",
            "Nunavut",
            "Ontario",
            "Prince Edward Island",
            "Quebec",
            "Saskatchewan",
            "Yukon"
        )
        val adapter = ArrayAdapter(this, R.layout.provice_layout, items)
        val provinceAutoCompleteTextView =
            provinceTextInputLayout.findViewById<MaterialAutoCompleteTextView>(R.id.proviceTextView)
        provinceAutoCompleteTextView.setAdapter(adapter)
    }

    fun validationResult(
        firstName: String?,
        lastName: String?,
        streetAddress: String?,
        floorNumber: String?,
        city: String?,
        zipcode: String?,
        province: String?
    ): Boolean {
        if (!validateName(firstName)) {
            Toast.makeText(this, "Please enter a valid first name!", Toast.LENGTH_LONG).show()
            return false
        }
        if (!validateName(lastName)) {
            Toast.makeText(this, "Please enter a valid last name!", Toast.LENGTH_LONG).show()
            return false
        }
        if (!validateStreetAddress(streetAddress)) {
            Toast.makeText(this, "Please enter a valid address!", Toast.LENGTH_LONG).show()
            return false
        }
        if (!validateFloorNumber(floorNumber)) {
            Toast.makeText(this, "Please enter a valid floor number!", Toast.LENGTH_LONG).show()
            return false
        }
        if (!validateCity(city)) {
            Toast.makeText(this, "Please enter a valid city name!", Toast.LENGTH_LONG).show()
            return false
        }
        if (!validateProvince(province)) {
            Toast.makeText(this, "Please choose a province!", Toast.LENGTH_LONG).show()
            return false
        }
        if (!validateZipCode(zipcode)) {
            Toast.makeText(this, "Please enter a proper zipcode!", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    fun prefillFormIfDataExists() {
        Utils.getMapDataFromRealTimeDataBase(
            Utils.getUserAddressPath(userId),
            object : OnGetDataListener {
                override fun onSuccess(dataMap: HashMap<String?, Any?>?) {
                    try {
                        firstNameET!!.setText(dataMap!!["firstName"].toString())
                        lastNameET!!.setText(dataMap["lastName"].toString())
                        streetAddressET!!.setText(dataMap["streetAddress"].toString())
                        floorNumberET!!.setText(dataMap["floorNumber"].toString())
                        cityET!!.setText(dataMap["city"].toString())
                        proviceTextView!!.setText(dataMap["province"].toString())
                        zipcodeET!!.setText(dataMap["zipcode"].toString())
                    } catch (e: Exception) {
                        Log.i(TAG, e.stackTrace.toString())
                    }
                }

                override fun onFailure(e: Exception?) {
                    Log.i(TAG, e!!.stackTrace.toString())
                }
            })
    }

    companion object {
        private val TAG = CheckoutActivity::class.java.getSimpleName()
        private var firstName: String? = null
        private var lastName: String? = null
        private var streetAddress: String? = null
        private var floorNumber: String? = null
        private var city: String? = null
        private var zipcode: String? = null
        private var province: String? = null
        private const val validationResult = false
        private var pattern: String? = null
        lateinit private var regex: Pattern
        fun validateName(name: String?): Boolean {
            val pattern = "^[A-Za-z]{3,50}$"
            val regex = Pattern.compile(pattern)
            return regex.matcher(name).matches()
        }

        fun validateStreetAddress(streetAddress: String?): Boolean {
            pattern = "^[\\w+(\\s\\w+){2,}]{5,100}$"
            regex = Pattern.compile(pattern)
            return regex.matcher(streetAddress).matches()
        }

        fun validateFloorNumber(floorNumber: String?): Boolean {
            try {
                if (floorNumber!!.isEmpty()) {
                    return true
                }
                val floorNumberByte = floorNumber.toByte()
                if (floorNumberByte >= 0 && floorNumberByte <= 100) {
                    return true
                }
            } catch (e: Exception) {
                Log.i(TAG, e.stackTrace.toString())
            }
            return false
        }

        fun validateCity(city: String?): Boolean {
            pattern = "^[[a-zA-Z]+(?:[\\s-][a-zA-Z]+)*]{5,40}$"
            regex = Pattern.compile(pattern)
            return regex.matcher(city).matches()
        }

        fun validateZipCode(zipcode: String?): Boolean {
            pattern =
                "^[ABCEGHJKLMNPRSTVXY]\\d[ABCEGHJKLMNPRSTVWXYZ][ -]?\\d[ABCEGHJKLMNPRSTVWXYZ]\\d$"
            regex = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE)
            return regex.matcher(zipcode).matches()
        }

        fun validateProvince(province: String?): Boolean {
            return if (!province!!.isEmpty()) {
                true
            } else false
        }
    }
}