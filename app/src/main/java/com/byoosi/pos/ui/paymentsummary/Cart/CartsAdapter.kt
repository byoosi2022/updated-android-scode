package com.byoosi.pos.ui.paymentsummary.Cart
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.byoosi.pos.R
import com.byoosi.pos.base.BaseAdapter
import com.byoosi.pos.model.ProductItem
import kotlinx.android.synthetic.main.listitem_cart.view.*

class CartsAdapter() : BaseAdapter<ProductItem>(R.layout.listitem_cart) {

    override fun setClickableView(itemView: View): List<View> {
        return listOf(itemView.ivAdd, itemView.ivRemove, itemView.tvQuantity)
    }

    override fun onBind(view: View, position: Int, item: ProductItem, payloads: MutableList<Any>) {
        view.run {
            tvTitle.text = item.item_code
            tvPrice.text = context.getString(R.string.price, item.price)
            tvTotalPrice.text = context.getString(R.string.price, item.price * item.quantity)
            tvQuantity.text = item.quantity.toString()

            ivAdd.setOnClickListener { onAddClicked(item) }
            ivRemove.setOnClickListener { onRemoveClicked(item) }
        }
    }

    private fun onRemoveClicked(item: ProductItem) {
        TODO("Not yet implemented")
    }

    private fun onAddClicked(item: ProductItem) {
        TODO("Not yet implemented")
    }


}
