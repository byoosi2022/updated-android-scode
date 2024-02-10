package com.byoosi.pos.ui.invoice

import android.util.Log
import android.view.View
import com.byoosi.pos.base.BaseAdapter
import com.byoosi.pos.model.InvoiceProduct
import com.byoosi.pos.R
import com.byoosi.pos.data.pref.SharedPref
import com.byoosi.pos.utils.AppSignatureHelper.TAG
import kotlinx.android.synthetic.main.listitem_invoice_item.view.*

class InvoiceItemAdapter : BaseAdapter<InvoiceProduct>(R.layout.listitem_invoice_item) {

    override fun setClickableView(itemView: View): List<View> {
        return listOf()
    }

    override fun onBind(view: View, position: Int, item: InvoiceProduct, payloads: MutableList<Any>) {
        view.run {
            tvItemName.text = item.item_name
            tvItemPrice.text = context.getString(R.string.quantity_x_price, item.qty.toInt(), item.rate)
//            Log.d("API_LOG key", "SharedPref data after getProducts: ${SharedPref.login}")
        }
    }
}