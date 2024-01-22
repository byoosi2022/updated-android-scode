package com.byoosi.pos.base

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.byoosi.pos.data.network.RequestInterface
import com.byoosi.pos.data.network.ResponseCode.*
import com.byoosi.pos.data.pref.SharedPref
import com.byoosi.pos.ui.splash.SplashActivity
import com.byoosi.pos.utils.AppGlobal
import com.byoosi.pos.utils.MyAppProgressDialog
import kotlinx.coroutines.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.json.JSONObject
import retrofit2.HttpException

abstract class BaseActivity(@LayoutRes layoutId: Int) : AppCompatActivity(layoutId) {
    protected val TAG: String = this::class.java.simpleName
    val requestInterface by lazy { RequestInterface.getInstance() }
    protected var listJobs = arrayListOf<Job>()
    private var noOfApiCall = 0
    lateinit var loader: MyAppProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loader = MyAppProgressDialog(this)
    }

    override fun onDestroy() {
        listJobs.forEach { it.cancel() }
        super.onDestroy()
    }

    fun <T> callApi(
        doShowLoader: Boolean = false,
        request: suspend () -> T,
        onSuccess: (T) -> Unit,
        onError: (Throwable) -> Unit = {}
    ) {
        if (!AppGlobal.isNetworkConnected(this))
            return AppGlobal.networkAlertDialog(this)
        if (doShowLoader && noOfApiCall == 0)
            showLoader()
        noOfApiCall++
        val job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = request()
                withContext(Dispatchers.Main) {
                    if (doShowLoader && noOfApiCall == 1)
                        hideLoader()
                    noOfApiCall--
                    onSuccess(result)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    if (doShowLoader && noOfApiCall == 1)
                        hideLoader()
                    noOfApiCall--
                    onError(e)
                    onResponseFailure(e)
                }
            }
        }
        listJobs.add(job)
    }

    protected fun onResponseFailure(throwable: Throwable) {
        Log.e(TAG, "onResponseFailure ${throwable.message.orEmpty()}")
        if (throwable is HttpException) {
            Log.e(TAG, "response code ${throwable.code()}")
            errorHandling(throwable)
        }
    }

    private fun errorHandling(throwable: HttpException) {
        val errorRawData = throwable.response()?.errorBody()?.string()?.trim()
        if (errorRawData.isNullOrEmpty()) return


        when (throwable.code()) {
            InValidateData.code,
            Unauthorized.code -> {
                onAuthFail()
                val jsonObject = JSONObject(errorRawData)
                val jObject = jsonObject.optJSONObject("errors")
                if (jObject != null) {
                    val keys: Iterator<String> = jObject.keys()
                    if (keys.hasNext()) {
                        while (keys.hasNext()) {
                            val msg = StringBuilder()
                            val key: String = keys.next()
                            if (jObject.get(key) is String) {
                                msg.append("- ${jObject.get(key)}\n")
                            }
                            errorDialog(this, msg.toString(), "Alert")
                        }
                    } else {
                        errorDialog(this, jsonObject.optString("message", ""))
                    }
                }
            }
            Unauthenticated.code -> onAuthFail()
            ServerError.code -> errorDialog(this, "")
            BadRequest.code,
            NotFound.code,
            Conflict.code,
            Blocked.code,
            ForceUpdate.code -> {
                onAuthFail()
                errorDialog(this, JSONObject(errorRawData).optString("message", ""))
            }
            OK.code -> {
            }
        }
    }

    protected fun showLoader() {
        loader.run()
    }

    protected fun hideLoader() {
        loader.dismiss()
    }

    fun errorDialog(activity: Activity?, message: String, vararg title: String) {
        AppGlobal.alertDialog(activity, message, *title)
    }

    fun onAuthFail() {
        SharedPref.clearPref()
        startActivity(intentFor<SplashActivity>().clearTask().newTask())
    }
}