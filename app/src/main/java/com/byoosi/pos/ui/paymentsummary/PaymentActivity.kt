package com.byoosi.pos.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.byoosi.pos.R
import com.byoosi.pos.data.network.RequestInterface
import com.byoosi.pos.model.SalesPaymentSummaryResponse
import kotlinx.android.synthetic.main.payment_report.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class PaymentActivityFragment : Fragment() {

        private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        private val currentDate = Calendar.getInstance().time

        private lateinit var requestInterface: RequestInterface
        private var startDate = ""
        private var endDate = ""

        override fun onCreateView(
                inflater: LayoutInflater, container: ViewGroup?,
                savedInstanceState: Bundle?
        ): View? {
                // Inflate the layout for this fragment
                return inflater.inflate(R.layout.payment_report, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                super.onViewCreated(view, savedInstanceState)

                requestInterface = RequestInterface.getInstance()

                // Set up click listener for the startDateTextView
                val startDateTextView = view.findViewById<TextView>(R.id.startDateTextView)
                startDateTextView.setOnClickListener {
                        showDatePickerDialog(true) // true indicates start date selection
                }

//                val btnMenu = view.findViewById<TextView>(R.id.btnMenu)
//                btnMenu.setOnClickListener {
//
//                }


                // Set up click listener for the endDateTextView
                val endDateTextView = view.findViewById<TextView>(R.id.endDateTextView)
                endDateTextView.setOnClickListener {
                        showDatePickerDialog(false) // false indicates end date selection
                }

                // Call getSalesPaymentSummary to fetch payment summary with default dates
                updateDateRange(
                        dateFormat.format(currentDate), dateFormat.format(currentDate)
                )
        }

        private fun showDatePickerDialog(isStartDate: Boolean) {
                val calendar = Calendar.getInstance()

                val datePickerDialog = DatePickerDialog(
                        requireContext(),
                        { _, year, monthOfYear, dayOfMonth ->
                                val selectedDate = "$year-${monthOfYear + 1}-${dayOfMonth}"
                                if (isStartDate) {
                                        updateDateRange(selectedDate, endDate)
                                } else {
                                        updateDateRange(startDate, selectedDate)
                                }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                )

                datePickerDialog.show()
        }

        private fun updateDateRange(start: String, end: String) {
                startDate = start
                endDate = end

                getSalesPaymentSummary()
        }

        private fun getSalesPaymentSummary() {
                GlobalScope.launch(Dispatchers.Main) {
                        val response = withContext(Dispatchers.IO) {
                                requestInterface.getSalesPaymentSummary(
                                        mapOf(
                                                "start_date" to startDate,
                                                "end_date" to endDate
                                        )
                                )
                        }

                        // Handle the response and update UI
                        val salesPaymentSummary = response.message
                        updateUIWithSalesPaymentSummary(salesPaymentSummary)
                }
        }

        private fun updateUIWithSalesPaymentSummary(salesPaymentSummary: SalesPaymentSummaryResponse) {
                val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("en", "UG"))
                currencyFormat.currency = Currency.getInstance("UGX")

                // Format and update UI elements using the sales payment summary data
                loginUserTextView.text = "Full Name: ${salesPaymentSummary.user}"
                startDateTextView.text = "From: ${salesPaymentSummary.start_date}"
                endDateTextView.text = "To: ${salesPaymentSummary.end_date}"
                totalInvoicesTextView.text = "Total Sales: ${currencyFormat.format(salesPaymentSummary.total_invoices)}"
                totalPaymentsTextView.text = "Total Payments: ${currencyFormat.format(salesPaymentSummary.total_payments)}"
        }
}
