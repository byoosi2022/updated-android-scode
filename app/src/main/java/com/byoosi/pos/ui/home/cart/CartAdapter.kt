package com.byoosi.pos.ui.home.cart

import android.text.Html
import android.view.View
import com.byoosi.pos.R
import com.byoosi.pos.base.BaseAdapter
import com.byoosi.pos.model.ProductItem
import kotlinx.android.synthetic.main.listitem_cart.view.*

class CartAdapter : BaseAdapter<ProductItem>(R.layout.listitem_cart) {
    override fun setClickableView(itemView: View): List<View> {
        return listOf(itemView.ivAdd, itemView.ivRemove, itemView.tvQuantity)
    }

    override fun onBind(view: View, position: Int, item: ProductItem, payloads: MutableList<Any>) {
        view.run {
            tvTitle.text = item.item_name
            tvSubTitle.text = Html.fromHtml(item.description)
            tvPrice.text = context.getString(R.string.price, item.price)
            tvTotalPrice.text = context.getString(R.string.price, item.price * item.quantity)
            tvQuantity.text = item.quantity.toString()
        }
    }

}