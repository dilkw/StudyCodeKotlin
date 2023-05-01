package com.dilkw.client

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dilkw.studycodekotlin.aidl.IMyAidlInterface

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btn_startService).apply {
            this.setOnClickListener {
                Log.d("Client_MainActivity", "点击事件: ")
                requestApp()
                Log.d("Client_MainActivity", "onCreate: $packageName")
            }
        }
    }

    private fun requestApp() {
        val intent = Intent()
        intent.component = ComponentName(
            "com.dilkw.studycodekotlin",
            "com.dilkw.studycodekotlin.aidl.AidlService"
        )
        //intent.action = "android.intent.action.AidlService"
        val serviceConnection = object: ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                Log.d("Client_MainActivity", "onServiceConnected: ")
                val iMyAidlInterface = IMyAidlInterface.Stub.asInterface(service)
                var aidlString = ""
                try {
                    aidlString = iMyAidlInterface.requestApp("调用App方法")
                } catch (e: RemoteException) {
                    e.printStackTrace()
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
                Toast.makeText(this@MainActivity, aidlString, Toast.LENGTH_SHORT).show()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                Log.d("Client_MainActivity", "onServiceDisconnected: ")
            }

        }
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }
}