package com.byoosi.pos.model

data class SalesPaymentSummaryResponse(
    val message:SalesPaymentSummary
) {
    val end_date: String = ""
    val start_date: String = ""
    val total_payments: Double = 0.0
    val total_invoices: Double = 0.0
    val user: String = ""

}

data class SalesPaymentSummary(
    val user: String,
    val start_date: String,
    val end_date: String,
    val total_invoices: Double,
    val total_payments: Double
)
