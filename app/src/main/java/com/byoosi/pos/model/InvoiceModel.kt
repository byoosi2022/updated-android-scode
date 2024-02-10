package com.byoosi.pos.model

import java.io.Serializable


/**
 * Created by pintusingh on 26/12/20.
 */

data class PaymentMode(val name: String)
data class TokenApi(val message: String)




data class InvoiceItem(
    val name: String,
    val customer: String,
    val customer_name: String,
    val grand_total: Double = 0.0,
    val paid_amount: Double = 0.0,
    val outstanding_amount: Double = 0.0,
    val docstatus: Int = 0,
    val status: String? = null
) : Serializable

class InvoiceResponse(val message: InvoiceDetail) : Serializable
class InvoiceContentResponse(val message: String) : Serializable

class InvoiceDetail(
    val name: String,
    val customer: String?,
    val customer_name: String?,
    val contact_mobile: String?,
    val contact_email: String?,
    val total_qty: Double,
    var total: Double,
    val grand_total: Double,
    var outstanding_amount: Double,
    var paid_amount: Double,
    val items: List<InvoiceProduct>,
    val payments: List<InvoicePayment>
) : Serializable

class InvoiceProduct(
    val name: String?,
    val item_code: String,
    val item_name: String?,
    val description: String?,
    val stock_uom: String,
    var qty: Double,
    var amount: Double,
    var rate: Double
) : Serializable

class InvoicePayment(
    val name: String?,
    val mode_of_payment: String,
    val amount: Double,
) : Serializable

class RegisterRequest {
    var email: String? = null
    var full_name: String? = null
    var first_name: String? = null
    var new_password: String? = null
    lateinit var roles: Array<Role>

    class Role {
        var role: String? = null
    }
}