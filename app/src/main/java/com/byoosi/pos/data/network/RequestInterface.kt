package com.byoosi.pos.data.network

import android.util.Log
import com.byoosi.pos.BuildConfig
import com.byoosi.pos.data.pref.SharedPref
import com.byoosi.pos.model.*
import com.byoosi.pos.utils.BASE_URL
import com.byoosi.pos.utils.TIME_OUT
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

@JvmSuppressWildcards
interface RequestInterface {

    @POST("method/android_pos.api.auth.login")
    suspend fun login(@Body map: Map<String, Any>): Login

    @POST("method/android_pos.api.item_details.get_item_details")
    suspend fun getProducts(@Body map: Map<String, Any>): CommonResponse<List<ProductItem>>

    @POST("method/android_pos.api.customer.get_customer_details")
    suspend fun getCustomers(@Body map: Map<String, Any>): CommonResponse<List<UserItem>>

    @POST("method/android_pos.api.customer.get_customer")
    suspend fun getCustomersPay(@Body map: Map<String, Any>): CommonResponse<List<UserItem>>

    @POST("method/android_pos.api.customer.create_customer")
    suspend fun addCustomers(@Body map: Map<String, Any>): CommonResponse<String>

    @POST("method/android_pos.api.invoice.create_invoice")
    suspend fun addInvoice(@Body map: Map<String, Any>): InvoiceResponse

    @POST("method/android_pos.api.invoice.get_invoice_details")
    suspend fun getInvoiceList(@Body map: Map<String, Any>): CommonResponse<List<InvoiceItem>>

    @POST("method/android_pos.api.invoice.cancel_invoice")
    suspend fun cancelInvoice(@Query("name") name: String?): Response<CancelResponse>

    @POST("method/android_pos.api.print_invoice.print_invoice")
    suspend fun getInvoiceContent(@Body map: Map<String, Any>): InvoiceContentResponse

    @POST("method/android_pos.api.invoice.get_sales_invoice")
    suspend fun getInvoiceDetail(@Body map: Map<String, Any>): InvoiceResponse

    @POST("method/android_pos.api.payment_entry.create_payment")
    suspend fun changePayment(@Body map: Map<String, Any>): InvoiceResponse

    @POST("method/android_pos.api.payment_entry.get_mode_of_payment")
    suspend fun getModeOfPayment(): CommonResponse<List<PaymentMode>>

    @POST("method/android_pos.api.invoice.get_sales_payment_summary")
    suspend fun getSalesPaymentSummary(
        @Body requestMap: Map<String, String>,
    ): CommonResponse<SalesPaymentSummaryResponse>

    @POST("method/android_pos.api.payment_entry.make_payment")
    suspend fun makePayment(@Body map: Map<String, Any>): PaymentResponse

    @POST("resource/User")
    suspend fun registerUser(@Body map: RegisterRequest): Response<RegisterRequest>



    @GET("method/logout")
    suspend fun logout(): CommonResponse<Any>

    companion object {
        @Volatile
        private var INSTANCE: RequestInterface? = null

        fun getInstance(): RequestInterface {
            return INSTANCE ?: synchronized(this) { INSTANCE ?: create().also { INSTANCE = it } }
        }

        private fun create(): RequestInterface {
            // Create OkHttpClient with timeout settings
            val httpClient = OkHttpClient.Builder()
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    // Intercept the request and modify headers
                    val requestBuilder = chain.request().newBuilder()
                        .method(chain.request().method, chain.request().body)
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")

                    // Check if user is logged in and has API key and secret
                    if (SharedPref.isLogin) {
                        val apiKey = SharedPref.apiKey
                        val apiSecret = SharedPref.apiSecret

                        // Log API key and secret for debugging purposes
                        Log.d("API_LOG", "API Key: $apiKey, API Secret: $apiSecret")

                        // Add Authorization header
                        requestBuilder.header("Authorization", "token $apiKey:$apiSecret")
                    }

                    // Proceed with the modified request
                    chain.proceed(requestBuilder.build())
                }

            // Add logging interceptor for debugging
            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
                httpClient.addInterceptor(logging)
            }

            // Create Retrofit instance
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
                .build()
                .create(RequestInterface::class.java)
        }

    }
}