package com.dilkw.studycodekotlin.systemBars

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import android.view.WindowInsetsController.BEHAVIOR_DEFAULT
import androidx.core.view.*
import androidx.databinding.DataBindingUtil
import com.dilkw.studycodekotlin.R
import com.dilkw.studycodekotlin.databinding.FragmentSystemBarsBinding
import com.dilkw.studycodekotlin.util.TypedValueUtil
import com.google.android.material.chip.Chip
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import kotlin.properties.Delegates

class SystemBarsFragment : Fragment() {

    private val TAG = "SystemBarsFragment"

    private lateinit var binding: FragmentSystemBarsBinding

    private var param1: String? = null
    private var param2: String? = null

    private var statusBarDefaultColor by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: ")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_system_bars, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: ")
        binding.layoutRootSystemBar.fitsSystemWindows = true
        val insetsController = WindowCompat.getInsetsController(requireActivity().window, requireView())

        // 根据状态栏的显示状态初始化控制状态栏显示与否的switchStatusBarVisible
        binding.switchStatusBarVisible.isChecked = !requireActivity().window.decorView.fitsSystemWindows
        binding.switchStatusBarVisible.setOnClickListener {
            handleStatusBarsVisibleBySwitch(it, insetsController)
        }

        // 改变状态的颜色（目前通过该系统api的方式仅有两种颜色可以控制）
        binding.switchStatusBarColor.isChecked = insetsController.isAppearanceLightStatusBars
        binding.switchStatusBarColor.setOnClickListener {
            insetsController.isAppearanceLightStatusBars = (it as SwitchMaterial).isChecked
        }

        // 状态栏颜色
        val initStatusBarColor = requireActivity().window.statusBarColor
        binding.chipGroupStatusBarBackground.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.size > 0) {
                val chip = binding.root.findViewById<Chip>(group.checkedChipId)
                val colorInt = binding.root.findViewById<Chip>(group.checkedChipId).chipBackgroundColor?.defaultColor
                if (colorInt != null) {
                    requireActivity().window.statusBarColor = colorInt
                    Log.d(TAG, "onViewCreated: ${colorInt.toUInt().toString(16)}")
                }
            } else {
                requireActivity().window.statusBarColor = initStatusBarColor
            }
        }

        binding.switchNavigationBarVisible.isChecked = true
        binding.switchNavigationBarVisible.setOnClickListener {
            if ((it as SwitchMaterial).isChecked) {
                insetsController.show(WindowInsetsCompat.Type.navigationBars())
            } else {
                //insetsController.hide(WindowInsetsCompat.Type.navigationBars())
                requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN.or(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
            }
        }

        // 导航栏按键是否高亮（默认为不高亮）
        binding.switchNavigationBarColor.isChecked = insetsController.isAppearanceLightNavigationBars
        binding.switchNavigationBarColor.setOnClickListener {
            insetsController.isAppearanceLightNavigationBars = (it as SwitchMaterial).isChecked
        }

        val navigationBackgroundColor = requireActivity().window.navigationBarColor
        binding.switchNavigationBarBackground.setOnClickListener {
            if ((it as SwitchMaterial).isChecked) {
                requireActivity().window.navigationBarColor = 0xFF8B5357.toInt()
            } else {
                requireActivity().window.navigationBarColor = navigationBackgroundColor
            }
        }

        val paddingDefault = TypedValueUtil.dp2px(8)
        statusBarDefaultColor = requireActivity().window.statusBarColor
        binding.switchTranslateStatusBar.setOnClickListener {
            if ((it as SwitchMaterial).isChecked) {
                WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
                requireActivity().window.statusBarColor = Color.TRANSPARENT
                binding.layoutNestedScrollView.updatePadding(paddingDefault , 0, paddingDefault, paddingDefault)
            } else {
                WindowCompat.setDecorFitsSystemWindows(requireActivity().window, true)
                requireActivity().window.statusBarColor = statusBarDefaultColor
                binding.layoutNestedScrollView.updatePadding(0, 0, 0, 0)
                binding.layoutRootSystemBar.updatePadding(paddingDefault, paddingDefault, paddingDefault, paddingDefault)
            }
        }

        val systemUiVisibleValue = requireActivity().window.decorView.systemUiVisibility
        for (i in 0 until SystemUIEnum.values().size) {
            val systemUIEnum = SystemUIEnum.values()[i]
            val itemView = LayoutInflater.from(requireContext()).inflate(R.layout.layout_item_system_ui_visible, null)
            itemView.findViewById<MaterialTextView>(R.id.tv_item_label).text = systemUIEnum.property
            itemView.findViewById<SwitchMaterial>(R.id.switch_item).setOnClickListener {
                if ((it as SwitchMaterial).isChecked) {
                    requireActivity().window.decorView.systemUiVisibility = systemUIEnum.value
                } else {
                    requireActivity().window.decorView.systemUiVisibility = systemUiVisibleValue
                }
            }
            binding.layoutSystemUiVisible.addView(itemView)
        }
    }

    /**
     * 处理statusBar是否显示
     */
    private fun handleStatusBarsVisibleBySwitch(it: View?, insetsController: WindowInsetsControllerCompat) {
        if ((it as SwitchMaterial).isChecked) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                insetsController.show(WindowInsetsCompat.Type.statusBars())
            } else {
                requireActivity().window.decorView.systemUiVisibility = View.STATUS_BAR_VISIBLE
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                insetsController.hide(WindowInsetsCompat.Type.statusBars())
            } else {
                requireActivity().window.decorView.systemUiVisibility = View.STATUS_BAR_HIDDEN
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SystemBarsFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, "onActivityCreated: ")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, true)
        requireActivity().window.statusBarColor = statusBarDefaultColor
    }
}