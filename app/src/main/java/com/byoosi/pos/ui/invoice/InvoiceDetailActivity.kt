package com.byoosi.pos.ui.invoice

import android.app.Dialog
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintJob
import android.print.PrintManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.byoosi.pos.R
import com.byoosi.pos.base.BaseActivity
import com.byoosi.pos.model.CancelResponse
import com.byoosi.pos.model.InvoiceDetail
import com.byoosi.pos.utils.PrinterCommands
import com.byoosi.pos.utils.Utils
import com.byoosi.pos.utils.gone
import kotlinx.android.synthetic.main.activity_invoice_detail.*
import kotlinx.android.synthetic.main.cancel_yes_no.*
import kotlinx.android.synthetic.main.dialog_enter_amount.*
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.OutputStream
import java.io.UnsupportedEncodingException
import java.util.*

/**
 * Created by pintusingh on 27/12/20.
 */

class InvoiceDetailActivity : BaseActivity(R.layout.activity_invoice_detail), View.OnClickListener {
    private var invoiceId: String? = null
    private lateinit var printJobs: PrintJob
    private var btsocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null
    private lateinit var invoiceDetail: InvoiceDetail
    private lateinit var cancelResponse: CancelResponse
    var FONT_TYPE: Byte = 0
    private lateinit var adapterItems: InvoiceItemAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setOnClick()
    }

    private fun init() {
        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setHomeButtonEnabled(true)
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        invoiceId = intent.getStringExtra("invoice_id")
        adapterItems = InvoiceItemAdapter().also { invoiceId?.let { getInvoiceDetail(it) } }
        rvItems.adapter = adapterItems
    }

    private fun setOnClick() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
        btnChangePayment.setOnClickListener(this)
        BtnCancel.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnChangePayment -> showPriceDialog()
            R.id.BtnCancel -> showCancelDialog()
        }
    }


    private fun showPriceDialog() {
        try {
            val dialog = Dialog(this)
            dialog.window?.run {
                requestFeature(Window.FEATURE_NO_TITLE)
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
            dialog.setContentView(R.layout.dialog_enter_amount)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(true)
            callApi(false, { requestInterface.getModeOfPayment() },
                {
                    dialog.run {
                        val adapter = ArrayAdapter(
                            context,
                            android.R.layout.simple_spinner_item,
                            it.message.map { it.name }
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerModeOfPayment.adapter = adapter
                        btnContinue.setOnClickListener {
                            val paymentMode =
                                adapter.getItem(spinnerModeOfPayment.selectedItemPosition)!!
                            val priceToPay = etAmount.text.toString().toDouble()
                            dismiss()
                            changePaymentStatus(priceToPay, paymentMode);
                        }
                        show()
                        val params = window?.attributes
                        params?.width = LinearLayout.LayoutParams.MATCH_PARENT
                        params?.height = LinearLayout.LayoutParams.WRAP_CONTENT
                        window?.attributes = params
                    }
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_product_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_print -> {
                callApi(
                    true,
                    {
                        invoiceId?.let {
                            requestInterface.getInvoiceContent(
                                mapOf<String, Any>(
                                    "invoice_id" to (invoiceId ?: "")
                                )
                            )
                        }
                    },
                    { it?.message?.let { it1 -> doWebViewPrint(it1) } })
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getInvoiceDetail(invoiceId: String) {
        callApi(true, {
            requestInterface.getInvoiceDetail(mapOf("docname" to invoiceId))
        }, {
            tvInvoiceNumber.text = it.message.name
            tvCustomerName.text = it.message.customer_name ?: "-"
            tvCustomerNumber.text = it.message.contact_mobile ?: "-"
            tvCustomerEmail.text = it.message.contact_email
            tvTotal.text = getString(R.string.price, it.message.total)
            tvPaidAmount.text =
                getString(R.string.price, it.message.total - it.message.outstanding_amount)
            tvRemainingAmount.text = getString(R.string.price, it.message.outstanding_amount)
            invoiceDetail = it.message
            adapterItems.addAll(it.message.items)

            if (it.message.outstanding_amount == 0.0) {
                btnChangePayment.gone()
                BtnCancel.gone()
            }
        })
    }
    private fun showCancelDialog() {
        val dialog = Dialog(this)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.cancel_yes_no)
        dialog.setCanceledOnTouchOutside(false) // Prevent dismissing on outside touch
        dialog.setCancelable(true)

        val btnYes = dialog.findViewById<Button>(R.id.btnYes)
        val btnNo = dialog.findViewById<Button>(R.id.btnNo)
        // Set a click listener for the "Yes" button
        btnYes.setOnClickListener {
            // Assuming you want to use invoiceDetail.name for the API call
            val nameValue = invoiceDetail.name

            if (nameValue != null) {
                // Launch a coroutine to make the API call
                lifecycleScope.launch {
                    try {
                        val response = requestInterface.cancelInvoice(nameValue)

                        if (response.isSuccessful) {
                            val cancelResponse = response.body()
                            if (cancelResponse != null) {
                                // Check if the response contains the expected structure
                                if (cancelResponse.status == "success") {
                                    // Handle success, e.g., show a toast
                                    val message = cancelResponse.message.message // Access the message property
                                    Toast.makeText(this@InvoiceDetailActivity, message, Toast.LENGTH_SHORT).show()
                                } else {
                                    // Handle errors, e.g., show an error message
                                    val errorMessage = cancelResponse.message.message // Access the message property
                                    // Create a new instance of the modal and show it with the error message
                                    Toast.makeText(this@InvoiceDetailActivity, errorMessage, Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                // Handle cases where the response body is null or unexpected
                                Toast.makeText(this@InvoiceDetailActivity, "Invalid API response", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            // Handle API errors based on response status code
                            val errorBody = response.errorBody()?.string()
                            // Handle the error response as needed
                        }
                    } catch (e: Exception) {
                        // Handle network or other exceptions
                        Toast.makeText(this@InvoiceDetailActivity, "Network error", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            dialog.dismiss()
        }

        // Set a click listener for the "No" button
        btnNo.setOnClickListener {
            // Handle the case where the user chooses not to cancel
            dialog.dismiss()
        }

        // Show the dialog
        dialog.show()

        // Adjust the dialog size
        val params = dialog.window?.attributes
        params?.width = LinearLayout.LayoutParams.MATCH_PARENT
        params?.height = LinearLayout.LayoutParams.WRAP_CONTENT
        dialog.window?.attributes = params
    }




    private fun changePaymentStatus(amount: Double, modeOfPayment: String) {
        callApi(true, {
            val map = mapOf(
                "paid_amount" to amount,
                "sales_invoice" to invoiceDetail.name,
                "mode_of_payment" to modeOfPayment,
            )
            requestInterface.changePayment(map)
        }, {
            tvTotal.text = getString(R.string.price, it.message.total)
            tvPaidAmount.text =
                getString(R.string.price, it.message.total - it.message.outstanding_amount)
            tvRemainingAmount.text = getString(R.string.price, it.message.outstanding_amount)

            invoiceDetail.paid_amount = it.message.total - it.message.outstanding_amount
            invoiceDetail.total = it.message.total
            invoiceDetail.outstanding_amount = it.message.outstanding_amount

            if (it.message.outstanding_amount == 0.0) {
                btnChangePayment.gone()
//                BtnCancel.gone()
            }

        })
    }

    private var mWebView: WebView? = null

    private fun doWebViewPrint(message: String) {
        // Create a WebView object specifically for printing
        val webView = WebView(this@InvoiceDetailActivity)
        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest) =
                false

            override fun onPageFinished(view: WebView, url: String) {
                Log.i(TAG, "page finished loading $url")
                createWebPrintJob(view)
                mWebView = null
            }
        }

        // Generate an HTML document on the fly:
        val htmlDocument = message
        webView.loadDataWithBaseURL(null, htmlDocument, "text/HTML", "UTF-8", null)

        // Keep a reference to WebView object until you pass the PrintDocumentAdapter
        // to the PrintManager
        mWebView = webView
    }


    private fun createWebPrintJob(webView: WebView) {

        // Get a PrintManager instance
        (getSystemService(Context.PRINT_SERVICE) as? PrintManager)?.let { printManager ->

            val jobName = "${getString(R.string.app_name)} Document"

            // Get a print adapter instance
            val printAdapter = webView.createPrintDocumentAdapter(jobName)

            // Create a print job with name and adapter instance
            printManager.print(
                jobName,
                printAdapter,
                PrintAttributes.Builder().build()
            )/*.also { printJob ->

                // Save the job object for later status checking
                printJobs += printJob
            }*/
        }
    }

    protected fun printBill() {
        /*if (btsocket == null) {
            val BTIntent = Intent(applicationContext, DeviceList::class.java)
            this.startActivityForResult(BTIntent, DeviceList.REQUEST_CONNECT_BT)
        } else {
            var opstream: OutputStream? = null
            try {
                opstream = btsocket!!.getOutputStream()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            outputStream = opstream

            //print command
            try {
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                outputStream = btsocket!!.getOutputStream()
                val printformat = byteArrayOf(0x1B, 0x21, 0x03)
                outputStream?.write(printformat)
                printCustom("Fair Group BD", 2, 1)
                printCustom("Pepperoni Foods Ltd.", 0, 1)
//                printPhoto(R.drawable.ic_icon_pos)
                printCustom("H-123, R-123, Dhanmondi, Dhaka-1212", 0, 1)
                printCustom("Hot Line: +88000 000000", 0, 1)
                printCustom("Vat Reg : 0000000000,Mushak : 11", 0, 1)
                val dateTime = getDateTime()
                printText(leftRightAlign(dateTime[0], dateTime[1]))
                printText(leftRightAlign("Qty: Name", "Price "))
                printCustom(String(CharArray(32)).replace("\u0000", "."), 0, 1)
                printText(leftRightAlign("Total", "2,0000/="))
                printNewLine()
                printCustom("Thank you for coming & we look", 0, 1)
                printCustom("forward to serve you again", 0, 1)
                printNewLine()
                printNewLine()
                outputStream?.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }*/
        var opstream: OutputStream? = null
        try {
            opstream = btsocket!!.getOutputStream()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        outputStream = opstream

        //print command
        try {
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            outputStream = btsocket!!.getOutputStream()
            val printformat = byteArrayOf(0x1B, 0x21, 0x03)
            outputStream?.write(printformat)
            printCustom("Fair Group BD", 2, 1)
            printCustom("Pepperoni Foods Ltd.", 0, 1)
//                printPhoto(R.drawable.ic_icon_pos)
            printCustom("H-123, R-123, Dhanmondi, Dhaka-1212", 0, 1)
            printCustom("Hot Line: +88000 000000", 0, 1)
            printCustom("Vat Reg : 0000000000,Mushak : 11", 0, 1)
            val dateTime = getDateTime()
            printText(leftRightAlign(dateTime[0], dateTime[1]))
            printText(leftRightAlign("Qty: Name", "Price "))
            printCustom(String(CharArray(32)).replace("\u0000", "."), 0, 1)
            printText(leftRightAlign("Total", "2,0000/="))
            printNewLine()
            printCustom("Thank you for coming & we look", 0, 1)
            printCustom("forward to serve you again", 0, 1)
            printNewLine()
            printNewLine()
            outputStream?.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    protected fun printDemo() {

        var opstream: OutputStream? = null
        try {
            opstream = btsocket?.getOutputStream()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        outputStream = opstream

        //print command
        try {
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            outputStream = btsocket?.getOutputStream()
            val printformat = byteArrayOf(0x1B, (0 * 21).toByte(), FONT_TYPE)
            //outputStream?.write(printformat);

            //print title
            printUnicode()
            //print normal text
            printCustom("HELLLO", 0, 0)
//                printPhoto(R.drawable.img)
            printNewLine()
            printText("     >>>>   Thank you  <<<<     ") // total 32 char in a single line
            //resetPrint(); //reset printer
            printUnicode()
            printNewLine()
            printNewLine()
            outputStream?.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    //print custom
    private fun printCustom(msg: String, size: Int, align: Int) {
        //Print config "mode"
        val cc = byteArrayOf(0x1B, 0x21, 0x03) // 0- normal size text
        //byte[] cc1 = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        val bb = byteArrayOf(0x1B, 0x21, 0x08) // 1- only bold text
        val bb2 = byteArrayOf(0x1B, 0x21, 0x20) // 2- bold with medium text
        val bb3 = byteArrayOf(0x1B, 0x21, 0x10) // 3- bold with large text
        try {
            when (size) {
                0 -> outputStream?.write(cc)
                1 -> outputStream?.write(bb)
                2 -> outputStream?.write(bb2)
                3 -> outputStream?.write(bb3)
            }
            when (align) {
                0 ->                     //left align
                    outputStream?.write(PrinterCommands.ESC_ALIGN_LEFT)
                1 ->                     //center align
                    outputStream?.write(PrinterCommands.ESC_ALIGN_CENTER)
                2 ->                     //right align
                    outputStream?.write(PrinterCommands.ESC_ALIGN_RIGHT)
            }
            outputStream?.write(msg.toByteArray())
            outputStream?.write(PrinterCommands.LF.toInt())
            //outputStream?.write(cc);
            //printNewLine();
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    //print photo
    fun printPhoto(img: Int) {
        try {
            val bmp = BitmapFactory.decodeResource(
                resources,
                img
            )
            if (bmp != null) {
                val command: ByteArray = Utils.decodeBitmap(bmp)
                outputStream?.write(PrinterCommands.ESC_ALIGN_CENTER)
                printText(command)
            } else {
                Log.e("Print Photo error", "the file isn't exists")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("PrintTools", "the file isn't exists")
        }
    }

    //print unicode
    fun printUnicode() {
        try {
            outputStream?.write(PrinterCommands.ESC_ALIGN_CENTER)
            printText(Utils.UNICODE_TEXT)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    //print new line
    private fun printNewLine() {
        try {
            outputStream?.write(PrinterCommands.FEED_LINE)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun resetPrint() {
        try {
            outputStream?.write(PrinterCommands.ESC_FONT_COLOR_DEFAULT)
            outputStream?.write(PrinterCommands.FS_FONT_ALIGN)
            outputStream?.write(PrinterCommands.ESC_ALIGN_LEFT)
            outputStream?.write(PrinterCommands.ESC_CANCEL_BOLD)
            outputStream?.write(PrinterCommands.LF.toInt())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    //print text
    private fun printText(msg: String) {
        try {
            // Print normal text
            outputStream?.write(msg.toByteArray())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    //print byte[]
    private fun printText(msg: ByteArray) {
        try {
            // Print normal text
            outputStream?.write(msg)
            printNewLine()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    private fun leftRightAlign(str1: String?, str2: String?): String {
        var ans = str1 + str2
        if (ans.length < 31) {
            val n = 31 - str1!!.length + str2!!.length
            ans = str1 + String(CharArray(n)).replace("\u0000", " ") + str2
        }
        return ans
    }


    private fun getDateTime(): Array<String?> {
        val c: Calendar = Calendar.getInstance()
        val dateTime = arrayOfNulls<String>(2)
        dateTime[0] =
            c.get(Calendar.DAY_OF_MONTH).toString() + "/" + c.get(Calendar.MONTH) + "/" + c.get(
                Calendar.YEAR
            )
        dateTime[1] = c.get(Calendar.HOUR_OF_DAY).toString() + ":" + c.get(Calendar.MINUTE)
        return dateTime
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (btsocket != null) {
                outputStream?.close()
                btsocket?.close()
                btsocket = null
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /*try {
            btsocket = DeviceList.getSocket()
            if (btsocket != null) {
                printText("message.getText().toString()")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }*/
    }
}





