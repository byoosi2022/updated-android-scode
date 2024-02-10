// UnpaidInvFragment.kt
package com.byoosi.pos.ui.paymentsummary.Items

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.byoosi.pos.R
import com.byoosi.pos.data.network.RequestInterface
import com.byoosi.pos.model.CustomModel.ApiResponse
import com.byoosi.pos.ui.invoice.InvoiceDetailActivity
import kotlinx.android.synthetic.main.fragment_product.etSearch
import kotlinx.android.synthetic.main.fragment_product.ivSearch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UnpaidInvFragment : Fragment() {

        private lateinit var recyclerView: RecyclerView
        private lateinit var adapter: UnpaidInvAdapter
        private val requestInterface = RequestInterface.create()
        private var isLoading = false

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
                val view = inflater.inflate(R.layout.fragment_invoice, container, false)
                recyclerView = view.findViewById(R.id.rvInvoice)
                return view
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                super.onViewCreated(view, savedInstanceState)
                setupRecyclerView()
                setOnClick()
                fetchUnpaidInvoices(0)
        }

        private fun setupRecyclerView() {
                adapter = UnpaidInvAdapter(requireContext(), mutableListOf())
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                recyclerView.adapter = adapter
        }

        private fun setOnClick() {

                ivSearch.setOnClickListener {
                        fetchUnpaidInvoices(0)
                }
                etSearch.setOnEditorActionListener { _, actionId, _ ->
                        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                                fetchUnpaidInvoices(0)
                                true
                        } else {
                                false
                        }
                }
                adapter.setItemClickListener { _, _, invoiceItem ->
                        val intent = Intent(requireActivity(), InvoiceDetailActivity::class.java).apply {
                                putExtra("invoice_id", invoiceItem.name)
                        }
                        startActivity(intent)
                }

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                                super.onScrollStateChanged(recyclerView, newState)
                                if (!isLoading && newState == RecyclerView.SCROLL_STATE_IDLE && layoutManager.findLastVisibleItemPosition() == adapter.itemCount - 1) {
                                        fetchUnpaidInvoices(adapter.itemCount)
                                        isLoading = true
                                }
                        }
                })
        }

        private fun fetchUnpaidInvoices(offset: Int) {
                val etSearch: EditText = requireView().findViewById(R.id.etSearch)
                val limit = 10
                val search = etSearch.text.toString()

                val call: Call<ApiResponse> = requestInterface.getUnpaidInvoiceDetails(offset, limit, search)
                call.enqueue(object : Callback<ApiResponse> {
                        override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                                if (response.isSuccessful) {
                                        val apiResponse = response.body()
                                        val invoices = apiResponse?.message
                                        invoices?.let {
                                                if (offset == 0) {
                                                        adapter.setItems(it)
                                                } else {
                                                        adapter.addItems(it)
                                                }
                                                isLoading = false
                                        }
                                } else {
                                        // Handle error response
                                        val errorBody = response.errorBody()?.string()
                                        Log.e("API_ERROR", errorBody ?: "Unknown error")
                                        Toast.makeText(requireContext(), "Failed to fetch unpaid invoices. Please try again later.", Toast.LENGTH_SHORT).show()
                                }
                        }

                        override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                                // Handle failure
                                Toast.makeText(requireContext(), "Network error. Please check your internet connection and try again.", Toast.LENGTH_SHORT).show()
                                t.printStackTrace()
                        }
                })
        }
}



