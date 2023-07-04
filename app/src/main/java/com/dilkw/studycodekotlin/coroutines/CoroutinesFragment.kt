package com.dilkw.studycodekotlin.coroutines

import android.database.DatabaseUtils
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.dilkw.studycodekotlin.R
import com.dilkw.studycodekotlin.databinding.FragmentCoroutinesBinding
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CoroutinesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CoroutinesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val TAG = "CoroutinesFragment"

    private lateinit var binding: FragmentCoroutinesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_coroutines, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnStart.setOnClickListener {
            classUI(true) {
                uiCode1()
                classUI(false) {
                    ioCode1()
                }
                classUI(true) {
                    uiCode2()
                }
            }
        }

        lifecycleScope.launch {  }
    }

    fun ioCode1() {
        Log.d(TAG, "${Thread.currentThread()}:ioCode1")
    }
    fun uiCode1() {
        Log.d(TAG,"${Thread.currentThread()}:uiCode1")
    }

    fun ioCode2() {
        Log.d(TAG,"${Thread.currentThread()}:ioCode2")
    }
    fun uiCode2() {
        Log.d(TAG,"${Thread.currentThread()}:uiCode2")
    }

    fun ioCode3() {
        Log.d(TAG,"${Thread.currentThread()}:ioCode3")
    }
    fun uiCode3() {
        Log.d(TAG,"${Thread.currentThread()}:uiCode3")
    }

    fun classUI(isUIThread: Boolean, block: () -> Unit) {
        thread {
            if (isUIThread) {
                requireActivity().runOnUiThread(block)
            } else {
                block()
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CorountinesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CoroutinesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}