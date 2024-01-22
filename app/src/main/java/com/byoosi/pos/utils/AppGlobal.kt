package com.byoosi.pos.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.view.ContextThemeWrapper
import com.byoosi.pos.MyApp
import com.byoosi.pos.R
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by DK on 03-10-2019.
 */

object AppGlobal {
    private const val TAG = "AppGlobal"

    val SERVER_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"
    val DATE_TIME_FORMAT = "EEE dd MMM, yyyy 'at' hh:mm a"
    val DATE_FORMAT = "EEE dd MMM, yyyy"
    val TIME_FORMAT = "hh:mm a"
    val DECIMAL_FORMAT = "##.##"

    val decimalFormat = DecimalFormat(DECIMAL_FORMAT)

    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    fun getFractionValue(value: Double): String {
        return decimalFormat.format(value)
    }

    fun setDialogTheme(activity: Activity): Context {
        return ContextThemeWrapper(activity, android.R.style.Theme_Light_NoTitleBar)
    }

    fun hideKeyboard(view: View) {
        val windowToken = view.rootView?.windowToken
        windowToken?.let {
            val imm = MyApp.getInstance().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(windowToken, 0)
        }
    }

    fun networkAlertDialog(context: Context?) {
        context?.alert(
            context.resources.getString(R.string.msg_no_internet),
            context.getString(R.string.app_name)
        ) { okButton { } }?.show()
    }

    fun alertDialog(context: Context?, message: String, vararg title: String) {
        if (context == null) return
        if (message.isEmpty()) return
        val displayTitle =
            if (title.isEmpty()) context.getString(R.string.app_name)
            else title[0]
        context.alert(message, displayTitle) { okButton { it.dismiss() } }.show()
    }

    fun openSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", context.packageName, null)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun utcToLocal(utcDateString: String?, outFormat: String): String {
        if (utcDateString.isNullOrEmpty()) return ""
        var systemDateString = ""
        try {
            val formatter = SimpleDateFormat(SERVER_DATE_TIME_FORMAT, Locale.ENGLISH)
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            val utcDate = formatter.parse(utcDateString)

            val dateFormatter =
                SimpleDateFormat(outFormat, Locale.getDefault()) //this format changeable
            dateFormatter.timeZone = TimeZone.getDefault()
            systemDateString = dateFormatter.format(utcDate)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return systemDateString
    }

    fun parseTimestamp(dateString: String, dateFormat: String = SERVER_DATE_TIME_FORMAT): Long {
        return try {
            SimpleDateFormat(dateFormat, Locale.US).parse(dateString)?.time ?: -1
        } catch (e: ParseException) {
            return -1
        }
    }
}