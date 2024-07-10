package com.surya.shopngo.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.surya.shopngo.activity.ProductHomePageActivity
import com.surya.shopngo.R
import com.surya.shopngo.activity.CartActivity
import com.surya.shopngo.activity.EmptyCartActivity
import com.surya.shopngo.interfaces.OnGetDataListener
import java.util.regex.Pattern
import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress

object Utils {
    private const val appRoot = "shopNGo"
    const val province = appRoot + "/province"
    const val vendorVsRegex = appRoot + "/vendorVsRegex"
    private const val products = appRoot + "/products"
    private const val users = appRoot + "/users"
    const val fruits = products + "/fruits"
    const val veggies = products + "/veggies"
    const val beverages = products + "/beverages"
    const val flyers = appRoot + "/flyers"
    const val flyerImages = flyers + "/images"
    private val TAG = Utils::class.java.simpleName
    var totalQuantity: Int = 0
    var taxFloat = 0f
    var discount = 0f
    fun addDataToFireStore(
        obj: HashMap<String?, Any?>?,
        path: String?,
        onCompleteListener: OnCompleteListener<Void?>
    ) {
        val db = FirebaseFirestore.getInstance()
        // Add a new document with a generated ID
        db.collection(path!!)
            .add(obj!!)
            .addOnSuccessListener { documentReference: DocumentReference? ->
                onCompleteListener.onComplete(
                    Tasks.forResult(null)
                ) // Complete the task successfully
            }
            .addOnFailureListener { e: Exception? ->
                onCompleteListener.onComplete(
                    Tasks.forException(
                        e!!
                    )
                ) // Complete the task with an error
            }
    }

    fun getFireStoreData(path: String?, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        val db = FirebaseFirestore.getInstance()
        db.collection(path!!)
            .get()
            .addOnSuccessListener { documentReference: QuerySnapshot ->
                onCompleteListener.onComplete(
                    Tasks.forResult(documentReference)
                ) // Complete the task successfully
            }
            .addOnFailureListener { e: Exception? ->
                onCompleteListener.onComplete(
                    Tasks.forException(
                        e!!
                    )
                ) // Complete the task with an error
            }
    }

    fun getFireStoreDataFromSubCollection(
        path: String?,
        onCompleteListener: OnCompleteListener<DocumentSnapshot>
    ) {
        val db = FirebaseFirestore.getInstance()
        db.document(path!!)
            .get()
            .addOnSuccessListener { documentReference: DocumentSnapshot ->
                onCompleteListener.onComplete(
                    Tasks.forResult(documentReference)
                ) // Complete the task successfully
            }
            .addOnFailureListener { e: Exception? ->
                onCompleteListener.onComplete(
                    Tasks.forException(
                        e!!
                    )
                ) // Complete the task with an error
            }
    }

    fun getFireStoreDataById(
        path: String?,
        id: String?,
        onCompleteListener: OnCompleteListener<DocumentSnapshot>
    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection(path!!)
            .document(id!!)
            .get()
            .addOnSuccessListener { documentReference: DocumentSnapshot ->
                onCompleteListener.onComplete(
                    Tasks.forResult(documentReference)
                ) // Complete the task successfully
            }
            .addOnFailureListener { e: Exception? ->
                onCompleteListener.onComplete(
                    Tasks.forException(
                        e!!
                    )
                ) // Complete the task with an error
            }
    }

    fun getFireStoreDataByIds(
        path: String?,
        documentIds: List<String?>?,
        onCompleteListener: OnCompleteListener<QuerySnapshot?>?
    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection(path!!)
            .whereIn(FieldPath.documentId(), documentIds!!)
            .get()
            .addOnCompleteListener(onCompleteListener!!)
    }

    fun updateFirestoreDataById(
        path: String?,
        id: String?,
        obj: Map<String?, Any?>?,
        onCompleteListener: OnCompleteListener<Void?>?
    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection(path!!).document(id!!)
            .update(obj!!)
            .addOnCompleteListener(onCompleteListener!!)
    }

    fun addMapDataToRealTimeDataBase(obj: HashMap<String?, Any?>?, path: String?) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(path!!)
        myRef.setValue(obj)
    }

    fun addMapDataToRealTimeDataBase(
        obj: HashMap<String?, Any?>?,
        path: String?,
        onSuccessListener: OnSuccessListener<Void?>?
    ) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(path!!)
        myRef.setValue(obj)
            .addOnSuccessListener(onSuccessListener!!)
    }

    fun getMapDataFromRealTimeDataBase(path: String?, listener: OnGetDataListener) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(path!!)
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.value
                if (value is HashMap<*, *>) {
                    val data = value as HashMap<String?, Any?>
                    listener.onSuccess(data)
                } else {
                    listener.onFailure(Exception("Data is not in expected format"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException())
                listener.onFailure(databaseError.toException())
            }
        })
    }

    fun deleteDataFromRealTimeDatabase(
        path: String?,
        onSuccessListener: OnSuccessListener<Void?>?
    ) {
        val database = FirebaseDatabase.getInstance()
        database.getReference(path!!).removeValue().addOnSuccessListener(onSuccessListener!!)
    }

    fun setProductQuantityForUser(
        userId: String,
        productId: String?,
        isIncrease: Boolean,
        proceedButton: Button?
    ) {
        setProductQuantityForUser(userId, productId, isIncrease, proceedButton, null)
    }

    fun setProductQuantityForUser(
        userId: String,
        productId: String?,
        isIncrease: Boolean,
        proceedButton: Button?,
        grandsubtotal: TextView?
    ) {
        setProductQuantityForUser(userId, productId, isIncrease, proceedButton, grandsubtotal, null)
    }

    fun setProductQuantityForUser(
        userId: String,
        productId: String?,
        isIncrease: Boolean,
        proceedButton: Button?,
        grandsubtotal: TextView?,
        context: Context?
    ) {
        getMapDataFromRealTimeDataBase(getUserCartItemsPath(userId), object : OnGetDataListener {
            override fun onSuccess(productVsQuantity: HashMap<String?, Any?>?) {
                var quantityFromRemote =
                    (productVsQuantity!!.getOrDefault(productId, 0).toString() + "").toByte()
                if (isIncrease) {
                    quantityFromRemote++
                } else {
                    quantityFromRemote--
                }
                if (quantityFromRemote <= 0) {
                    productVsQuantity.remove(productId)
                } else if (quantityFromRemote >= 20) {
                    quantityFromRemote = 20
                    productVsQuantity[productId] = quantityFromRemote.toInt()
                } else {
                    productVsQuantity[productId] = quantityFromRemote.toInt()
                }
                addMapDataToRealTimeDataBase(productVsQuantity, getUserCartItemsPath(userId))
                handleTotalPriceChange(userId, proceedButton, grandsubtotal, null, null, context)
            }

            override fun onFailure(e: Exception?) {
                val productVsQuantity = HashMap<String?, Any?>()
                productVsQuantity[productId] = 1
                addMapDataToRealTimeDataBase(productVsQuantity, getUserCartItemsPath(userId))
            }
        })
    }

    fun getUserCartItemsPath(userId: String): String {
        return users + '/' + userId + "/products"
    }

    fun getUserAddressPath(userId: String): String {
        return users + '/' + userId + "/address"
    }

    fun getUserCardPath(userId: String): String {
        return users + '/' + userId + "/card"
    }

    fun addHomeIconNavigation(context: Context, menuBar: Toolbar) {
        menuBar.setNavigationIcon(R.drawable.ic_home)
        menuBar.setNavigationOnClickListener {
            if (context.javaClass != ProductHomePageActivity::class.java) {
                context.startActivity(Intent(context, ProductHomePageActivity::class.java))
            }
        }
    }

    fun handleMenuCLick(context: Context, item: MenuItem) {
        when (item.itemId) {
            R.id.cartIcon -> {
                if (context.javaClass == CartActivity::class.java) {
                    return
                }
                context.startActivity(Intent(context, CartActivity::class.java))
            }
        }
    }

    fun getSubtotalStr(quantity: Byte, price: Double): String {
        return (Math.round(quantity * price * 100.0) / 100.0).toString() + ""
    }

    fun getSubtotalFloat(quantity: Byte, price: Double): Float {
        return (Math.round(quantity * price * 100.0) / 100.0).toFloat()
    }

    @JvmOverloads
    fun handleTotalPriceChange(
        userId: String,
        proceedToBuy: Button?,
        total: TextView? = null,
        tax: TextView? = null,
        subTotal: TextView? = null,
        context: Context? = null,
        discountTV: TextView? = null
    ) {
        totalQuantity = 0
        taxFloat = 0f
        discount = 0f
        getMapDataFromRealTimeDataBase(getUserCartItemsPath(userId), object : OnGetDataListener {
            override fun onSuccess(dataMap: HashMap<String?, Any?>?) {
                val products = ArrayList<String?>()
                if (dataMap != null && !dataMap.isEmpty()) {
                    for ((key, value) in dataMap) {
                        products.add(key)
                        totalQuantity = totalQuantity + (value.toString() + "").toInt()
                    }
                }
                if (!products.isEmpty()) {

                    //Fruits
                    getFireStoreDataByIds(fruits, products, OnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val subTotals = ArrayList<Float>()
                            var quantity: Byte = 0
                            for (document in task.result!!) {
                                quantity = (dataMap!![document.id].toString() + "").toByte()
                                val price = (document["price"].toString() + "").toDouble()
                                if (quantity <= 0) {
                                    continue
                                }
                                handleCalculation(document, quantity, price, subTotals)
                            }
                            if (subTotals.size != products.size) {
                                //Veggies
                                getFireStoreDataByIds(
                                    veggies,
                                    products,
                                    OnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            var quantity: Byte = 0
                                            for (document in task.result!!) {
                                                quantity =
                                                    (dataMap!![document.id].toString() + "").toByte()
                                                val price =
                                                    (document["price"].toString() + "").toDouble()
                                                if (quantity <= 0) {
                                                    continue
                                                }
                                                handleCalculation(
                                                    document,
                                                    quantity,
                                                    price,
                                                    subTotals
                                                )
                                            }
                                            if (subTotals.size != products.size) {

                                                //Veggies
                                                getFireStoreDataByIds(
                                                    beverages,
                                                    products,
                                                    OnCompleteListener { task ->
                                                        if (task.isSuccessful) {
                                                            var quantity: Byte = 0
                                                            for (document in task.result!!) {
                                                                quantity =
                                                                    (dataMap!![document.id].toString() + "").toByte()
                                                                val price =
                                                                    (document["price"].toString() + "").toDouble()
                                                                if (quantity <= 0) {
                                                                    continue
                                                                }
                                                                handleCalculation(
                                                                    document,
                                                                    quantity,
                                                                    price,
                                                                    subTotals
                                                                )
                                                            }
                                                            setTotalAmount(
                                                                subTotals,
                                                                proceedToBuy,
                                                                total,
                                                                tax,
                                                                subTotal,
                                                                discountTV
                                                            )
                                                        } else {
                                                            Log.w(
                                                                "getData",
                                                                "Error getting documents.",
                                                                task.exception
                                                            )
                                                        }
                                                    })
                                            } else {
                                                setTotalAmount(
                                                    subTotals,
                                                    proceedToBuy,
                                                    total,
                                                    tax,
                                                    subTotal,
                                                    discountTV
                                                )
                                            }
                                        } else {
                                            Log.w(
                                                "getData",
                                                "Error getting documents.",
                                                task.exception
                                            )
                                        }
                                    })
                            } else {
                                setTotalAmount(
                                    subTotals,
                                    proceedToBuy,
                                    total,
                                    tax,
                                    subTotal,
                                    discountTV
                                )
                            }
                        } else {
                            Log.w("getData", "Error getting documents.", task.exception)
                        }
                    })
                }
            }

            override fun onFailure(e: Exception?) {
                if (context != null) {
                    val subTotals = ArrayList<Float>()
                    setTotalAmount(subTotals, proceedToBuy, total, tax, subTotal)
                    val emptyCart = Intent(context, EmptyCartActivity::class.java)
                    context.startActivity(emptyCart)
                }
            }
        })
    }

    fun setTotalAmount(
        subTotals: ArrayList<Float>,
        proceedButton: Button?,
        total: TextView?,
        tax: TextView?,
        subTotalView: TextView?
    ) {
        setTotalAmount(subTotals, proceedButton, total, tax, subTotalView, null)
    }

    fun setTotalAmount(
        subTotals: ArrayList<Float>,
        proceedButton: Button?,
        total: TextView?,
        tax: TextView?,
        subTotalView: TextView?,
        discountTV: TextView?
    ) {
        var totalAmount = 0f
        for (subTotal in subTotals) {
            totalAmount += subTotal
        }
        var itemStr = "(" + totalQuantity + " items)"
        if (totalQuantity.toInt() == 1) {
            itemStr = "(" + subTotals.size + " item)"
        }
        if (proceedButton != null) {
            proceedButton.text = "Proceed to checkout $itemStr"
        }
        if (tax != null) {
            //tax.setText("$ "+getSubtotalStr((byte)1, totalAmount * 0.13));
            tax.text = "$ " + getSubtotalStr(1.toByte(), taxFloat.toDouble())
        }
        if (discountTV != null) {
            discountTV.text = "$ " + getSubtotalStr(1.toByte(), discount.toDouble())
        }
        if (subTotalView != null) {
            subTotalView.text =
                "$ " + getSubtotalStr(1.toByte(), (totalAmount + taxFloat - discount).toDouble())
        }
        if (total != null) {
            total.text = "$ " + getSubtotalStr(1.toByte(), totalAmount.toDouble())
        }
    }

    fun getMaskedCardNumberForDisplay(cardNumber: String): String {
        val cardNumberSB = StringBuilder(cardNumber)
        for (i in 0 until cardNumber.length - 4) {
            cardNumberSB.setCharAt(i, '*')
        }
        return cardNumberSB.toString()
    }

    fun isValidEmail(email: String?): Boolean {
        var valid = true
        try {
            val emailAddr = InternetAddress(email)
            emailAddr.validate()
        } catch (e: AddressException) {
            valid = false
        }
        return valid
    }

    fun isValidPassword(password: String?): Boolean {
        // Minimum 8 chars
        // Maximum 15 chars
        // Atleast 1 upper case
        // Atleast 1 lower case
        // Atleast 1 number
        // Atleast 1 special char
        val regex =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$")
        return regex.matcher(password).matches()
    }

    fun handleCalculation(
        document: QueryDocumentSnapshot,
        quantity: Byte,
        price: Double,
        subTotals: ArrayList<Float>
    ) {
        val subTotal = getSubtotalFloat(quantity, price)
        var taxPercent = 0f
        var discountPercent = 0f
        try {
            taxPercent = (document["tax"].toString() + "").toFloat()
        } catch (e: Exception) {
        }
        try {
            discountPercent = (document["discount"].toString() + "").toFloat()
        } catch (e: Exception) {
        }
        if (document.contains("tax")) {
            taxFloat += getSubtotalFloat(1.toByte(), (subTotal * taxPercent).toDouble())
        }
        if (document.contains("discount")) {
            discount += getSubtotalFloat(1.toByte(), (subTotal * discountPercent).toDouble())
        }
        subTotals.add(subTotal)
    }
}
