package com.dilkw.studycodekotlin.api

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.dilkw.studycodekotlin.R
import com.dilkw.studycodekotlin.databinding.FragmentHttpDemoBinding
import okhttp3.*
import java.io.IOException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val LOG_TAG = "HttpDemoFragment"

/**
 * A simple [Fragment] subclass.
 * Use the [HttpDemoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HttpDemoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentHttpDemoBinding

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
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_http_demo, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val client = OkHttpClient.Builder().build()
        val request = Request.Builder()
            .url("https://www.baidu.com")
            .build()
        binding.tvOkHttpResponse.movementMethod = ScrollingMovementMethod.getInstance()
        binding.btnOkHttpRequest.setOnClickListener {
            client.newCall(request).enqueue(object:Callback{
                override fun onFailure(call: Call, e: IOException) {
                    requireActivity().runOnUiThread {
                        binding.tvOkHttpResponse.text = e.message
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    // 以流方式读取数据
                    val reader = response.body?.byteStream()?.bufferedReader()
                    val stringBuilder = StringBuilder()
                    if (reader != null) {
                        try {
                            while (reader.readLine() != null) {
                                stringBuilder.append(reader.readLine())
                            }
                        }catch (e: IOException) {
                            e.printStackTrace()
                        }

                        Log.d(LOG_TAG, "onResponse: \n" +
                                "${response.code}\n " +
                                "message:${response.message}\n"
                                + stringBuilder
                        )

                        requireActivity().runOnUiThread {
                            binding.tvOkHttpResponse.text = stringBuilder
                        }
                    }

                }

            })
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HttpDemoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HttpDemoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}