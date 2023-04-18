package com.dilkw.studycodekotlin.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast

/**
 * service学习
 */
class  FirstService : Service() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        Toast.makeText(this, "service onBind", Toast.LENGTH_SHORT).show()
        return null
    }

    override fun onDestroy() {
        Toast.makeText(this, "service destroy", Toast.LENGTH_SHORT).show()
    }
}