package com.byoosi.pos.utils

import android.app.Activity
import android.app.Dialog
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.byoosi.pos.R


/**
 * Created by DK on 03-10-2019.
 */
class MyAppProgressDialog(val activity: Activity?) : Thread() {

    private var dialog: Dialog? = Dialog(AppGlobal.setDialogTheme(this.activity!!))
    var view: View? = null

    init {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            dialog?.window?.statusBarColor =
                ContextCompat.getColor(this.activity!!, R.color.transparent)
            dialog?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
        view = activity?.layoutInflater?.inflate(R.layout.dialog_loader, null)

        dialog?.setContentView(view!!)

        setCanceledOnTouchOutside(false)
        setCancelable(true)
    }

    override fun run() {
        super.run()
        try {
            activity?.runOnUiThread {
                if (!activity.isFinishing) {
                    dialog?.show()
                }
            }
        } catch (e: WindowManager.BadTokenException) {
        }
    }

    fun dismiss() {
        dialog?.dismiss()
        this.interrupt()
    }

    fun setCancelable(flag: Boolean) {
        dialog?.setCancelable(flag)
    }

    fun setCanceledOnTouchOutside(flag: Boolean) {
        dialog?.setCanceledOnTouchOutside(flag)
    }
}