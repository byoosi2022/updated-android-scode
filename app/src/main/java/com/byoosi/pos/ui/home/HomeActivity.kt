package com.byoosi.pos.ui.home

/**
 * Created by pintusingh on 24/12/20.
 */

import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.byoosi.pos.base.BaseActivity
import com.byoosi.pos.data.pref.SharedPref
import com.byoosi.pos.ui.splash.SplashActivity
import com.byoosi.pos.R
import kotlinx.android.synthetic.main.activity_home.*
import org.jetbrains.anko.startActivity

class HomeActivity : BaseActivity(R.layout.activity_home) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        setSupportActionBar(toolbar)
        val navController = findNavController(this, R.id.navHostFragment)
        NavigationUI.setupWithNavController(navigationView, navController)
        NavigationUI.setupActionBarWithNavController(this, navController, mainDrawer)
        navigationView.menu[3].setOnMenuItemClickListener {
            callApi(true, { requestInterface.logout() }, {
                SharedPref.clear()
                startActivity<SplashActivity>()
                finishAffinity()
            })
            true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(this, R.id.navHostFragment)
        return NavigationUI.navigateUp(navController, mainDrawer) || super.onNavigateUp()
    }

    override fun onBackPressed() {
        if (mainDrawer.isDrawerOpen(GravityCompat.START)) mainDrawer.closeDrawer(GravityCompat.START)
        else super.onBackPressed()
    }
}