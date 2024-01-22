package com.byoosi.pos.base

/**
 * Created by DK on 24/9/19.
 */
interface OnItemClickListener<T> {
    fun onItemClick(position: Int, item: T)
}
