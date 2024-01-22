package com.byoosi.pos

import android.app.Application
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import coil.Coil
import coil.ImageLoader
import com.chibatching.kotpref.Kotpref
import com.chibatching.kotpref.gsonpref.gson
import com.google.gson.Gson

class MyApp : Application(), LifecycleObserver {
    private val TAG by lazy { getString(R.string.app_name) }

    companion object {
        private lateinit var mInstance: MyApp
        fun getInstance(): MyApp = mInstance
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        Kotpref.init(this)
        Kotpref.gson = Gson()
        Coil.setImageLoader { ImageLoader(this) }
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }
}
