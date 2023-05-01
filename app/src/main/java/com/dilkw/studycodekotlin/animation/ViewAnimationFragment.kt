package com.dilkw.studycodekotlin.animation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.databinding.DataBindingUtil
import com.dilkw.studycodekotlin.R
import com.dilkw.studycodekotlin.databinding.FragmentViewAnimationBinding

class ViewAnimationFragment : Fragment() {

    private val LOG_TAG = "ViewAnimationFragment"

    private lateinit var binding: FragmentViewAnimationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_animation, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnAnimationStart.setOnClickListener {
            when(binding.chipGroup.checkedChipId) {
                R.id.chip_alpha -> binding.tv.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.view_animation_alpha))
                R.id.chip_scale -> binding.tv.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.view_animation_scale))
                R.id.chip_translate -> binding.tv.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.view_animation_translate))
                R.id.chip_rotate -> binding.tv.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.view_animation_rotate))
                R.id.chip_set -> binding.tv.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.view_animation_set))
            }
        }

        // 也可通过animate函数进行定义
        binding.tv.animate()
            .alpha(0f)
            .alpha(1f)
            .setInterpolator(LinearInterpolator())
            .duration = 3000
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ViewAnimationFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}