package com.byoosi.pos.ui.home.cart

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import com.byoosi.pos.R
import com.byoosi.pos.base.BaseFragment
import com.byoosi.pos.data.pref.SharedPref
import com.byoosi.pos.model.PaymentMode
import com.byoosi.pos.ui.invoice.InvoiceDetailActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.dialog_enter_amount.*
import kotlinx.android.synthetic.main.dialog_enter_quantity.*
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.android.synthetic.main.fragment_cart.btnContinue
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.alert

class CartFragment : BaseFragment(R.layout.fragment_cart), View.OnClickListener {
    companion object {
        const val CUSTOMER_REQUEST_CODE = 101
        const val CUSTOMER_RESULT_CODE = 102
    }

    private lateinit var adapterCart: CartAdapter
    private var customerName: String? = null
    private var priceToPay: Double = 0.0
    private lateinit var paymentMode: PaymentMode

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        setOnClick()
    }

    private fun init() {
        adapterCart = CartAdapter().also { it.addAll(SharedPref.cartItems) }
        rvCartItems.apply { adapter = adapterCart }
        onCartItemChanged()
    }

    private fun setOnClick() {
        btnContinue.setOnClickListener(this)
        adapterCart.run {
            setItemClickListener { view, i, productItem ->
                when (view.id) {
                    R.id.ivAdd -> if (productItem.quantity < productItem.stock.toInt()) {
                        productItem.quantity++
                        notifyItemChanged(i)
                        onCartItemChanged()
                    } else {
                        alert(
                                getString(R.string.product_is_out_of_stock),
                                getString(R.string.out_of_stock)
                        ) { positiveButton(getString(R.string.okay)) {} }.show()
                    }
                    R.id.ivRemove -> {
                        if (productItem.quantity > 1) {
                            productItem.quantity--
                            notifyItemChanged(i)
                        } else adapterCart.removeItemAt(i)
                        onCartItemChanged()
                    }
                    R.id.tvQuantity -> {
                        try {
                            val dialog = Dialog(requireContext())
                            dialog.window?.run {
                                requestFeature(Window.FEATURE_NO_TITLE)
                                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            }
                            dialog.run {
                                setContentView(R.layout.dialog_enter_quantity)
                                setCanceledOnTouchOutside(true)
                                setCancelable(true)
                                etQuantity.setText(productItem.quantity.toString())
                                btnSave.setOnClickListener {
                                    val quantity = etQuantity.text.toString().toInt()
                                    if (quantity < productItem.stock.toInt()) {
                                        if (quantity >= 0) {
                                            productItem.quantity = quantity
                                            notifyItemChanged(i)
                                            onCartItemChanged()
                                            dismiss()
                                        }
                                    } else {
                                        alert(
                                                getString(R.string.product_is_out_of_stock),
                                                getString(R.string.out_of_stock)
                                        ) {
                                            positiveButton(getString(R.string.okay)) { }
                                        }.show()
                                    }
                                }
                                show()
                                val params = window?.attributes
                                params?.width = LinearLayout.LayoutParams.MATCH_PARENT
                                params?.height = LinearLayout.LayoutParams.WRAP_CONTENT
                                window?.attributes = params
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    private fun onCartItemChanged() {
        SharedPref.cartItems = adapterCart.list
        if (adapterCart.itemCount > 0) {
            llBottom.visibility = View.VISIBLE
            var price = 0.0
            adapterCart.forEach { price += it.price * it.quantity }
            tvTotalPrice.text = getString(R.string.price, price)
        } else {
            llBottom.visibility = View.GONE
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnContinue -> {
                startActivityForResult(
                        Intent(requireContext(), SelectCustomerActivity::class.java),
                        CUSTOMER_REQUEST_CODE
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CUSTOMER_REQUEST_CODE && resultCode == CUSTOMER_RESULT_CODE && data != null) {
            customerName = data.getStringExtra("customer_name")
//            showPriceDialog()
            createInvoice()
        }
    }

    private fun showPriceDialog() {
        try {
            val dialog = Dialog(requireContext())
            dialog.window?.run {
                requestFeature(Window.FEATURE_NO_TITLE)
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
            dialog.setContentView(R.layout.dialog_enter_amount)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(true)
            dialog.run {
                btnContinue.setOnClickListener {
                    priceToPay = etAmount.text.toString().toDouble()
                    dismiss()
                    createInvoice()
                }
            }
            dialog.show()
            val params = dialog.window?.attributes
            params?.width = LinearLayout.LayoutParams.MATCH_PARENT
            params?.height = LinearLayout.LayoutParams.WRAP_CONTENT
            dialog.window?.attributes = params
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createInvoice() {
        callApi(true, {
            requestInterface.addInvoice(
                    mapOf(
                            "customer_name" to (customerName ?: ""),
                            "items" to Gson().toJson(adapterCart.list.map {
                                mapOf<String, Any>(
                                        "item_code" to it.item_code,
                                        "qty" to it.quantity.toDouble()
                                )
                            }),
                            "paid_amount" to priceToPay,
                            "user" to (SharedPref.login?.message?.user ?: "")
                    )
            )
        }, { response ->
            adapterCart.clearAll()
            onCartItemChanged()
            requireActivity().startActivity<InvoiceDetailActivity>("invoice_id" to response.message.name)
            findNavController().navigateUp()
        })
    }

//    private fun getModeOfPayments() {
//        callApi(true, {
//            requestInterface.getModeOfPayment()
//        }, {
//            try {
//                val dialog = Dialog(requireContext())
//                dialog.window?.run {
//                    requestFeature(Window.FEATURE_NO_TITLE)
//                    setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//                }
//                dialog.setContentView(R.layout.dialog_enter_amount)
//                dialog.setCanceledOnTouchOutside(true)
//                dialog.setCancelable(true)
//                dialog.run {
//                    val adapter = ArrayAdapter(
//                            context,
//                            android.R.layout.simple_spinner_dropdown_item,
//                            it.message
//                    )
//                    spinnerModeOfPayment.adapter = adapter
//                    btnContinue.setOnClickListener {
//                        paymentMode = adapter.getItem(spinnerModeOfPayment.selectedItemPosition)!!
//                        priceToPay = etAmount.text.toString().toDouble()
//                        dismiss()
//                        createInvoice()
//                    }
//                }
//                dialog.show()
//                val params = dialog.window?.attributes
//                params?.width = LinearLayout.LayoutParams.MATCH_PARENT
//                params?.height = LinearLayout.LayoutParams.WRAP_CONTENT
//                dialog.window?.attributes = params
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        })
//    }
}

