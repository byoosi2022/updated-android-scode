package com.byoosi.pos.ui.home.invoice

import android.view.View
import com.byoosi.pos.base.BaseAdapter
import com.byoosi.pos.model.InvoiceItem
import com.byoosi.pos.R
import kotlinx.android.synthetic.main.listitem_invoice.view.*


class InvoiceAdapter : BaseAdapter<InvoiceItem>(R.layout.listitem_invoice) {

    override fun setClickableView(itemView: View): List<View> {
        return listOf(itemView)
    }

    override fun onBind(view: View, position: Int, item: InvoiceItem, payloads: MutableList<Any>) {
        view.run {
            tvName.text = item.customer_name
            tvTotalPrice.text = context.getString(R.string.price, item.grand_total)
            tvStatus.text = item.status ?: context.getString(R.string.unknown_status)
        }
    }
}

