package com.dilkw.studycodekotlin.aidl

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class AidlService : Service() {
    
    val TAG = "AidlService"

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: ")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: ")
        return super.onStartCommand(intent, flags, startId)
    }
    
    override fun onBind(intent: Intent): IBinder {
        return object: IMyAidlInterface.Stub() {
            override fun requestApp(params: String?): String {
                Log.d("App_MainActivity", "requestApp: ")
                return "aidl调用成功"
            }

        }
    }
}