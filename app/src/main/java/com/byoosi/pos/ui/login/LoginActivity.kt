package com.byoosi.pos.ui.login

import android.os.Bundle
import android.util.Log
import android.view.View
import com.byoosi.pos.base.BaseActivity
import com.byoosi.pos.data.pref.SharedPref
import com.byoosi.pos.ui.home.HomeActivity
import com.byoosi.pos.R
import com.byoosi.pos.data.network.RequestInterface
import kotlinx.android.synthetic.main.activity_login.*
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
                } else if (etUrl.text.isEmpty()) {
                    etUrl.error = getString(R.string.please_enter_url)
                } else {
                    login()
                }
            }
        }
    }

    private fun login() {
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        val baseUrl = etUrl.text.toString()

        // Log what is being sent to the server
        Log.d("LOGIN_LOG", "Sending login request with email: $email, password: $password")

        // Set the dynamic base URL
        RequestInterface.setBaseUrl(baseUrl)

        // Use the corrected base URL for the login request
        callApi(true, {
            RequestInterface.getInstance().login(mapOf("usr" to email, "pwd" to password))
        }, { response ->
            // Log the response received from the server
            Log.d("LOGIN_LOG", "Login response received: $response")

            // Process the response and update UI or navigate to the next screen
            SharedPref.setLoginData(response)
            startActivity<HomeActivity>()
        }, { error ->
            // Log an error if login fails
            Log.e("LOGIN_LOG", "Login failed with error: $error")
            toast(getString(R.string.login_failed))
        })
    }




}
