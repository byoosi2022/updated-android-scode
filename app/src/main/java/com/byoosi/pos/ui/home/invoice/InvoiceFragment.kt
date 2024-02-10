package com.byoosi.pos.ui.home.invoice

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.byoosi.pos.base.BaseFragment
import com.byoosi.pos.ui.invoice.InvoiceDetailActivity
import com.byoosi.pos.R
import com.byoosi.pos.data.network.RequestInterface
import com.byoosi.pos.data.pref.SharedPref
import kotlinx.android.synthetic.main.fragment_invoice.*
import kotlinx.android.synthetic.main.fragment_product.etSearch
import kotlinx.android.synthetic.main.fragment_product.ivSearch
import org.jetbrains.anko.startActivity
import com.byoosi.pos.data.pref.SharedPref.clear
import kotlinx.coroutines.launch

/**
 * Created by pintusingh on 24/12/20.
 */




class InvoiceFragment : BaseFragment(R.layout.fragment_invoice) {
    private lateinit var adapterInvoice: InvoiceAdapter
    private var isLoading = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        setOnClick()
    }

    private fun init() {
        adapterInvoice = InvoiceAdapter().also { getInvoices(0) }
        rvInvoice.adapter = adapterInvoice
        rvInvoice.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!isLoading &&
                    (rvInvoice.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition() == adapterInvoice.itemCount - 1
                ) {
                    getInvoices(adapterInvoice.itemCount)
                    isLoading = true
                }
            }
        })
    }

    private fun setOnClick() {
        ivSearch.setOnClickListener {
            getInvoices(0)
            Log.i(TAG, "api key ${SharedPref.apiKey}")
//            Log.d("API_LOG-invoices", "SharedPref data after getProducts: ${SharedPref.login}")
        }
        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                getInvoices(0)
                true
            } else {
                false
            }
        }
        adapterInvoice.setItemClickListener { view, _, invoiceItem ->
            when (view.id) {
                R.id.llInvoice -> requireActivity().startActivity<InvoiceDetailActivity>("invoice_id" to invoiceItem.name)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getInvoices(0)
    }

    private fun getInvoices(offset: Int) {
        val apiService = RequestInterface.getInstance() // Use getInstance() method to get the Retrofit instance

        val headers = HashMap<String, String>()
        headers["Authorization"] = "token ${SharedPref.apiKey}:${SharedPref.apiSecret}"

        // Use lifecycleScope to launch a coroutine
        lifecycleScope.launch {
            try {
                val response = apiService.getInvoiceList(headers, mapOf(
                    "offset" to offset,
                    "search" to etSearch.text.toString(),
                    "limit" to 10
                ))
                Log.d("API_LOG-invoices", "SharedPref data after getProducts: ${SharedPref.login}")
                adapterInvoice.run {
                    if (offset == 0) {
                        clear()
                        addAll(response.message)
                    } else {
                        appendAll(response.message)
                    }
                }
                isLoading = false
            } catch (e: Exception) {
                // Handle error
                Log.e("API_LOG", "Error fetching invoices: ${e.message}", e)
                // Handle error state, such as showing a toast or retrying the operation
            }
        }
    }




}

