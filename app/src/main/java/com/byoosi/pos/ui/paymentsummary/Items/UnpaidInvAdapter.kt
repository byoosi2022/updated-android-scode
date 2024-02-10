package com.byoosi.pos.ui.paymentsummary.Items

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.byoosi.pos.R
import com.byoosi.pos.model.CustomModel.UpaidInvoice

class UnpaidInvAdapter(private val context: Context, private val invoices: MutableList<UpaidInvoice>) :
    RecyclerView.Adapter<UnpaidInvAdapter.ViewHolder>() {

    private var itemClickListener: ((View, Int, UpaidInvoice) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.listitem_invoice, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val invoice = invoices[position]
        holder.bind(invoice)
        holder.itemView.setOnClickListener {
            itemClickListener?.invoke(it, position, invoice)
        }
    }

    override fun getItemCount(): Int {
        return invoices.size
    }

    fun setItems(newItems: List<UpaidInvoice>) {
        invoices.clear()
        invoices.addAll(newItems)
        notifyDataSetChanged()
    }

    fun addItems(newItems: List<UpaidInvoice>) {
        val startPos = invoices.size
        invoices.addAll(newItems)
        notifyItemRangeInserted(startPos, newItems.size)
    }

    fun setItemClickListener(listener: (View, Int, UpaidInvoice) -> Unit) {
        itemClickListener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvTotalPrice: TextView = itemView.findViewById(R.id.tvTotalPrice)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)

        fun bind(invoice: UpaidInvoice) {
            tvName.text = invoice.customerName
            tvTotalPrice.text = invoice.grandTotal.toString()
            tvStatus.text = invoice.status
        }
    }
}




