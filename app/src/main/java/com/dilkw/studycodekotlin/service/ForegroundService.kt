package com.dilkw.studycodekotlin.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import com.dilkw.studycodekotlin.R

/**
 * 前台service
 */
class ForegroundService : Service() {

    private val TAG = "ForegroundService"

    val ID = "com.dilkw.studycodekotlin.service"
    val NAME = "test"

    private lateinit var mNotification: Notification

    private lateinit var mRemoteViews: RemoteViews

    private lateinit var mNotificationManager: NotificationManager

    private val CLOSE_CLICK_REQUEST_CODE = 1001

    // 关闭事件 action
    private val INTENT_ACTION_CLOSE_EVENT = "CLOSE_EVENT"
    // 通知栏消息被清除时的intent.action
    private val INTENT_ACTION_CLEAR_NOTIFICATION = "CLEAR_NOTIFICATION"
    // 点击 previous 事件intent.action
    private val INTENT_ACTION_PREVIOUS = "PREVIOUS"
    // 点击 pause 事件intent.action
    private val INTENT_ACTION_PAUSE = "PAUSE"
    // 点击 play 事件intent.action
    private val INTENT_ACTION_PLAY = "PLAY"
    // 点击 next 事件intent.action
    private val INTENT_ACTION_NEXT = "NEXT"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        Log.d(TAG, "onCreate: ")
        super.onCreate()
        initRemoteViews()
        mNotification = createNotification()
        mNotification.flags = Notification.FLAG_AUTO_CANCEL
        mNotification.deleteIntent = clearNotificationIntent()
        startForeground(1, mNotification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand:\n ")
        if (intent != null) {
            when(intent.action) {
                INTENT_ACTION_PREVIOUS -> {
                    Log.d(TAG, "上一首")
                }
                INTENT_ACTION_PAUSE -> {
                    Log.d(TAG, "暂停")
                    stopForeground(false)
                    mRemoteViews.setImageViewResource(R.id.btn_pause_or_play, R.drawable.ic_foreground_service_play)
                    mNotificationManager.notify(1, mNotification)
                }
                INTENT_ACTION_PLAY -> {
                    Log.d(TAG, "播放")
                    onStartCommand(null, 0, 0)
                    mRemoteViews.setImageViewResource(R.id.btn_pause_or_play, R.drawable.ic_foreground_service_pause)
                    mNotificationManager.notify(1, mNotification)
                }
                INTENT_ACTION_NEXT -> {
                    Log.d(TAG, "下一首")
                }
                INTENT_ACTION_CLOSE_EVENT, INTENT_ACTION_CLEAR_NOTIFICATION -> {
                    Log.d(TAG, "stopForeground: ")
                    stopSelf()
                }
                else -> {
                    Log.d(TAG, "onStartCommand: ")
                }
            }

        }
        return super.onStartCommand(intent, flags, startId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotification(): Notification {

        val channel = NotificationChannel(ID, NAME, NotificationManager.IMPORTANCE_DEFAULT)
        channel.enableLights(true)
        channel.setShowBadge(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        channel.enableVibration(true)

        mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setCustomContentView(mRemoteViews)
            .build()
    }

    /**
     * 初始化通知栏通知UI
     */
    private fun initRemoteViews(){
        mRemoteViews = RemoteViews(packageName, R.layout.service_foreground_remoteviews)
        mRemoteViews.setOnClickPendingIntent(R.id.layout_root_foreground_service_notification_remoteView, notificationClickIntent())
        mRemoteViews.setOnClickPendingIntent(R.id.btn_notification_close, closeServiceClickIntent())
        mRemoteViews.setOnClickPendingIntent(R.id.btn_previous, previousClickIntent())
        mRemoteViews.setOnClickPendingIntent(R.id.btn_pause_or_play, pauseServiceClickIntent())
        mRemoteViews.setOnClickPendingIntent(R.id.btn_play, playClickIntent())
        mRemoteViews.setOnClickPendingIntent(R.id.btn_next, nextClickIntent())
    }

    /**
     * 设置通知点击事件
     */
    private fun notificationClickIntent(): PendingIntent {
        return  PendingIntent.getActivity(this, 0, Intent(this, ServiceActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
    }

    /**
     * 设置关闭点击事件
     */
    private fun closeServiceClickIntent(): PendingIntent {
        val intent = Intent(this, this.javaClass)
        intent.action = INTENT_ACTION_CLOSE_EVENT
        return PendingIntent.getService(this, CLOSE_CLICK_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    /**
     * 设置上一首点击事件
     */
    private fun previousClickIntent(): PendingIntent {
        val intent = Intent(this, this.javaClass)
        intent.action = INTENT_ACTION_PREVIOUS
        return PendingIntent.getService(this, CLOSE_CLICK_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    /**
     * 设置暂停点击事件
     */
    private fun pauseServiceClickIntent(): PendingIntent {
        val intent = Intent(this, this.javaClass)
        intent.action = INTENT_ACTION_PAUSE
        return PendingIntent.getService(this, CLOSE_CLICK_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    /**
     * 设置播放点击事件
     */
    private fun playClickIntent(): PendingIntent {
        val intent = Intent(this, this.javaClass)
        intent.action = INTENT_ACTION_PLAY
        return PendingIntent.getService(this, CLOSE_CLICK_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    /**
     * 设置下一首点击事件
     */
    private fun nextClickIntent(): PendingIntent {
        val intent = Intent(this, this.javaClass)
        intent.action = INTENT_ACTION_NEXT
        return PendingIntent.getService(this, CLOSE_CLICK_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    /**
     * 设置通知清楚事件
     */
    private fun clearNotificationIntent(): PendingIntent {
        val intent = Intent(this, this.javaClass)
        intent.action = INTENT_ACTION_CLEAR_NOTIFICATION
        return PendingIntent.getService(this, CLOSE_CLICK_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.d(TAG, "onBind: ")
        return null
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind: ")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: ")
        super.onDestroy()
    }
}