package com.dilkw.studycodekotlin.network

import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.dilkw.studycodekotlin.BuildConfig
import com.dilkw.studycodekotlin.R
import com.dilkw.studycodekotlin.databinding.FragmentHttpDemoBinding
import com.dilkw.studycodekotlin.network.dao.RepoReleaseTag
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
        // okHttp请求demo
        okHttpInit()
        // retrofit请求demo
        retrofitInit()
    }

    private fun okHttpInit() {
        /**
         * okhttp
         */
        val client = OkHttpClient.Builder()
//            .cookieJar(object : CookieJar {
//                override fun loadForRequest(url: HttpUrl): List<Cookie> {
//
//                }
//
//                override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
//
//                }
//
//            })
            .build()
        val request = Request.Builder()
            .url("https://www.baidu.com")
            .build()
        binding.tvOkHttpResponse.movementMethod = ScrollingMovementMethod.getInstance()
        binding.btnOkHttpRequest.setOnClickListener {
            client.newCall(request).enqueue(object : Callback {
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
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                        Log.d(
                            LOG_TAG, "onResponse: \n" +
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

    private fun retrofitInit() {
        /**
         * retrofit
         */

        val okHttpClient = OkHttpClient.Builder()
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/repos/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .validateEagerly(BuildConfig.DEBUG) //如果当前构建的包是debug包，则验证请求方法是否正确（内部通过反射验证，比较耗费性能）
            .build()
        val retrofitApi = retrofit.create(RetrofitApi::class.java)

        binding.tvRetrofitResult.movementMethod = ScrollingMovementMethod.getInstance()
        binding.btnRetrofitStart.setOnClickListener {
            getTags(retrofitApi)
        }
    }

    private fun getTags(retrofitApi: RetrofitApi) {
        retrofitApi.getRepoReleaseTags().enqueue(object : retrofit2.Callback<List<RepoReleaseTag>> {
            override fun onResponse(
                call: retrofit2.Call<List<RepoReleaseTag>>,
                response: retrofit2.Response<List<RepoReleaseTag>>
            ) {
                binding.tvRetrofitResult.text = buildString {
                    append("成功：\n")
                    if (response.body()?.isNotEmpty() == true) {
                        response.body()!!.forEach {
                            append("${it.name}\n")
                        }
                    } else {
                        append("无法获取版本号")
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<List<RepoReleaseTag>>, t: Throwable) {
                binding.tvRetrofitResult.text = buildString {
                    append("失败：")
                    append(t.message)
                }
            }
        })
    }

    private fun getRepos(retrofitApi: RetrofitApi) {
        retrofitApi.getInformationFromBaidu().enqueue(object : retrofit2.Callback<List<Repo>> {
            override fun onResponse(
                call: retrofit2.Call<List<Repo>>,
                response: retrofit2.Response<List<Repo>>
            ) {
                binding.tvRetrofitResult.text = buildString {
                    append("成功：")
                    if (response.body()?.isNotEmpty() == true) {
                        append(response.body()?.get(0)?.name ?: "空")
                    } else {
                        append("无法获取版本号")
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<List<Repo>>, t: Throwable) {
                binding.tvRetrofitResult.text = buildString {
                    append("失败：")
                    append(t.message)
                }
            }
        })
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