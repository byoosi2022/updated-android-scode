package com.byoosi.pos.data.pref


import android.util.Log
import com.byoosi.pos.model.Login
import com.byoosi.pos.model.ProductItem
import com.byoosi.pos.utils.AppSignatureHelper.TAG
import com.chibatching.kotpref.KotprefModel
import com.chibatching.kotpref.gsonpref.gsonNullablePref
import com.chibatching.kotpref.gsonpref.gsonPref

/**
 * Created by pintusingh on 20/12/20.
 */

object SharedPref : KotprefModel() {
    var isLogin by booleanPref()
    var apiKey by stringPref()
    var apiSecret by stringPref()
    var login by gsonNullablePref<Login>()
    var cartItems by gsonPref<ArrayList<ProductItem>>(arrayListOf())

    fun setLoginData(data: Login) {
        login = data
        isLogin = true
        apiKey = data.message?.api_key ?: ""
        apiSecret = data.message?.api_secret ?: ""
        Log.d("SharedPref", "Login data set: $data")
    }

    fun clearPref() {
        clear()
        Log.d("SharedPref", "Shared preferences cleared")
    }
}
