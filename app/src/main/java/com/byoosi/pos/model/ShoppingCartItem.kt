package com.byoosi.pos.model

import com.google.gson.annotations.SerializedName

data class YourResponseType(
    val status: String,
    val message: String,
    val data: AddToCartResponseData // AddToCartResponseData is another data class representing the nested data structure
)
data class AddToCartResponseData(
    val item_code: String,
    val quantity: Int,
    val price: Double
)



data class AddToCartRequest(
    val item_code: String,
    val quantity: Int,
    val price: Double
)




