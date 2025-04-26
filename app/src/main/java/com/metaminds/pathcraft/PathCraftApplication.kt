package com.metaminds.pathcraft

import android.app.Application


class PathCraftApplication: Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container= DefaultApplication(context = this)
    }
}