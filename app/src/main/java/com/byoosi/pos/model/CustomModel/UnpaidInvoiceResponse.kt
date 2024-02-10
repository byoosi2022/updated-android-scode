package com.byoosi.pos.model.CustomModel

import com.google.gson.annotations.SerializedName


data class UpaidInvoice(
    @SerializedName("name") val name: String,
    @SerializedName("grand_total") val grandTotal: Double,
    @SerializedName("paid_amount") val paidAmount: String,
    @SerializedName("outstanding_amount") val outstandingAmount: Double,
    @SerializedName("docstatus") val docstatus: Int,
    @SerializedName("status") val status: String,
    @SerializedName("customer") val customer: String,
    @SerializedName("customer_name") val customerName: String
)


data class ApiResponse(
    @SerializedName("message") val message: List<UpaidInvoice>
)


