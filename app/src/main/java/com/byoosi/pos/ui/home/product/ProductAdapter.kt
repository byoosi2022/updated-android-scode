package com.byoosi.pos.ui.home.product

import android.text.Html
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.byoosi.pos.base.BaseAdapter
import com.byoosi.pos.model.ProductItem
import com.byoosi.pos.R
import com.byoosi.pos.utils.gone
import com.byoosi.pos.utils.visible
import kotlinx.android.synthetic.main.listitem_product.view.*

class ProductAdapter : BaseAdapter<ProductItem>(R.layout.listitem_product) {

    override fun setClickableView(itemView: View): List<View> {
        return listOf(itemView.ivAdd, itemView.ivRemove, itemView.ivExpand, itemView.tvQuantity)
    }

    override fun onBind(view: View, position: Int, item: ProductItem, payloads: MutableList<Any>) {
        view.run {
            tvTitle.text = item.item_name
            tvPrice.text = context.getString(R.string.price, item.price)
            tvQuantity.text = item.quantity.toString()
            tvDescription.text = Html.fromHtml(item.description)
            tvStock.text = context.getString(R.string.total_stock, item.stock.toInt(), item.stock_uom)
            groupExpand.visibility = if (item.isExpanded) VISIBLE else GONE
            ivExpand.setImageResource(if (item.isExpanded) R.drawable.ic_arrow_up_black else R.drawable.ic_arrow_down_black)

            val adapterStock = StockAdapter()

            adapterStock.addAll(item.other_warehouse_stock)
            Log.e("ProductAdapter", "onBind: ${item.other_warehouse_stock.size}", )
            rvStocks.apply { adapter = adapterStock }

            if(adapterStock.itemCount == 0){
                tvLabel.text  = ""
            }else{
                tvLabel.text = context.getString(R.string.other_warehouses)
            }
        }
    }
}