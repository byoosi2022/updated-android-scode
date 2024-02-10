package com.byoosi.pos.data.network

import android.util.Log
import com.byoosi.pos.data.pref.SharedPref
import com.byoosi.pos.model.*
import com.byoosi.pos.utils.BASE_URL
import com.byoosi.pos.utils.TIME_OUT
import com.byoosi.pos.BuildConfig
import com.byoosi.pos.model.CustomModel.ApiResponse
import com.byoosi.pos.model.CustomModel.UpaidInvoice
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JvmSuppressWildcards
interface RequestInterface {

    @POST("method/android_pos.api.auth.login")
    suspend fun login(@Body map: Map<String, Any>): Login

//    @POST("method/android_pos.api.item_details.get_item_details")
//    suspend fun getProducts(@Body map: Map<String, Any>): CommonResponse<List<ProductItem>>

    @GET("method/android_pos.api.item_details.get_item_details")
    suspend fun getProducts(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("search") search: String?,
        @Query("user") user: String?
    ): CommonResponse<List<ProductItem>>

    @POST("method/android_pos.api.customer.get_customer_details")
    suspend fun getCustomers(@Body map: Map<String, Any>): CommonResponse<List<UserItem>>

    @POST("method/android_pos.api.customer.get_customer")
    suspend fun getCustomersPay(@Body map: Map<String, Any>): CommonResponse<List<UserItem>>

    @POST("method/android_pos.api.customer.create_customer")
    suspend fun addCustomers(@Body map: Map<String, Any>): CommonResponse<String>

    @POST("method/android_pos.api.invoice.create_invoice")
    suspend fun addInvoice(@Body map: Map<String, Any>): InvoiceResponse

    @POST("method/android_pos.api.api_cart.add_to_cart")
    suspend fun addToCart(@Body map: Map<String, Any>): Response<AddToCartRequest>


    @POST("method/android_pos.api.invoice.get_invoice_details")
    suspend fun getInvoiceList(
        @HeaderMap headers: Map<String, String>, // Include headers as a separate parameter
        @Body params: Map<String, Any>
    ): CommonResponse<List<InvoiceItem>>

    @POST("method/android_pos.api.invoice.get_invoice_details")
    suspend fun getUpaidInvoiceList(
        @Header("Authorization") authToken: String = getDefaultAuthToken(),
        @Body requestMap: Map<String, Any>
    ): UpaidInvoice

    @POST("method/android_pos.api.invoice.cancel_invoice")
    suspend fun cancelInvoice(@Query("name") name: String?): Response<CancelResponse>

    @POST("method/android_pos.api.print_invoice.print_invoice")
    suspend fun getInvoiceContent(@Body map: Map<String, Any>): InvoiceContentResponse

    @POST("method/android_pos.api.invoice.get_sales_invoice")
    suspend fun getInvoiceDetail(
        @Header("Authorization") authToken: String = getDefaultAuthToken(),
        @Body requestMap: Map<String, Any>
    ):InvoiceResponse

    @GET("method/android_pos.api.invoice.get_invoice_details")
    fun getUnpaidInvoiceDetails(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("search") search: String
    ): Call<ApiResponse>

    @POST("method/android_pos.api.payment_entry.create_payment")
    suspend fun changePayment(@Body map: Map<String, Any>): InvoiceResponse

    @POST("method/android_pos.api.payment_entry.get_mode_of_payment")
    suspend fun getModeOfPayment(): CommonResponse<List<PaymentMode>>

    @POST("method/android_pos.api.token.get_logged_in_user_api_key_and_secret")
    suspend fun getToken(): CommonResponse<List<TokenApi>>

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

    @POST("method/android_pos.api.payment_entry.get_mode_of_payment")
    suspend fun getModeOfPayment(@Header("Authorization") token: String): CommonResponse<List<PaymentMode>>



    companion object {
        private fun getDefaultAuthToken(): String {
            val apiKey = SharedPref.apiKey
            val apiSecret = SharedPref.apiSecret

            Log.d("API_LOG", "API Key: $apiKey")
            Log.d("API_LOG", "API Secret: $apiSecret")
            return "token $apiKey:$apiSecret"
        }
        @Volatile
        private var INSTANCE: RequestInterface? = null

        fun getInstance(): RequestInterface {
            return INSTANCE ?: synchronized(this) { INSTANCE ?: create().also { INSTANCE = it } }
        }

        fun create(): RequestInterface {
            val httpClient = OkHttpClient.Builder()
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    val requestBuilder = chain.request().newBuilder()
                        .method(chain.request().method, chain.request().body)
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")

                    if (SharedPref.isLogin) {
                        // Add authorization header only if the user is logged in
                        val authToken = "token ${SharedPref.apiKey}:${SharedPref.apiSecret}"
                        Log.d("RequestHeaders", "Authorization: $authToken")
                        requestBuilder.header("Authorization", authToken)
                    }


                    val request = requestBuilder.build()

                    // Log request headers
                    Log.d("RequestHeaders", "Headers: ${request.headers}")

                    val response = chain.proceed(request)

                    // Log response headers
                    Log.d("ResponseHeaders", "Headers: ${response.headers}")

                    response
                }

            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
                httpClient.addInterceptor(logging)
            }

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
                .build()
                .create(RequestInterface::class.java)
        }

    }
}