package com.byoosi.pos.ui.home.product

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.Window
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.byoosi.pos.R
import com.byoosi.pos.base.BaseFragment
import com.byoosi.pos.data.pref.SharedPref
import com.byoosi.pos.model.ProductItem
import kotlinx.android.synthetic.main.dialog_enter_quantity.*
import kotlinx.android.synthetic.main.fragment_product.*
import org.jetbrains.anko.support.v4.alert

class ProductsFragment : BaseFragment(R.layout.fragment_product), View.OnClickListener {
    private lateinit var adapterProduct: ProductAdapter
    private var isLoading = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        setOnClick()
    }

    private fun init() {
        adapterProduct = ProductAdapter().also {
            getProducts(0)
            isLoading = true
        }
        rvProducts.apply { adapter = adapterProduct }
        onCartItemChanged()
    }

    private fun setOnClick() {
        llBottom.setOnClickListener(this)
        ivSearch.setOnClickListener(this)
        etSearch.setOnEditorActionListener { v, actionId, event ->
            getProducts(0)
            true
        }
        adapterProduct.run {
            setItemClickListener { view, i, productItem ->
                when (view.id) {
                    R.id.ivAdd -> if (productItem.quantity < productItem.stock.toInt()) {
                        productItem.quantity++
                        val list = arrayListOf<ProductItem>()
                        list.addAll(SharedPref.cartItems)
                        list.removeAll { it.item_code == productItem.item_code }
                        list.add(productItem)
                        SharedPref.cartItems = list
                        notifyItemChanged(i)
                        onCartItemChanged()
                    } else {
                        alert(
                            getString(R.string.product_is_out_of_stock),
                            getString(R.string.out_of_stock)
                        ){positiveButton(getString(R.string.okay)) {}}.show()
                    }
                    R.id.ivRemove -> if (productItem.quantity > 0) {
                        productItem.quantity--
                        val list = arrayListOf<ProductItem>()
                        list.addAll(SharedPref.cartItems)
                        list.removeAll { it.item_code == productItem.item_code }
                        if (productItem.quantity != 0) list.add(productItem)
                        SharedPref.cartItems = list
                        notifyItemChanged(i)
                        onCartItemChanged()
                    }
                    R.id.ivExpand -> {
                        productItem.isExpanded = !productItem.isExpanded
                        adapterProduct.notifyItemChanged(i)
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
                                            val list = arrayListOf<ProductItem>()
                                            list.addAll(SharedPref.cartItems)
                                            list.removeAll { it.item_code == productItem.item_code }
                                            list.add(productItem)
                                            SharedPref.cartItems = list
                                            notifyItemChanged(i)
                                            onCartItemChanged()
                                            dismiss()
                                        }
                                    } else {
                                        alert(
                                            getString(R.string.product_is_out_of_stock),
                                            getString(R.string.out_of_stock)
                                        ) {
                                            positiveButton(getString(R.string.okay)) {}
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
        rvProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!isLoading &&
                    (rvProducts.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition() == adapterProduct.itemCount - 1
                ) {
                    getProducts(adapterProduct.itemCount)
                    isLoading = true
                }
            }
        })
    }

    private fun onCartItemChanged() {
        val selectedItemCount = SharedPref.cartItems.size
        tvCartItemCount.text = getString(R.string.cart_item_count, selectedItemCount)
        llBottom.visibility = if (selectedItemCount > 0) VISIBLE else GONE
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.llBottom -> {
                findNavController().navigate(R.id.action_productFragment_to_cartFragment)
            }
            R.id.ivSearch -> getProducts(0)
        }
    }

    fun getProducts(offset: Int) {
        callApi(offset == 0, {
            val apiParams = mapOf<String, Any>(
                "offset" to offset,
                "limit" to 10,
                "search" to etSearch.text.toString(),
                "user" to (SharedPref.login?.message?.user ?: "")
            )

            // Log the API request parameters
            Log.d("API_LOG", "Sending getProducts request with parameters: $apiParams")

            requestInterface.getProducts(apiParams)
        }, { response ->
            // Callback function to handle the API response
            response.message.forEach { product ->
                // Update quantity for each product based on the items in SharedPref.cartItems
                SharedPref.cartItems.forEach {
                    if (product.item_code == it.item_code) product.quantity = it.quantity
                }
            }

            // Update the UI with the list of products
            response.message.let {
                if (offset == 0) adapterProduct.addAll(it) else adapterProduct.appendAll(it)
            }

            isLoading = false

            // Log the API response
            Log.d("API_LOG", "getProducts response: $response")

            // Log the SharedPref data after handling the response
            Log.d("API_LOG", "SharedPref data after getProducts: ${SharedPref.login}")
        })
    }


}