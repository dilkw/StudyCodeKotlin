package com.dilkw.studycodekotlin.animation

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.dilkw.studycodekotlin.R
import com.dilkw.studycodekotlin.databinding.FragmentObjectAnimationBinding

class ObjectAnimationFragment : Fragment() {

    private val LOG_TAG = "ObjectAnimationFragment"

    private lateinit var binding: FragmentObjectAnimationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_object_animation, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 对官方控件TextView进行动画操作
        var width: Int
        var animatorTextView: ObjectAnimator? = null
        binding.tv.viewTreeObserver.addOnDrawListener {
            width = binding.tv.width
            //Log.d(LOG_TAG, "viewTreeObserver: $width")
            animatorTextView =
                ObjectAnimator.ofInt(binding.tv, "width", 100, 300, binding.tv.width)
            animatorTextView?.duration = 5000
        }

        binding.btnTvAnimationStart.setOnClickListener {
            animatorTextView?.start()
        }

        // 对自定义控件StatisticalChartView进行动画操作
        val animatorStatisticalChartView: ObjectAnimator =
            ObjectAnimator.ofFloat(binding.statisticalChartView, "progress", 0f, 50f)
        //动画每次执行的时间（毫秒数）
        animatorStatisticalChartView.duration = 3000
        //设置动画执行次数（ValueAnimator.INFINITE为无限）
        animatorStatisticalChartView.repeatCount = 0
        //当动画执行次数大于零或是无限（ValueAnimator.INFINITE）时setRepeatMode才有效
        //animator1.setRepeatMode(ValueAnimator.RESTART);
        //设置插值（匀速，创建插值对象）
        animatorStatisticalChartView.interpolator = LinearInterpolator()
        binding.btnStatisticalChartViewAnimationStart.setOnClickListener {
            animatorStatisticalChartView.start()
        }

        // 时钟动画
        val animatorRing: ObjectAnimator = ObjectAnimator.ofInt(binding.ring, "mSeconds", 0, 60)
        //动画每次执行的时间（毫秒数）
        animatorRing.duration = 60000
        //设置动画执行次数（ValueAnimator.INFINITE为无限）
        animatorRing.repeatCount = ValueAnimator.INFINITE
        //当动画执行次数大于零或是无限（ValueAnimator.INFINITE）时setRepeatMode才有效
        animatorRing.repeatMode = ValueAnimator.RESTART
        //设置插值（匀速，创建插值对象）
        animatorRing.interpolator = LinearInterpolator()

        binding.btnRingViewAnimationStart.setOnClickListener {
            // setCurrentFraction 和 currentPlayTime 作用效果一样，只不过setCurrentFraction要求api >= 22
            // animatorRing.setCurrentFraction(binding.ring.mSeconds / 60f)
            animatorRing.currentPlayTime = ((binding.ring.mSeconds / 60f) * animatorRing.duration).toLong()
            animatorRing.start()
        }


        var mIsTracking = false
        binding.seekBar.max = 100000
        val animator: ValueAnimator = ValueAnimator.ofInt(0, 100000)
        animator.duration = 100000
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener {


            // If the user is changing the slider, cancel the animation.
            if (mIsTracking) {
                Log.d(LOG_TAG, "UpdateListener: ${it.animatedValue}")
                Log.d(LOG_TAG, "UpdateListener: ${animator.currentPlayTime}")
                return@addUpdateListener
            }
            binding.seekBar.progress = it.animatedValue as Int
        }

        binding.btnStartStop.setImageResource(R.drawable.ic_foreground_service_play)
        binding.btnStartStop.setOnClickListener {
            if (animator.isStarted && !animator.isPaused) {
                (it as ImageView).setImageResource(R.drawable.ic_foreground_service_play)
                animator.pause()
            }else {
                (it as ImageView).setImageResource(R.drawable.ic_foreground_service_pause)
                if (animator.isStarted && animator.isPaused) {
                    Log.d(LOG_TAG, "resume: ${animator.currentPlayTime}")
                    animator.resume()
                }else {
                    animator.start()
                }
            }
        }
        binding.seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                Log.d(LOG_TAG, "onProgressChanged: $progress")
                if (fromUser) {
                    Log.d(LOG_TAG, "onProgressChanged fromUser: $progress")
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                mIsTracking = true
            }

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                animator.currentPlayTime = seekBar.progress.toLong()
                //animator.setCurrentFraction((seekBar.progress).toFloat() / seekBar.max)
                Log.d(LOG_TAG, "onStopTrackingTouch: ${seekBar.progress}")
                mIsTracking = false
            }

        })
    }

    override fun onResume() {
        super.onResume()
        Log.d(LOG_TAG, "onResume: ${binding.tv.width}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(LOG_TAG, "onDestroy: ")
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ObjectAnimationFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}