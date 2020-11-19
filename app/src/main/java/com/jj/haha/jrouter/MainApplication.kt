package com.jj.haha.jrouter

import android.app.Application
import com.jj.haha.jrouter.api.Router

class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Router.lazyInit(this)
    }
}