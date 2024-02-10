package com.byoosi.pos.data.pref
import android.content.Context
import android.util.Log
import com.byoosi.pos.data.local.ApiCredentials
import com.byoosi.pos.data.local.AppDatabase
import com.byoosi.pos.model.Login
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object SharedPref2 {

    private const val TAG = "SharedPref"

    fun setLoginData(context: Context, data: Login) {
        val apiCredentials = ApiCredentials(
            apiKey = data.message?.api_key ?: "",
            apiSecret = data.message?.api_secret ?: ""
        )

        GlobalScope.launch(Dispatchers.IO) {
            AppDatabase.getDatabase(context).apiCredentialsDao().insert(apiCredentials)
            Log.d(TAG, "API Key and Secret saved")
        }
    }

    fun getApiCredentials(context: Context, callback: (Pair<String, String>?) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val apiCredentials = AppDatabase.getDatabase(context).apiCredentialsDao().getApiCredentials()
            val pair = apiCredentials?.let { Pair(it.apiKey, it.apiSecret) }
            callback(pair)
        }
    }
}
