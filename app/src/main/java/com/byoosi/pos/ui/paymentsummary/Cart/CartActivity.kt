package com.byoosi.pos.ui.paymentsummary.Cart
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.byoosi.pos.R
import com.byoosi.pos.data.pref.SharedPref


class CartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_carts)
    }
}
