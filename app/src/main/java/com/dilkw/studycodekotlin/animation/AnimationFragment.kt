package com.dilkw.studycodekotlin.animation

import android.os.Bundle
import android.os.PowerManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dilkw.studycodekotlin.R
import com.dilkw.studycodekotlin.databinding.FragmentAnimationBinding
import com.google.android.material.tabs.TabLayoutMediator


class AnimationFragment() : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var wakeLock: PowerManager.WakeLock

    private lateinit var binding: FragmentAnimationBinding

    private val tabItemTitles: MutableList<String> = mutableListOf("视图动画", "属性动画")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val powerManager = getSystemService<PowerManager>(requireContext(), PowerManager::class.java) as PowerManager?
        wakeLock = powerManager!!.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DilkwApp::WakeLockTag")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_animation, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.animationFragmentViewPager.adapter = object: FragmentStateAdapter(requireActivity()) {
            override fun getItemCount(): Int {
               return 2
            }

            override fun createFragment(position: Int): Fragment {
                return when(position) {
                    0 -> ViewAnimationFragment()
                    1 -> ObjectAnimationFragment()
                    else -> Fragment()
                }
            }
        }
        TabLayoutMediator(binding.animationFragmentTabLayout,binding.animationFragmentViewPager) { tab, position ->
            tab.text = tabItemTitles[position]
        }.attach()
        binding.animationFragmentTabLayout.isTabIndicatorFullWidth = false
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AnimationFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onResume() {
        super.onResume()
        wakeLock.acquire();
    }

    override fun onPause() {
        super.onPause()
        wakeLock.release();
    }
}