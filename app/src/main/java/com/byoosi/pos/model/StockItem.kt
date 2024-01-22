package com.byoosi.pos.model

import java.io.Serializable

data class StockItem(
    val warehouse_name: String = "",
    val stock: Double = 0.0
) : Serializable
