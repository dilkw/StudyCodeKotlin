package com.dilkw.studycodekotlin.media

import android.animation.ObjectAnimator
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.KeyEvent
import android.view.animation.LinearInterpolator
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media.app.NotificationCompat.MediaStyle
import androidx.media.session.MediaButtonReceiver
import com.dilkw.studycodekotlin.R
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * 音视频学习
 */
private const val TAG = "MediaPlaybackService"
private const val MY_MEDIA_ROOT_ID = "media_root_id"
private const val MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id"
private const val NOTIFICATION_ID = 412
private const val CHANNEL_ID = "com.dilkw.studycodekotlin.media.MediaPlaybackService"
private const val NAME = "music_player"

class MediaPlaybackService : MediaBrowserServiceCompat() {

    private lateinit var mMediaSession: MediaSessionCompat

    private lateinit var mStateBuilder: PlaybackStateCompat.Builder

    private var isPlayForegroundServiceActive = false

    private lateinit var mNotificationManager: NotificationManager

    private var mMediaPlayer: MediaPlayer? = null

    private lateinit var mOnCompletionListener: MediaPlayer.OnCompletionListener

    // notification actions
    private lateinit var mPrevAction: NotificationCompat.Action
    private lateinit var mPauseAction: NotificationCompat.Action
    private lateinit var mPlayAction: NotificationCompat.Action
    private lateinit var mNextAction: NotificationCompat.Action

    override fun onCreate() {
        Log.d(TAG, "onCreate: ")
        super.onCreate()
        initNotificationManagerAndAction()

        initMediaPlayer()

        //(getSystemService(Context.AUDIO_SERVICE) as AudioManager).requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        mMediaSession = MediaSessionCompat(this, TAG).apply {

            // Enable callbacks from MediaButtons and TransportControls
            setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                        or MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS
                        or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )

            // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
            mStateBuilder = PlaybackStateCompat.Builder().setActions(
                PlaybackStateCompat.ACTION_PLAY
                        or PlaybackStateCompat.ACTION_PLAY_PAUSE
            )
            mStateBuilder.setState(
                PlaybackStateCompat.STATE_PAUSED,
                0,
                1.0f,
                SystemClock.elapsedRealtime()
            )
            setPlaybackState(mStateBuilder.build())

            if (!isActive) {
                isActive = true
            }
            val sessionCallback: MediaSessionCompat.Callback =
                object : MediaSessionCompat.Callback() {
                    private val mPlaylist: MutableList<MediaSessionCompat.QueueItem> =
                        ArrayList<MediaSessionCompat.QueueItem>()
                    private var mQueueIndex = -1
                    private var mPreparedMedia: MediaMetadataCompat? = null

                    // 播放回调
                    override fun onPlay() {
                        Log.d(TAG, "sessionCallback: onPlay")
                        if (mPreparedMedia == null) {
                            onPrepare()
                        }

                        // 更新播放器状态为 STATE_PLAYING
                        val stateCompat = setNewState(PlaybackStateCompat.STATE_PLAYING)
                        mMediaPlayer!!.start()
                        val notification = handleMediaNotification(stateCompat).build()
                        // 判断当前服务是否已经创建
                        if (!isPlayForegroundServiceActive) {
                            val intent =
                                Intent(this@MediaPlaybackService, MediaPlaybackService::class.java)
                            ContextCompat.startForegroundService(this@MediaPlaybackService, intent)
                            // 更新服务存活标志
                            isPlayForegroundServiceActive = true
                        }
                        startForeground(NOTIFICATION_ID, notification)
                    }

                    // 暂停回调
                    override fun onPause() {
                        Log.d(TAG, "sessionCallback: onPause")

                        mMediaPlayer!!.pause()
                        // 更新播放器状态为 STATE_PAUSED
                        val stateCompat =  setNewState(PlaybackStateCompat.STATE_PAUSED)
                        val notification = handleMediaNotification(stateCompat).build()
                        // 停止服务
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            this@MediaPlaybackService.stopForeground(STOP_FOREGROUND_DETACH)
                        } else {
                            this@MediaPlaybackService.stopForeground(false)
                        }
                        mNotificationManager.notify(NOTIFICATION_ID, notification)
                    }

                    // 结束回调
                    override fun onStop() {
                        Log.d(TAG, "sessionCallback: onStop")

                        mMediaPlayer!!.stop()
                        // 更新播放器状态为 STATE_STOPPED
                        setNewState(PlaybackStateCompat.STATE_STOPPED)
                        // 更新服务存活标志
                        isPlayForegroundServiceActive = false

                        // 停止前台服务
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            this@MediaPlaybackService.stopForeground(STOP_FOREGROUND_DETACH)
                        } else {
                            this@MediaPlaybackService.stopForeground(false)
                        }
                        stopSelf()
                    }

                    // 当UI方调用MediaController.addQueueItem(mediaItem.description)时回调该方法
                    override fun onAddQueueItem(description: MediaDescriptionCompat?) {
                        Log.d(TAG, "onAddQueueItem: ")
                        mPlaylist.add(MediaSessionCompat.QueueItem(description, description.hashCode().toLong()))
                        mQueueIndex = if (mQueueIndex == -1) 0 else mQueueIndex
                        setQueue(mPlaylist)
                    }

                    // 当UI方调用MediaController.removeQueueItem()时回调该方法
                    override fun onRemoveQueueItem(description: MediaDescriptionCompat?) {
                        Log.d(TAG, "sessionCallback: onRemoveQueueItem")
                        mPlaylist.remove(MediaSessionCompat.QueueItem(description,
                            description.hashCode().toLong()
                        ))
                        mQueueIndex = if (mPlaylist.isEmpty()) -1 else mQueueIndex
                        setQueue(mPlaylist)
                    }

                    // 当UI方调用MediaController.transportControls.prepare()回调该方法
                    override fun onPrepare() {
                        Log.d(TAG, "onPrepare: ")
                        if (mQueueIndex < 0 && mPlaylist.isEmpty()) {
                            // Nothing to play.
                            return
                        }
                        val mediaId: String? = mPlaylist[mQueueIndex].description.mediaId

                        // 当准备播放id和目前正在播放的id一样时则不需要重新播放，只需要将mediaPlayer进度跳转至开头就可以
                        if (mediaId == mPreparedMedia?.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)) {
                            mMediaPlayer?.seekTo(0)
                            return
                        }
                        mPreparedMedia = mediaId?.let { getMetadata(this@MediaPlaybackService, it) }
                        // 注意这里先调用setMetadata，再调用prepareMediaData（该方法有yan），
                        setMetadata(mPreparedMedia)
                        // 根据id获取音频相关信息设置给mediaPlayer
                        prepareMediaData(mediaId)

                        if (!isActive) {
                           isActive = true
                        }
                    }

                    // 当MediaButtonReceiver接收到系统的广播事件，回调该方法
                    override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
                        if (mediaButtonEvent != null) {
                            // 根据SDK版本判断使用哪个方法（在api>=33时使用更安全的方法getParcelableExtra(@Nullable String name, @NonNull Class<T> clazz)）
                            val ke: KeyEvent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT, KeyEvent::class.java)
                            } else {
                                mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT)
                            }
                            if (ke != null) {
                                Log.d(TAG, "onMediaButtonEvent: ${ke.keyCode}")
                            }
                        }
                        return super.onMediaButtonEvent(mediaButtonEvent)
                    }

                    // 跳转到下一首
                    override fun onSkipToNext() {
                        mQueueIndex = (++mQueueIndex % mPlaylist.size)
                        mPreparedMedia = null
                        onPlay()
                    }

                    // 跳转到上一首
                    override fun onSkipToPrevious() {
                        mQueueIndex = if (mQueueIndex > 0) --mQueueIndex else mPlaylist.size - 1
                        mPreparedMedia = null
                        onPlay()
                    }

                    // 根据id跳转到指定Queue中的某一项项播放
                    override fun onSkipToQueueItem(id: Long) {
                        super.onSkipToQueueItem(id)
                        setNewState(PlaybackStateCompat.STATE_SKIPPING_TO_QUEUE_ITEM)
                    }

                    override fun onCustomAction(action: String?, extras: Bundle?) {
                        super.onCustomAction(action, extras)
                        Log.d(TAG, "onCustomAction: $action")
                    }

                    override fun onSetRepeatMode(repeatMode: Int) {
                        setRepeatMode(repeatMode)
                        super.onSetRepeatMode(repeatMode)
                        Log.d(TAG, "onSetRepeatMode: $repeatMode")
                    }

                    override fun onSeekTo(pos: Long) {
                        Log.d(TAG, "onSeekTo: $pos")
                        if(mMediaPlayer?.duration != null ){
                            if(((mMediaPlayer?.duration)?.toLong() ?: 0) == pos) {
                                mOnCompletionListener.onCompletion(mMediaPlayer)
                                return
                            }

                        }
                        mMediaPlayer?.seekTo(pos.toInt())
                    }
                }

            // MySessionCallback() has methods that handle callbacks from a media controller
            setCallback(sessionCallback)

            // Set the session's token so that client activities can communicate with it.
            setSessionToken(sessionToken)
        }
    }


    /**
     * 更具mediaId获取准备播放的数据
     */
    private fun prepareMediaData(mediaId: String?) {
        val assetFileDescriptor: AssetFileDescriptor =
            (this@MediaPlaybackService).applicationContext.assets.openFd(getMusicFilename(mediaId)!!)

        // 注意这里的reset方法调用，更新player的状态，不然调用setDataSource会出现错误
        mMediaPlayer?.reset()
        mMediaPlayer?.setDataSource(
            assetFileDescriptor.fileDescriptor,
            assetFileDescriptor.startOffset,
            assetFileDescriptor.length
        )

        // 是否为单曲循环
        mMediaPlayer!!.isLooping = mIsSingleLoop
        mMediaPlayer?.prepare()
    }

    override fun onStart(intent: Intent?, startId: Int) {
        Log.d(TAG, "onStart: ")
        super.onStart(intent, startId)
    }
    
    private var mState = 0
    private var mIsListLoop = false
    private var mIsSingleLoop = false

    private fun initMediaPlayer() {
        mMediaPlayer = MediaPlayer()

        mOnCompletionListener = MediaPlayer.OnCompletionListener {
            Log.d(TAG, "mediaPlayer onCompletion:")

            when(mMediaSession.controller.repeatMode) {
                PlaybackStateCompat.REPEAT_MODE_ONE -> {
                    mMediaSession.controller.transportControls.seekTo(0)
                    mMediaSession.controller.transportControls.play()
                }
                PlaybackStateCompat.REPEAT_MODE_ALL -> {
                    mMediaSession.controller.transportControls.skipToNext()
                }
                else -> {
                    mMediaSession.controller.transportControls.pause()
                    mMediaSession.controller.transportControls.seekTo(0)
                }
            }
        }
        mMediaPlayer!!.setOnCompletionListener(mOnCompletionListener)
    }

    @get:PlaybackStateCompat.Actions
    private val availableActions: Long
        get() {
            var actions = (PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
                    or PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH
                    or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
            actions = when (mState) {
                PlaybackStateCompat.STATE_STOPPED -> actions or (PlaybackStateCompat.ACTION_PLAY
                        or PlaybackStateCompat.ACTION_PAUSE)
                PlaybackStateCompat.STATE_PLAYING -> actions or (PlaybackStateCompat.ACTION_STOP
                        or PlaybackStateCompat.ACTION_PAUSE
                        or PlaybackStateCompat.ACTION_SEEK_TO)
                PlaybackStateCompat.STATE_PAUSED -> actions or (PlaybackStateCompat.ACTION_PLAY
                        or PlaybackStateCompat.ACTION_STOP)
                else -> actions or (PlaybackStateCompat.ACTION_PLAY
                        or PlaybackStateCompat.ACTION_PLAY_PAUSE
                        or PlaybackStateCompat.ACTION_STOP
                        or PlaybackStateCompat.ACTION_PAUSE)
            }
            return actions
        }

    // This is the main reducer for the player state machine.
    private fun setNewState(@PlaybackStateCompat.State newPlayerState: Int): PlaybackStateCompat {
        mState = newPlayerState

        val stateBuilder = PlaybackStateCompat.Builder()
        stateBuilder.setActions(availableActions)
        mMediaPlayer?.currentPosition?.let {
            stateBuilder.setState(
                mState,
                it.toLong(),
                1.0f,
                SystemClock.elapsedRealtime()
            )
        }

        val dataBundle = Bundle()
        mMediaPlayer?.currentPosition?.let { dataBundle.putInt("CURRENT_POSITION", it) }
        mMediaPlayer?.duration?.let { dataBundle.putInt("DURATION", it) }
        stateBuilder.setExtras(dataBundle)
        val stateCompat = stateBuilder.build()
        mMediaSession.setPlaybackState(stateCompat)

        return stateCompat
    }

    private fun initNotificationManagerAndAction() {
        mNotificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 创建下一首action
        mNextAction = NotificationCompat.Action(
            R.drawable.ic_foreground_service_next,
            getString(R.string.next),
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                this@MediaPlaybackService,
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT
            )
        )

        // 创建上一首action
        mPrevAction = NotificationCompat.Action(
            R.drawable.ic_foreground_service_previous,
            getString(R.string.previous),
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                this@MediaPlaybackService,
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            )
        )

        // 创建播放action
        mPlayAction = NotificationCompat.Action(
            R.drawable.ic_foreground_service_play,
            getString(R.string.play),
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                this@MediaPlaybackService,
                PlaybackStateCompat.ACTION_PLAY
            )
        )

        // 创建暂停action
        mPauseAction = NotificationCompat.Action(
            R.drawable.ic_foreground_service_pause,
            getString(R.string.pause),
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                this@MediaPlaybackService,
                PlaybackStateCompat.ACTION_PAUSE
            )
        )

        // Cancel all notifications to handle the case where the Service was killed and
        // restarted by the system.
        mNotificationManager.cancelAll()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: ")
        MediaButtonReceiver.handleIntent(mMediaSession, intent)
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * 控制对服务的访问
     */
    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        Log.d(TAG, "onGetRoot: ")
        // (Optional) Control the level of access for the specified package name.
        // You'll need to write your own logic to do this.
        return if (allowBrowsing(clientPackageName, clientUid)) {
            // Returns a root ID that clients can use with onLoadChildren() to retrieve
            // the content hierarchy.
            MediaBrowserServiceCompat.BrowserRoot(MY_MEDIA_ROOT_ID, null)
        } else {
            // Clients can connect, but this BrowserRoot is an empty hierachy
            // so onLoadChildren returns nothing. This disables the ability to browse for content.
            MediaBrowserServiceCompat.BrowserRoot(MY_EMPTY_MEDIA_ROOT_ID, null)
        }
    }

    override fun onDestroy() {
        setNewState(PlaybackStateCompat.STATE_STOPPED)
        if (mMediaPlayer != null) {
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
        mMediaSession.release()
    }


    private fun handleMediaNotification(stateCompat: PlaybackStateCompat): NotificationCompat.Builder {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID).apply {

            // 当api >= 26 时，通知栏显示信息需要创建 NotificationChannel
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(CHANNEL_ID, NAME, NotificationManager.IMPORTANCE_LOW)
                channel.enableLights(true)
                channel.setShowBadge(true)
                channel.enableVibration(true)
                //channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                mNotificationManager.createNotificationChannel(channel)
            }

            priority = NotificationCompat.PRIORITY_MAX

            // 设置通知栏信息样式
            setStyle(
                MediaStyle()
                    .setMediaSession(mMediaSession.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
                    // For backwards compatibility with Android L and earlier.
                    .setShowCancelButton(true)
                    .setCancelButtonIntent(
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            this@MediaPlaybackService,
                            PlaybackStateCompat.ACTION_STOP))
            )

            color = ContextCompat.getColor(this@MediaPlaybackService, R.color.notification_bg)
            setSmallIcon(R.drawable.ic_stat_image_audiotrack)
            setContentIntent(createContentIntent())
            setContentTitle("description.title")
            setContentText("description.subtitle")
            setLargeIcon(BitmapFactory.decodeResource(this@MediaPlaybackService.resources, R.drawable.album_youtube_audio_library_rock_2))
            setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this@MediaPlaybackService, PlaybackStateCompat.ACTION_STOP))
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        }

        handleNotificationAction(builder, stateCompat)

        // Display the notification and place the service in the foreground
        return builder
    }

    private fun handleNotificationAction(builder: NotificationCompat.Builder, stateCompat: PlaybackStateCompat) {
        // 添加”上一首“按钮，根据 stateCompat.actions 和 ACTION_SKIP_TO_PREVIOUS 进行"与"运算，判断是否支持”上一首“这个操作命令
        if ((stateCompat.actions and PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS) != 0L) {
            builder.addAction(mPrevAction)
        }

        // 添加”播放“/”暂停“ 按钮
        builder.addAction(if(stateCompat.state == PlaybackStateCompat.STATE_PLAYING) mPauseAction else mPlayAction)

        // 添加”下一首“按钮，根据 stateCompat.actions 和 ACTION_SKIP_TO_NEXT 进行"与"运算，判断是否支持”下一首“这个操作命令
        if (stateCompat.actions and PlaybackStateCompat.ACTION_SKIP_TO_NEXT != 0L) {
            builder.addAction(mNextAction)
        }
    }

    private fun allowBrowsing(clientPackageName: String, clientUid: Int): Boolean {
        return true
    }

    /**
     * 传达内容
     */
    override fun onLoadChildren(
        parentMediaId: String,
        result: Result<List<MediaBrowserCompat.MediaItem>>
    ) {
        Log.d(TAG, "onLoadChildren: $parentMediaId  ${result}")
        result.sendResult(getMediaItems())
    }

    private fun createContentIntent(): PendingIntent? {
        val openUI = Intent(this, MediaPlayerActivity::class.java)
        openUI.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        return PendingIntent.getActivity(
            this,
            501,
            openUI,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
    }

    companion object {

        private val music: TreeMap<String, MediaMetadataCompat> = TreeMap()
        private val musicFilenames = HashMap<String, String>()
        private val albumRes = HashMap<String, Int>()

        init {
            createMediaMetadataCompat(
                "Jazz_In_Paris",
                "Jazz in Paris",
                "Media Right Productions",
                "Jazz & Blues",
                "Jazz",
                103,
                TimeUnit.SECONDS,
                "music/jazz_in_paris.mp3",
                R.drawable.album_jazz_blues,
                "album_jazz_blues"
            )
            createMediaMetadataCompat(
                "The_Coldest_Shoulder",
                "The Coldest Shoulder",
                "The 126ers",
                "Youtube Audio Library Rock 2",
                "Rock",
                160,
                TimeUnit.SECONDS,
                "music/the_coldest_shoulder.mp3",
                R.drawable.album_youtube_audio_library_rock_2,
                "album_youtube_audio_library_rock_2"
            )
        }

        private fun createMediaMetadataCompat(
            mediaId: String,
            title: String,
            artist: String,
            album: String,
            genre: String,
            duration: Long,
            durationUnit: TimeUnit,
            musicFilename: String,
            albumArtResId: Int,
            albumArtResName: String
        ) {
            music[mediaId] = MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                .putLong(
                    MediaMetadataCompat.METADATA_KEY_DURATION,
                    TimeUnit.MILLISECONDS.convert(duration, durationUnit)
                )
                .putString(MediaMetadataCompat.METADATA_KEY_GENRE, genre)
                .putString(
                    MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
                    getAlbumArtUri(
                        albumArtResName
                    )
                )
                .putString(
                    MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI,
                    getAlbumArtUri(
                        albumArtResName
                    )
                )
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                .build()


            albumRes[mediaId] = albumArtResId

            musicFilenames[mediaId] = musicFilename
        }


        private fun getAlbumArtUri(albumArtResName: String): String {
            return ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                    "com.dilkw.studycodekotlin" + "/drawable/" + albumArtResName
        }

        fun getMusicFilename(mediaId: String?): String? {
            return if (musicFilenames.containsKey(
                    mediaId
                )
            ) musicFilenames[mediaId] else null
        }

        private fun getAlbumRes(mediaId: String): Int? {
            return if (albumRes.containsKey(mediaId)) albumRes[mediaId] else 0
        }

        private fun getAlbumBitmap(context: Context, mediaId: String): Bitmap? {
            return getAlbumRes(
                mediaId
            )?.let {
                BitmapFactory.decodeResource(
                    context.resources,
                    it
                )
            }
        }

        fun getMediaItems(): List<MediaBrowserCompat.MediaItem> {
            val result: MutableList<MediaBrowserCompat.MediaItem> = ArrayList()
            for (metadata in music.values) {
                result.add(
                    MediaBrowserCompat.MediaItem(
                        metadata.description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
                    )
                )
            }
            return result
        }

        fun getMetadata(context: Context, mediaId: String): MediaMetadataCompat? {
            val metadataWithoutBitmap: MediaMetadataCompat? =
                music[mediaId]
            val albumArt: Bitmap? =
                getAlbumBitmap(
                    context,
                    mediaId
                )

            // Since MediaMetadataCompat is immutable, we need to create a copy to set the album art.
            // We don't set it initially on all items so that they don't take unnecessary memory.
            val builder = MediaMetadataCompat.Builder()
            for (key in arrayOf(
                MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
                MediaMetadataCompat.METADATA_KEY_ALBUM,
                MediaMetadataCompat.METADATA_KEY_ARTIST,
                MediaMetadataCompat.METADATA_KEY_GENRE,
                MediaMetadataCompat.METADATA_KEY_TITLE
            )) {
                if (metadataWithoutBitmap != null) {
                    builder.putString(key, metadataWithoutBitmap.getString(key))
                }
            }
            if (metadataWithoutBitmap != null) {
                builder.putLong(
                    MediaMetadataCompat.METADATA_KEY_DURATION,
                    metadataWithoutBitmap.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
                )
            }
            builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
            return builder.build()
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

}