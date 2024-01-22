package com.byoosi.pos.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.byoosi.pos.base.BaseActivity
import com.byoosi.pos.data.pref.SharedPref
import com.byoosi.pos.ui.home.HomeActivity
import com.byoosi.pos.ui.login.LoginActivity
import com.byoosi.pos.R
import org.jetbrains.anko.startActivity

/**
 * Created by Pintu Singh on 21/12/20.
 */

class SplashActivity() : BaseActivity(R.layout.activity_splash) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed({
            if (SharedPref.isLogin) startActivity<HomeActivity>()
            else startActivity<LoginActivity>()
            finishAffinity()
        }, 3000)
    }
}