package com.byoosi.pos.ui.paymentsummary

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.byoosi.pos.R
import com.byoosi.pos.data.network.RequestInterface
import com.byoosi.pos.data.pref.SharedPref
import com.byoosi.pos.model.PaymentMode
import com.byoosi.pos.utils.AppSignatureHelper.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PaymentEntryActivityFragment : Fragment() {

        private lateinit var requestInterface: RequestInterface
        private lateinit var spinnerModeOfPayment: Spinner
        private lateinit var etAmount: EditText
        private lateinit var btnContinue: Button

        override fun onCreateView(
                inflater: LayoutInflater, container: ViewGroup?,
                savedInstanceState: Bundle?
        ): View? {
                // Inflate the layout for this fragment
                val view = inflater.inflate(R.layout.payment_entry, container, false)

                // Initialize views
                spinnerModeOfPayment = view.findViewById(R.id.ModeOfPayment)
                etAmount = view.findViewById(R.id.etAmount)
                btnContinue = view.findViewById(R.id.btnContinue)

                return view
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                super.onViewCreated(view, savedInstanceState)
                requestInterface = RequestInterface.getInstance()

                // Call API to get the mode of payment and populate the spinner
                getModeOfPayment()
        }

        private fun getModeOfPayment() {
                GlobalScope.launch(Dispatchers.Main) {
                        try {
                                Log.d(TAG, "Before Retrieval - API Key: ${SharedPref.apiKey}, API Secret: ${SharedPref.apiSecret}")
                                // Simulating API call with delay
                                val response = withContext(Dispatchers.IO) {
                                        requestInterface.getModeOfPayment()
                                }

                                // Handle the response and update the spinner
                                val modeOfPaymentList: List<PaymentMode> = response.message
                                updateSpinnerWithModeOfPayment(modeOfPaymentList)
                        } catch (e: Exception) {
                                // Handle exception, show an error message, etc.
                                e.printStackTrace()
                        }
                }
        }

        private fun updateSpinnerWithModeOfPayment(modeOfPaymentList: List<PaymentMode>) {
                val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        modeOfPaymentList.map { it.name }
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerModeOfPayment.adapter = adapter
        }
}
