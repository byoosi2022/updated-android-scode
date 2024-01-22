package com.byoosi.pos.ui.home.cart

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import androidx.core.util.PatternsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.byoosi.pos.R
import com.byoosi.pos.base.BaseActivity
import com.byoosi.pos.ui.home.cart.CartFragment.Companion.CUSTOMER_RESULT_CODE
import com.byoosi.pos.ui.home.customer.CustomerAdapter
import kotlinx.android.synthetic.main.dialog_add_customer.*
import kotlinx.android.synthetic.main.fragment_customers.*

class SelectCustomerActivity : BaseActivity(R.layout.fragment_customers), View.OnClickListener {
    private lateinit var adapterCustomer: CustomerAdapter
    private var isLoading = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setOnClick()
    }

    private fun init() {
        adapterCustomer = CustomerAdapter().also {
            getCustomers(0)
            isLoading = true
        }
        rvCustomers.apply { adapter = adapterCustomer }
    }

    private fun setOnClick() {
        fabAddCustomer.setOnClickListener(this)
        ivSearch.setOnClickListener(this)
        etSearch.setOnEditorActionListener { v, actionId, event ->
            getCustomers(0)
            true
        }
        adapterCustomer.setItemClickListener { view, i, userItem ->
            when (view.id) {
                R.id.tvName -> {
                    val result = Intent().putExtra("customer_name", userItem.name)
                    setResult(CUSTOMER_RESULT_CODE, result)
                    finish()
                }
            }
        }
        rvCustomers.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!isLoading &&
                    (rvCustomers.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition() == adapterCustomer.itemCount - 1
                ) {
                    getCustomers(adapterCustomer.itemCount)
                    isLoading = true
                }
            }
        })
    }

    fun getCustomers(offset: Int) {
        callApi(offset == 0, {
            requestInterface.getCustomers(mapOf<String, Any>("offset" to offset, "limit" to 10, "search" to etSearch.text.toString()))
        }, { response ->
            response.message.let { if (offset == 0) adapterCustomer.addAll(it) else adapterCustomer.appendAll(it) }
            isLoading = false
        })
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.fabAddCustomer -> showAddCustomerDialog()
            R.id.ivSearch -> getCustomers(0)
        }
    }

    private fun showAddCustomerDialog() {
        try {
            val dialog = Dialog(applicationContext)
            dialog.window?.run {
                requestFeature(Window.FEATURE_NO_TITLE)
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
            dialog.setContentView(R.layout.dialog_add_customer)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(true)
            dialog.run {
                btnAddCustomer.setOnClickListener {
                    if (etName.text.isEmpty()) {
                        etName.error = getString(R.string.please_enter_name)
                    } else if (etEmail.text.isEmpty()) {
                        etEmail.error = getString(R.string.please_enter_email_address)
                    } else if (!PatternsCompat.EMAIL_ADDRESS.matcher(etEmail.text.toString()).matches()) {
                        etEmail.error = getString(R.string.please_enter_valid_email_address)
                    } else if (etMobileNo.text.isEmpty()) {
                        etMobileNo.error = getString(R.string.please_enter_mobile_number)
                    } else if (etMobileNo.text.length < 7) {
                        etMobileNo.error = getString(R.string.please_enter_valid_mobile_number)
                    } else {
                        callApi(true, {
                            requestInterface.addCustomers(
                                mapOf<String, Any>(
                                    "customer_name" to etName.text.toString(),
                                    "mobile_no" to etMobileNo.text.toString(),
                                    "email_id" to etEmail.text.toString()
                                )
                            )
                        }, {
                            dismiss()
                            adapterCustomer.clearAll()
                            getCustomers(0)
                        }, {
                            dismiss()
                        })
                    }
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


}

