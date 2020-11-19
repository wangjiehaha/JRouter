package com.jj.haha.jrouter

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.jj.haha.jrouter.api.Router
import com.test.module.sdk.MODULE1_KEY
import com.test.module.sdk.ModuleApi

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val moduleApi = Router.getService(ModuleApi::class.java, MODULE1_KEY)
        if (moduleApi != null) {
            val buyService = moduleApi.getBuyService()
            if (buyService != null) {
                Log.e("jjj", "buy: ${buyService.buy(20)}")
            }
        }
        findViewById<View>(R.id.test).setOnClickListener {
            val buyService = moduleApi.getBuyService()
            if (buyService != null) {
                Log.e("jjj", "buy: ${buyService.buy(20)}")
            }
        }
    }
}
