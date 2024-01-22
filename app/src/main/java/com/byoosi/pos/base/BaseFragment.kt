package com.byoosi.pos.base

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

import com.byoosi.pos.data.network.RequestInterface
import com.byoosi.pos.data.network.ResponseCode.*
import com.byoosi.pos.data.pref.SharedPref
import com.byoosi.pos.model.CommonResponse
import com.byoosi.pos.ui.splash.SplashActivity
import com.byoosi.pos.utils.AppGlobal
import com.byoosi.pos.utils.MyAppProgressDialog
import com.byoosi.pos.MyApp
import kotlinx.coroutines.*
import org.jetbrains.anko.startActivity
import org.json.JSONObject
import retrofit2.HttpException

/**
 * Created by DK on 19/9/19.
 */
abstract class BaseFragment(@LayoutRes val layoutId: Int) : Fragment() {
    val TAG: String = this.javaClass.simpleName
    private var mActivity: BaseActivity? = null

    val requestInterface by lazy { RequestInterface.getInstance() }

    protected var listJobs = arrayListOf<Job>()
    private var noOfApiCall = 0
    private var onFailure: OnFailure? = null
    lateinit var loader: MyAppProgressDialog
    private var mContext: Context = MyApp.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
        loader = MyAppProgressDialog(requireActivity())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(layoutId, container, false)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity) {
            val activity = context as BaseActivity?
            this.mActivity = activity
        }
    }

    override fun onDetach() {
        super.onDetach()
        if (context is BaseActivity) this.mActivity = context as BaseActivity?
    }

    fun getBaseActivity(): BaseActivity? = mActivity


    override fun onDestroy() {
        listJobs.forEach { it.cancel() }
        super.onDestroy()
    }

    fun <T> callApi(
        doShowLoader: Boolean = false,
        apiBlock: suspend () -> T,
        successBlock: (T) -> Unit = {},
        errorBlock: (Throwable) -> Unit = {}
    ) {
        if (!AppGlobal.isNetworkConnected(mContext))
            return AppGlobal.networkAlertDialog(mContext)
        if (doShowLoader)
            if (++noOfApiCall == 1) showLoader()
        val job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = apiBlock()
                withContext(Dispatchers.Main) {
                    onResponse()
                    successBlock(result)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResponse()
                    errorBlock(e)
                    onResponseFailure(e)
                }
            }
        }
        listJobs.add(job)
    }

    protected fun onResponseFailure(throwable: Throwable, vararg doHideLoader: Boolean) {
        if (doHideLoader.isNotEmpty() && doHideLoader[0])
            onResponse()
        Log.e(throwable.toString(), throwable.message.orEmpty())
        if (throwable is HttpException) {
            Log.e(TAG, "response code ${throwable.code()}")
            errorHandling(throwable)
        }
    }

    private fun errorHandling(throwable: HttpException) {
        val errorRawData = throwable.response()?.errorBody()?.string()?.trim()
        if (errorRawData.isNullOrEmpty()) return

        when (throwable.code()) {
            BadRequest.code -> {
                errorDialog(activity, JSONObject(errorRawData).optString("message", ""))
            }
            InValidateData.code -> {
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
                            errorDialog(activity, msg.toString(), "Alert")
                        }
                    } else {
                        errorDialog(activity, jsonObject.optString("message", ""))
                    }
                }
            }
            Unauthenticated.code -> {
                onAuthFail()
                onFailure?.onFailure(throwable.code())
            }
            OK.code -> {
            }
            ServerError.code -> Log.e(TAG, ServerError.toString())
            Unauthorized.code -> {
                onAuthFail()
                errorDialog(activity, JSONObject(errorRawData).optString("message", ""))
            }
            NotFound.code -> {
                errorDialog(activity, JSONObject(errorRawData).optString("message", ""))
            }
            Conflict.code -> {
                errorDialog(activity, JSONObject(errorRawData).optString("message", ""))
            }
            Blocked.code -> {
                errorDialog(activity, JSONObject(errorRawData).optString("message", ""))
            }
            ForceUpdate.code -> {
                errorDialog(activity, JSONObject(errorRawData).optString("message", ""))
            }
        }
    }

    protected fun <T> onStatusFalse(t: T, vararg doSawLoader: Boolean): Boolean {
        if (doSawLoader.isNotEmpty() && doSawLoader[0]) {
            noOfApiCall--
            if (noOfApiCall <= 0) {
                noOfApiCall = 0
                hideLoader()
            }
        }
        if (t is CommonResponse<*>) {
            AppGlobal.alertDialog(mActivity, t.message.toString())
            return true
        }
        return false
    }

    protected fun <T> onStatusFalseNoMessage(t: T, vararg doSawLoader: Boolean): Boolean {
        if (doSawLoader.isNotEmpty() && doSawLoader[0]) {
            noOfApiCall--
            if (noOfApiCall <= 0) {
                noOfApiCall = 0
                hideLoader()
            }
        }
        if (t is CommonResponse<*>) {
            return true
        }
        return false
    }

    fun setOnFail(onFailure: OnFailure) {
        this.onFailure = onFailure
    }

    fun setLayoutParams(dialog: Dialog) {
        val lWindowParams = WindowManager.LayoutParams()
        lWindowParams.copyFrom(dialog.window!!.attributes)
        lWindowParams.width =
            WindowManager.LayoutParams.MATCH_PARENT // this is where the magic happens
        lWindowParams.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window?.attributes = lWindowParams
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

    private fun onResponse() {
        noOfApiCall--
        if (noOfApiCall <= 0) {
            noOfApiCall = 0
            hideLoader()
        }
    }

    fun onAuthFail() {
        SharedPref.isLogin = false
        activity?.startActivity<SplashActivity>()
        activity?.finishAffinity()
    }

    interface OnFailure {
        fun onFailure(responseCode: Int)
    }

    interface CallbackFragment<T> {
        fun onFragmentAttached(TAG: String, fragment: Fragment)
        fun onFragmentDetached()
    }
}