package com.byoosi.pos.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import org.jetbrains.anko.browse


/**
 * Created by DK on 03-10-2019.
 */

fun <T> Context.isServiceRunning(service: Class<T>): Boolean {
    return (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
        .getRunningServices(Integer.MAX_VALUE)
        .any { it.service.className == service.name }
}

fun AppCompatActivity.hideKeyboard() {
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(this)
    }
    hideKeyboard(view)
}

fun Context.hideKeyboard(view: View) {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
    view.clearFocus()
}

fun Context.openBrowser(url: String?) {
    if (url.isNullOrEmpty()) return

    /*val builder=CustomTabsIntent.Builder().build()
    builder.launchUrl(this, Uri.parse(url))*/

    browse(url)
}

fun Context.getDeviceHeight(): Int = resources.displayMetrics.heightPixels

fun Context.getDeviceWidth(): Int = resources.displayMetrics.widthPixels

fun Context.openAppInPlay() {
    try {
        val uri = Uri.parse("market://details?id=$packageName")
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    } catch (anfe: ActivityNotFoundException) {
        val uri = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}

fun EditText.isValidPhoneNumber(): Boolean {
    return when {
        TextUtils.isEmpty(text) -> false
        Patterns.PHONE.matcher(text).matches() -> text.length == 9/*{
            if (Locale.getDefault().country.equals("in", ignoreCase = true)) {
                text.length == 10
            } else {
                text.length == 9
            }
        }*/
        else -> false
    }
}

fun EditText.isValidEmail(): Boolean {
    return when {
        TextUtils.isEmpty(text) -> false
        Patterns.EMAIL_ADDRESS.matcher(text).matches() -> true
        else -> false
    }
}

fun ImageView.loadImage(img: String?, placeHolder: Int) {

}

fun TextView.htmlText(msg: String?) {
    text = HtmlCompat.fromHtml(msg.orEmpty(), HtmlCompat.FROM_HTML_MODE_COMPACT)
}


fun Int?.orDefault(value: Int = 0) = this ?: value
fun Long?.orDefault(value: Long = 0L) = this ?: value
fun Float?.orDefault(value: Float = 0f) = this ?: value
fun Double?.orDefault(value: Double = 0.0) = this ?: value

fun View.gone() {
    if (visibility != View.GONE) visibility = View.GONE
}

fun View.visible() {
    if (visibility != View.VISIBLE) visibility = View.VISIBLE
}

fun View.invisible() {
    if (visibility != View.INVISIBLE) visibility = View.INVISIBLE
}