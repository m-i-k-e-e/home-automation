package org.mike.homeconnect

import android.app.Application
import com.google.android.things.update.UpdateManager

class ThingsApp: Application() {
    override fun onCreate() {
        super.onCreate()
        UpdateManager.getInstance().channel = "dev-channel"
    }
}