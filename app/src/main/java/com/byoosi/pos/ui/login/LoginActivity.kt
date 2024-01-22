package com.byoosi.pos.ui.login

import android.os.Bundle
import android.util.Log
import android.view.View
import com.byoosi.pos.base.BaseActivity
import com.byoosi.pos.data.pref.SharedPref
import com.byoosi.pos.ui.home.HomeActivity
import com.byoosi.pos.R
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_product.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class LoginActivity : BaseActivity(R.layout.activity_login), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setOnClick()
    }

    private fun setOnClick() {
        btnLogin.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnLogin -> {
                if (etEmail.text.isEmpty()) {
                    etEmail.error = getString(R.string.please_enter_email_address)
                } else if (etPassword.text.isEmpty()) {
                    etPassword.error = getString(R.string.please_enter_password)
                } else {
                    login()
                }
            }
        }
    }

    private fun login() {
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()

        // Log what is being sent to the server
        Log.d("LOGIN_LOG", "Sending login request with email: $email, password: $password")

        callApi(true, {
            requestInterface.login(mapOf("usr" to email, "pwd" to password))
        }, {
            // Log the response received from the server
            Log.d("LOGIN_LOG", "Login response received: $it")

            // Process the response and update UI or navigate to the next screen
            SharedPref.setLoginData(it)
            startActivity<HomeActivity>()
        }, {
            // Log an error if login fails
            Log.e("LOGIN_LOG", "Login failed with error: $it")
            toast(getString(R.string.login_failed))
        })
    }

}