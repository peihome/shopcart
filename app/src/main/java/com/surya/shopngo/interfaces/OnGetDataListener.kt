package com.surya.shopngo.interfaces

interface OnGetDataListener {
    fun onSuccess(data: HashMap<String?, Any?>?)
    fun onFailure(e: Exception?)
}
