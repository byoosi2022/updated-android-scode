package com.byoosi.pos.ui.home.customer

import android.view.View
import com.byoosi.pos.R
import com.byoosi.pos.base.BaseAdapter
import com.byoosi.pos.model.UserItem
import kotlinx.android.synthetic.main.listitem_customer.view.*

class CustomerAdapter : BaseAdapter<UserItem>(R.layout.listitem_customer) {
    override fun setClickableView(itemView: View): List<View> {
        return listOf(itemView.tvName)
    }

    override fun onBind(view: View, position: Int, item: UserItem, payloads: MutableList<Any>) {
        view.run {
            tvName.text = item.customer_name
        }
    }
}