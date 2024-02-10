package com.byoosi.pos.ui.home.customer

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import androidx.core.util.PatternsCompat.EMAIL_ADDRESS
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.byoosi.pos.base.BaseFragment
import com.byoosi.pos.model.UserItem
import com.byoosi.pos.R
import kotlinx.android.synthetic.main.dialog_add_customer.*
import kotlinx.android.synthetic.main.dialog_customer_detail.*
import kotlinx.android.synthetic.main.fragment_customers.*

/**
 * Created by pintusingh on 24/12/20.
 */

class CustomerFragment : BaseFragment(R.layout.fragment_customers), View.OnClickListener {
    private lateinit var adapterCustomer: CustomerAdapter
    private var isLoading = false;

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                R.id.tvName -> showUserDetailDialog(userItem)
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

    override fun onClick(view: View) {
        when (view.id) {
            R.id.fabAddCustomer -> showAddCustomerDialog()
            R.id.ivSearch -> getCustomers(0)
        }
    }

    fun getCustomers(offset: Int) {
        callApi(offset == 0, {
            requestInterface.getCustomers(
                mapOf<String, Any>(
                    "offset" to offset,
                    "limit" to 10,
                    "search" to etSearch.text.toString()
                )
            )
        }, { response ->
            response.message.let {
                adapterCustomer.run {
                    if (offset == 0) addAll(it) else appendAll(it)
                }
            }
            isLoading = false
        })
    }

    private fun showAddCustomerDialog() {
        try {
            val dialog = Dialog(requireContext())
            dialog.window?.run {
                requestFeature(Window.FEATURE_NO_TITLE)
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
            dialog.setContentView(R.layout.dialog_add_customer)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(true)
            dialog.run {
                btnAddCustomer.setOnClickListener {
                    when {
                        etName.text.isEmpty() -> etName.error =
                            getString(R.string.please_enter_name)
                        etMobileNo.text.isEmpty() -> etMobileNo.error =
                            getString(R.string.please_enter_mobile_number)
                        etMobileNo.text.length < 7 -> etMobileNo.error =
                            getString(R.string.please_enter_valid_mobile_number)
                        else -> {
                            val customerName = etName.text.toString()
                            val mobileNo = etMobileNo.text.toString()
                            val emailId = etEmail.text.toString()

                            callApi(true, {
                                val customerData = mapOf<String, Any>(
                                    "customer_name" to customerName,
                                    "mobile_no" to mobileNo,
                                    "email_id" to emailId
                                )
                                requestInterface.addCustomers(customerData)
                                // Log the details
                                println("Added customer: $customerData")
//                                Log.d("CustomerData", customerData.toString())
                            }, {
                                dismiss()
                                getCustomers(0)
                            }, { dismiss() })
                        }
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


    private fun showUserDetailDialog(userItem: UserItem) {
        try {
            val dialog = Dialog(requireContext())
            dialog.window?.run {
                requestFeature(Window.FEATURE_NO_TITLE)
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
            dialog.setContentView(R.layout.dialog_customer_detail)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(true)
            dialog.run {
                tvName.text = userItem.customer_name
                tvEmail.text = if (userItem.email_id.isNullOrEmpty()) "-" else userItem.email_id
                tvMobile.text = if (userItem.mobile_no.isNullOrEmpty()) "-" else userItem.mobile_no
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