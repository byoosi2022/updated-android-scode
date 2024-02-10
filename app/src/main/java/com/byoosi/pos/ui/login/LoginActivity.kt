package com.byoosi.pos.ui.login

import android.os.Bundle
import android.view.View
import com.byoosi.pos.base.BaseActivity
import com.byoosi.pos.data.pref.SharedPref
import com.byoosi.pos.ui.home.HomeActivity
import com.byoosi.pos.R
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
                } else {
                    login()
                }
            }
        }
    }

    private fun login() {
        callApi(true, {
            requestInterface.login(mapOf<String, Any>("usr" to etEmail.text.toString(), "pwd" to etPassword.text.toString()))
        }, {
            SharedPref.setLoginData(it)
            startActivity<HomeActivity>()
        }, { toast(getString(R.string.login_failed)) }
        )
    }
}