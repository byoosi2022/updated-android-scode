package com.byoosi.pos.model

data class PaymentResponse(
    val message: PaymentDetails
)

data class PaymentDetails(
    val posting_date: String,
    val party_type: String,
    val paid_to: String,
    val party: String,
    val paid_amount: String,
    val received_amount: String,
    val mode_of_payment: String,
    val remarks: String
)
