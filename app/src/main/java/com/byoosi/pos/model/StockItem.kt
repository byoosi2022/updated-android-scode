package com.byoosi.pos.model

import java.io.Serializable

data class StockItem(
    val warehouse_name: String = "",
    val stock: Double = 0.0
) : Serializable

// ProductItem.kt

data class ProductItems(
    val name: String,
    val price: String,
    val stock: Int,
    val store: String,
    val imageResId: Int
)


