package com.byoosi.pos.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by DK on 14/9/19.
 */
abstract class BaseAdapter<T>(@LayoutRes var layout: Int) : RecyclerView.Adapter<BaseAdapter<T>.ViewHolder>() {

    val list = ArrayList<T>()
    private var mOnLayoutSelector: OnLayoutSelector? = null
    private lateinit var mRecyclerView: RecyclerView
    private var emptyView: View? = null
    private var onClickView: ((View, Int, T) -> Unit)? = null
    private var onCreateViewHolderBlock: ((View) -> View)? = null

    fun setOnLayoutSelector(mOnLayoutSelector: OnLayoutSelector) {
        this.mOnLayoutSelector = mOnLayoutSelector
    }

    fun setItemClickListener(onClickView: (View, Int, T) -> Unit) {
        this.onClickView = onClickView
    }

    fun setOnCreateViewHolderBlock(block: ((View) -> View)) {
        this.onCreateViewHolderBlock = block
    }

    interface OnLayoutSelector {
        fun selectLayout(itemViewType: Int): Int?
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (mOnLayoutSelector != null) {
            if (mOnLayoutSelector?.selectLayout(viewType) != null) {
                layout = mOnLayoutSelector?.selectLayout(viewType)!!
            }
        }
        var v = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        onCreateViewHolderBlock?.let { it -> v = it(v) }
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            onBind(holder.getBindView(), position, list[position], payloads)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        onBind(holder.getBindView(), position, list[position])
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            setClickableView(itemView).forEach { clickView ->
                clickView.setOnClickListener { onClickView?.let { it1 -> it1(it, adapterPosition, list[adapterPosition]) } }
            }
        }

        fun getBindView(): View = itemView
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    open fun addItemAt(index: Int, item: T) {
        list.add(index, item)
        notifyItemInserted(index)
    }

    open fun addItem(item: T) {
        list.add(item)
        notifyItemInserted(list.size)
    }

    open fun appendAll(dataList: Collection<T>) {
        list.addAll(dataList)
        notifyDataSetChanged()
    }

    open fun addAll(dataList: Collection<T>) {
        list.clear()
        list.addAll(dataList)
        notifyDataSetChanged()
        if (::mRecyclerView.isInitialized) mRecyclerView.checkIfEmpty(emptyView)
    }

    open fun forEach(function: (T) -> Unit) {
        list.forEach(function)
    }

    open fun clearAll() {
        list.clear()
        notifyDataSetChanged()
    }

    open fun removeItemAt(position: Int) {
        if (0 <= position && position < list.size) {
            list.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun setEmptyView(emptyView: View?) {
        this.emptyView = emptyView
    }

    abstract fun setClickableView(itemView: View): List<View>

    abstract fun onBind(view: View, position: Int, item: T, payloads: MutableList<Any> = mutableListOf())

    open fun selectMyLayout(itemViewType: Int): Int? = null

    private fun RecyclerView.checkIfEmpty(emptyView: View?) {
        if (emptyView != null && adapter != null) {
            val emptyViewVisible = adapter?.itemCount == 0
            emptyView.visibility = if (emptyViewVisible) View.VISIBLE else View.GONE
            visibility = if (emptyViewVisible) View.GONE else View.VISIBLE
        }
    }
}