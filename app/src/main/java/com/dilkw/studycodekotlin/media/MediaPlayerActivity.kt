package com.dilkw.studycodekotlin.media

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.ComponentName
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dilkw.studycodekotlin.R
import com.google.android.material.button.MaterialButton


class MediaPlayerActivity : AppCompatActivity() {

    private lateinit var mMediaBrowser: MediaBrowserCompat

    private lateinit var mMediaControllerCompatCallback: MediaControllerCompat.Callback

    private lateinit var mConnectionCallbacks: MediaBrowserCompat.ConnectionCallback

    /**
     * mImgSongBg:          音乐背景图
     * mTvTitle:            音乐名称
     * mBtnPrevious:        上一首按钮
     * mBtnPlayOrPause:     播放或者暂停按钮
     * mBtnNext:            下一首按钮
     * mBtnRepeatMode:      循环模式
     * mSeekBarMedia:       歌曲进度条
     * mProgressAnimator:   进度条属性动画
     */
    private lateinit var mImgSongBg: ImageView
    private lateinit var mTvTitle: TextView
    private lateinit var mBtnPrevious: ImageView
    private lateinit var mBtnPlayOrPause: ImageView
    private lateinit var mBtnNext: ImageView
    private lateinit var mBtnRepeatMode: ImageView
    private lateinit var mSeekBarMedia: SeekBar
    private var mProgressAnimator: ObjectAnimator? = null

    private lateinit var mMediaController: MediaControllerCompat

    private val TAG = "MediaPlayerActivity"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_player)
        Log.d(TAG, "onCreate: ")
        varInit()
        mMediaBrowser = MediaBrowserCompat(
            this,
            ComponentName(this, MediaPlaybackService::class.java),
            mConnectionCallbacks,
            null // optional Bundle
        )

        val btnStartForegroundService = findViewById<MaterialButton>(R.id.btn_start_media_service)
        btnStartForegroundService.setOnClickListener {
            val intent = Intent(this@MediaPlayerActivity, MediaPlaybackService::class.java)
            ContextCompat.startForegroundService(this@MediaPlayerActivity, intent)
            mMediaController.transportControls.play()
        }

    }

    /**
     * 变量初始化
     */
    private fun varInit() {
        mImgSongBg = findViewById(R.id.img_song_bg)
        mTvTitle = findViewById(R.id.tv_title)
        mSeekBarMedia = findViewById(R.id.seek_bar_media)


        mMediaControllerCompatCallback = object: MediaControllerCompat.Callback() {
            override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                if (metadata != null) {
                    Log.d(TAG, "onMetadataChanged: ${metadata.mediaMetadata}")
                    val bitmap = metadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART)
                    mImgSongBg.setImageBitmap(bitmap)
                    mTvTitle.text = metadata.description.title
                    mSeekBarMedia.max = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION).toInt()
                }
            }

            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                Log.d(TAG, "onPlaybackStateChanged: $state")
                if (state != null) {
                    if (state.state == PlaybackStateCompat.STATE_PAUSED
                        || state.state == PlaybackStateCompat.STATE_STOPPED) {
                        mBtnPlayOrPause.setImageResource(R.drawable.ic_foreground_service_play)
                    } else if (state.state == PlaybackStateCompat.STATE_PLAYING) {
                        // 更新 播放/暂停 按钮图标
                        mBtnPlayOrPause.setImageResource(R.drawable.ic_foreground_service_pause)
                    }
                    val currentPosition = state.extras?.getInt("CURRENT_POSITION", 0)
                    val duration = state.extras?.getInt("DURATION", 0)

                    Log.d(TAG, "onPlaybackStateChanged: currentPosition: $currentPosition \n   duration: $duration")
                    if (currentPosition != null && duration != null) {
                        mSeekBarMedia.max = duration
                        handleSeekBar(currentPosition, duration, false)
                    }

                }
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                Log.d(TAG, "onRepeatModeChanged: ")
            }

            override fun onSessionEvent(event: String?, extras: Bundle?) {
                super.onSessionEvent(event, extras)
            }
        }

        mSeekBarMedia.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    Log.d(TAG, "onProgressChanged: $progress")
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    if (mProgressAnimator?.isStarted == true) {
                        mProgressAnimator!!.cancel()
                    }
                    Log.d(TAG, "onStartTrackingTouch: ${seekBar.progress.toLong()}")
                }

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    Log.d(TAG, "onStopTrackingTouch: ${seekBar.progress.toLong()}")
                    mMediaController.transportControls.seekTo(seekBar.progress.toLong())
                    handleSeekBar(seekBar.progress, mSeekBarMedia.max, true)
                }
            }

        })

        mConnectionCallbacks = object: MediaBrowserCompat.ConnectionCallback() {
            override fun onConnected() {
                Log.d(TAG, "onConnected: ")

                // Get the token for the MediaSession
                mMediaBrowser.sessionToken.also { token ->

                    // Create a MediaControllerCompat
                    mMediaController = MediaControllerCompat(
                        this@MediaPlayerActivity, // Context
                        token
                    )
                    // 初始化循环模式未列表循环
                    mMediaController.transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE)

                    // Register a Callback to stay in sync
                    mMediaController.registerCallback(mMediaControllerCompatCallback)

                    // Save the controller
                    MediaControllerCompat.setMediaController(this@MediaPlayerActivity, mMediaController)
                }

                // 获取数据列表
                mMediaBrowser.subscribe(mMediaBrowser.root, object: MediaBrowserCompat.SubscriptionCallback() {
                    override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
                        Log.d(TAG, "onChildrenLoaded: $children")
                        for (mediaItem in children) {
                            mMediaController.addQueueItem(mediaItem.description)
                        }
                        mMediaController.transportControls.prepare()
                    }
                })

                // Finish building the UI
                buildTransportControls()
            }

            override fun onConnectionSuspended() {
                Log.d(TAG, "onConnectionSuspended: ")
                // The Service has crashed. Disable transport controls until it automatically reconnects
            }

            override fun onConnectionFailed() {
                Log.d(TAG, "onConnectionFailed: ")
                // The Service has refused our connection
            }
        }
    }


    /**
     * 当播放状态发生变化时，处理动画以及进度条的更新
     */
    private fun handleSeekBar(currentPosition: Int, maxPosition: Int, isStopTrackingTouch: Boolean) {
        if (mProgressAnimator == null) {
            mProgressAnimator = ObjectAnimator.ofInt(mSeekBarMedia, "progress", currentPosition, maxPosition)
            mProgressAnimator!!.interpolator = LinearInterpolator()
        }

        if (mMediaController.playbackState.state == PlaybackStateCompat.STATE_PLAYING) {
            mProgressAnimator!!.setIntValues(currentPosition, maxPosition)
            mProgressAnimator!!.duration = (maxPosition - currentPosition).toLong()
            mProgressAnimator!!.start()
        }else {
            mProgressAnimator!!.cancel()
            mProgressAnimator!!.currentPlayTime
        }
    }

    /**
     * 处理播放控制器
     */
    fun buildTransportControls() {
        // 对 （播放/暂停） 按钮设置监听事件
        mBtnPlayOrPause = findViewById<ImageView>(R.id.btn_play_or_pause).apply {
            setOnClickListener {
                val pbState = mMediaController.playbackState.state
                val isPlaying = handleMediaController(pbState)
                (it as ImageView).setImageResource(if (isPlaying) R.drawable.ic_foreground_service_pause else R.drawable.ic_foreground_service_play)
            }
        }

        // 对 ”上一首“ 按钮设置监听事件
        mBtnPrevious = findViewById<ImageView>(R.id.btn_previous).apply {
            setOnClickListener {
                Log.d(TAG, "previous: ")
                mMediaController.transportControls.skipToPrevious()
            }
        }

        // 对 ”下一首“ 按钮设置监听事件
        mBtnNext = findViewById<ImageView>(R.id.btn_next).apply {
            setOnClickListener {
                Log.d(TAG, "next: ")
                mMediaController.transportControls.skipToNext()
            }
        }

        // 设置循环播放模式
        mBtnRepeatMode = findViewById<ImageView?>(R.id.btn_repeat_mode).apply {
            setImageResource(R.drawable.ic_media_repeat_mode)
            setOnClickListener {
                Log.d(TAG, "当前循环模式: ${mMediaController.repeatMode}")
                mMediaController.transportControls.setRepeatMode(
                    when(mMediaController.repeatMode) {
                        PlaybackStateCompat.REPEAT_MODE_NONE -> {
                            this.setImageResource(R.drawable.ic_repeat_mode_one)
                            Toast.makeText(this@MediaPlayerActivity, "单曲循环", Toast.LENGTH_SHORT).show()
                            PlaybackStateCompat.REPEAT_MODE_ONE
                        }
                        PlaybackStateCompat.REPEAT_MODE_ONE -> {
                            this.setImageResource(R.drawable.ic_repeat_mode_group)
                            Toast.makeText(this@MediaPlayerActivity, "列表循环", Toast.LENGTH_SHORT).show()
                            PlaybackStateCompat.REPEAT_MODE_ALL
                        }
                        else -> {
                            this.setImageResource(R.drawable.ic_media_repeat_mode)
                            Toast.makeText(this@MediaPlayerActivity, "列表不循环", Toast.LENGTH_SHORT).show()
                            PlaybackStateCompat.REPEAT_MODE_NONE
                        }
                    })
                Log.d(TAG, "更新循环模式: ${mMediaController.repeatMode}")
            }
        }

    }

    /**
     * 更新播放器状态
     * @param playbackState: 状态信息
     * @return 是否为播放状态
     */
    private fun handleMediaController(playbackState: Int): Boolean {
        return when (playbackState) {
            PlaybackStateCompat.STATE_PLAYING -> {
                mMediaController.transportControls.pause()
                false
            }
            PlaybackStateCompat.STATE_PAUSED, PlaybackStateCompat.STATE_NONE -> {
                mMediaController.transportControls.play()
                true
            }
            else -> {
                false
            }
        }

    }

    public override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
        mMediaBrowser.connect()
    }

    public override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        volumeControlStream = AudioManager.STREAM_MUSIC
    }

    public override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
        // (see "stay in sync with the MediaSession")
        MediaControllerCompat.getMediaController(this)?.unregisterCallback(mMediaControllerCompatCallback)
        mMediaBrowser.disconnect()
    }
}