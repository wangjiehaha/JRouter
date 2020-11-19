package com.test.module

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.jwhaha.jrouter.api.ipclib.utils.ServiceUtils

class TestActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ServiceUtils.startServiceSafely(this, Intent(this, BuyService::class.java))
    }
}