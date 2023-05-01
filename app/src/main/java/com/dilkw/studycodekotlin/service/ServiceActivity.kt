package com.dilkw.studycodekotlin.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import com.dilkw.studycodekotlin.R
import com.google.android.material.button.MaterialButton

/**
 * service启动页面
 */
class ServiceActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service)

        val startServiceButton: MaterialButton = findViewById(R.id.btn_start_service)

        startServiceButton.setOnClickListener {
            val intent: Intent = Intent(this, FirstService::class.java)
            startService(intent)
            val channel: NotificationChannel = NotificationChannel("0", "NAME", NotificationManager.IMPORTANCE_HIGH)
            channel.enableLights(true)
            channel.setShowBadge(true)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            channel.enableVibration(true)

            val manager: NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)

            val notification = Notification.Builder(this, "0")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setCustomContentView(initRemoteViews())
                .setWhen(System.currentTimeMillis())
                .build()
            manager.notify(0, notification)

        }

        val startForegroundServiceButton: MaterialButton = findViewById(R.id.btn_start_foreground_service)
        startForegroundServiceButton.setOnClickListener {
            val intentForegroundService: Intent = Intent(this, ForegroundService::class.java)
            // 启动前台服务
            //startForegroundService(intentForegroundService)
            startService(intentForegroundService)
        }
    }

    private fun initRemoteViews(): RemoteViews {
        val remoteViews = RemoteViews(packageName, R.layout.service_foreground_remoteviews)
        return remoteViews
    }
}