package com.surya.shopcart.interfaces;

import java.util.HashMap;

public interface OnGetDataListener {
    void onSuccess(HashMap<String, Object> data);
    void onFailure(Exception e);
}
