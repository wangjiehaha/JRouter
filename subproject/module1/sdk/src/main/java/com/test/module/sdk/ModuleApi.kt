package com.test.module.sdk

const val MODULE1_KEY = "module1_key"

interface ModuleApi {
    fun getBuyService(): IBuy?
}