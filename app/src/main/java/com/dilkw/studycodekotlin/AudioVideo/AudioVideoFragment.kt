package com.dilkw.studycodekotlin.AudioVideo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata.MediaType
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.dilkw.studycodekotlin.R
import com.dilkw.studycodekotlin.databinding.FragmentAudioVideoBinding
import com.google.android.exoplayer2.util.MimeTypes


class AudioVideoFragment() : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentAudioVideoBinding

    private val tabItemTitles: MutableList<String> = mutableListOf("视图动画", "属性动画")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_audio_video, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val player: Player = ExoPlayer.Builder(this.requireContext()).build()
        binding.pvAudioVedio.player = player
        player.addMediaItem(MediaItem.fromUri("http://10.0.2.2:8080/index.m3u8"))
        player.prepare()
        player.play()
    }

    override fun onResume() {
        super.onResume()
        // 恢复播放
        binding.pvAudioVedio.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.pvAudioVedio.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.pvAudioVedio.player?.release()
        binding.pvAudioVedio.player = null
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AudioVideoFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}