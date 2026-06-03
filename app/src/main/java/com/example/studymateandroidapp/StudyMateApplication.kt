package com.example.studymateandroidapp

import android.app.Application
import com.example.studymateandroidapp.utils.notification.NotificationHelper

class StudyMateApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannels(this)
    }
}
