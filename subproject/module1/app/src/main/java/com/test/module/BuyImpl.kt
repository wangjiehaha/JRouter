package com.test.module

import com.test.module.sdk.IBuy

object BuyImpl: IBuy.Stub() {

    var count = 0

    override fun buy(something: Int): Int {
        count += something
        return count
    }
}