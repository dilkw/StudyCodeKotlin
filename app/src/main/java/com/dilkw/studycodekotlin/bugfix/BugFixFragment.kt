package com.dilkw.studycodekotlin.bugfix

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.dilkw.studycodekotlin.R
import com.dilkw.studycodekotlin.databinding.FragmentBugFixBinding
import kotlinx.coroutines.delay
import java.lang.Thread.sleep

/**
 * 背景：当View设置了动画（隐藏动画，即设置visibility值为GONE），
 *      当view的动画为执行完毕时，再次点击View（具体动作为：按下直到动画结束后再抬起，即再动画未结束时触发ACTION_DOWN，持续到动画结束后触发ACTION_UP）
 *      此时出现的现象为在view隐藏之前显示的位置进行点击，view仍然能响应点击事件
 *      具体代码实例如代码所示
 * 结论：当view在执行动画时，若动画未结束对view进行点击，view响应了点击事件并重新设置了animation对象（即对view的mCurrentAnimation重新赋值），
 *      在对ViewGroup的事件分发源码解读过程中发现，当View的mCurrentAnimation不为空时，但view的坐标属性不变（即点击事件坐标在view的范围内），
 *      所以仍然会将事件分发给View，而响应了点击事件后（即在点击事件抬起时触发ACTION_UP才会回调onClick()方法），重新设置了mCurrentAnimation的值
 *      所以ViewGroup中分发事件时，判断view的mCurrentAnimation是否为空时，结果未永不为空，永远响应点击事件，即使view的visibility值为GONE
 *
 * 注意：当动画执行完毕时，mCurrentAnimation 会被清除，置为null
 */
class BugFixFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val TAG = "BugFixFragment"

    private lateinit var binding: FragmentBugFixBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bug_fix, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val thread = object: Thread() {
            override fun run() {
                requireActivity().runOnUiThread {
                    sleep(3000)
                    Log.d(TAG, "run: ")
                    binding.tvMask.animation = AnimationUtils.loadAnimation(requireContext(), R.anim.drogdown_mask_out)
                    this.interrupt()
                }
            }
        }

        var isVisible = true
        binding.tvMask.setOnClickListener {
//            binding.tvMask.visibility = if (isVisible) View.GONE else View.VISIBLE
//            binding.tvMask.animation = if (isVisible )AnimationUtils.loadAnimation(requireContext(), R.anim.drogdown_mask_out) else AnimationUtils.loadAnimation(requireContext(), R.anim.drogdown_mask_in)
//            isVisible = !isVisible

            binding.tvMask.visibility = View.GONE
            binding.tvMask.animation = AnimationUtils.loadAnimation(requireContext(), R.anim.drogdown_mask_out)
            //binding.tvMask.startAnimation(binding.tvMask.animation)
            Log.d(TAG, "bugfix click: ${if (binding.tvMask.animation != null) binding.tvMask.animation.toString() else "animation is null"}")
        }
        binding.tv.setOnClickListener {
            Log.d(TAG, "onViewCreated: tv")
        }
        binding.btnController.setOnClickListener {
            binding.tvMask.visibility = View.VISIBLE
            binding.tvMask.animation = AnimationUtils.loadAnimation(requireContext(), R.anim.drogdown_mask_in)
        }
        
        binding.btnGetAnimation.setOnClickListener {
            //Log.d(TAG, "onViewCreated: ${if (binding.tvMask.animation==null) "animation is null" else "animation notnull"}")
            Toast.makeText(requireContext(), "onViewCreated: ${if (binding.tvMask.animation==null) "animation is null" else "animation notnull"}", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BugFixFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}