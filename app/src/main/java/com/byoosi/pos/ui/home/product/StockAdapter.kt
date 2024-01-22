package com.byoosi.pos.ui.home.product

import android.text.Html
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.byoosi.pos.base.BaseAdapter
import com.byoosi.pos.model.ProductItem
import com.byoosi.pos.R
import com.byoosi.pos.model.StockItem
import com.byoosi.pos.utils.gone
import com.byoosi.pos.utils.visible
import kotlinx.android.synthetic.main.listitem_stock_detail.view.*

class StockAdapter : BaseAdapter<StockItem>(R.layout.listitem_stock_detail) {

    override fun setClickableView(itemView: View): List<View> {
        return listOf()
    }

    override fun onBind(view: View, position: Int, item: StockItem, payloads: MutableList<Any>) {
        view.run {
            tvName.text = item.warehouse_name
            tvStock.text =
                context.getString(R.string.total_stock_inner, item.stock.toInt())

            if (position == itemCount-1) {
                separator.gone()
            }else{
                separator.visible()
            }
        }
    }
}