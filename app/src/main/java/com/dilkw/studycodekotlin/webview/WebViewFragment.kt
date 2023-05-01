package com.dilkw.studycodekotlin.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.dilkw.studycodekotlin.R
import com.dilkw.studycodekotlin.baseviews.BaseFragment
import com.dilkw.studycodekotlin.databinding.FragmentWebViewBinding
import java.lang.Thread.sleep

class WebViewFragment : BaseFragment() {

    private val LOG_TAG = "WebViewFragment"

    private lateinit var binding: FragmentWebViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_web_view, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //val url = "https://wiki.biligame.com/yanyu"
        //val url = "file:///android_asset/index.html"
        val url = "https://baidu.com"

        binding.etUrl.setText(url)
        val webViewClient = WebViewClient()
        binding.webview.isScrollContainer
        binding.webview.webViewClient = webViewClient

        //binding.webview.settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE

        // 允许js调用原生方法
        binding.webview.settings.javaScriptEnabled = true

        /**
         * 添加js所需要的调用原生方法的对象，param1:类对象实例，param2:类对象名称
         * js中调用如下：
         *      window.androidObject.callOfJsMethodReturnValue()
         *      androidObject: 就是param2的值（类对象实例）
         *      callOfJsMethodReturnValue：param1的类中所定义的方法
         */
        binding.webview.addJavascriptInterface(this, "androidObject")

        // webView设置webChromeClient（获取加载url的进度、标题等等）
        binding.webview.webChromeClient = object: WebChromeClient() {
            // 获取 url 加载进度
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                Log.d(LOG_TAG, "onProgressChanged: $newProgress")
                if (newProgress >= 100) {
                    binding.pbUrlLoad.visibility = View.GONE
                } else{
                    binding.pbUrlLoad.visibility = View.VISIBLE
                    binding.pbUrlLoad.progress = newProgress
                }
            }

            // 获取加载的 html 文件标题
            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                binding.tvTitle.text = title
            }
        }

        // 加载url按钮监听事件
        binding.btnLoadUrlStart.setOnClickListener {
            val url = binding.etUrl.text.toString().trim()
            if ("" != url) {
                Log.d(LOG_TAG, " loadUrl: $url")
                binding.webview.loadUrl(url)
            }
        }

        binding.btnCallJsMethod.setOnClickListener {
            binding.webview.loadUrl("javaScript:sum(3, 4)")
            // 原生调用js方法
            // 1.
            binding.webview.evaluateJavascript("sum(3, 4)"
            ) { value ->
                Toast.makeText(requireContext(), "js sum函数返回值：$value", Toast.LENGTH_SHORT).show()
            }

//            // 2.
//            binding.webview.loadUrl("javaScript:sum(3, 4)")
        }

        val handlerThread = object: Handler() {
            override fun handleMessage(msg: Message) {
                if (msg.what == 100) {
                    Log.d(LOG_TAG, "handleMessage: ${msg.data.getString("params1")}")
                }
            }
        }

        val thread = Thread {
            sleep(3000)
            val message = Message()
            val bundle = Bundle()
            bundle.putString("params1", "data")
            message.what = 100
            message.data = bundle
            handlerThread.sendMessage(message)
        }.start()

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WebViewFragment().apply {
                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }

    // js 调用 Android 原生方法
    @JavascriptInterface
    fun callOfJsMethod() {
        Toast.makeText(requireContext(),"原生toast:js 调用 android 原生方法", Toast.LENGTH_SHORT).show()
        Log.d(LOG_TAG, "callOfJsMethod: js 调用 Android 原生方法")
    }

    // js 调用 Android 原生方法，并且有返回值
    @JavascriptInterface
    fun callOfJsMethodReturnValue(): String {
        Toast.makeText(requireContext(),"原生toast:js 调用 android 原生方法，并返回数据到js", Toast.LENGTH_SHORT).show()
        Log.d(LOG_TAG, "callOfJsMethodReturnValue: js 调用 Android 原生方法，并且有返回值")
        return "android 原生方法返回数据"
    }

    override fun onFragmentBackPressed(): Boolean {
        if (binding.webview.canGoBack()) {
            binding.webview.goBack()
            return true
        }
        return super.onFragmentBackPressed()
    }

}

/**
 * 一、js 调用原生方法：
 *      1.通过webView.addJavaScriptInterface("方法所在类的实例", "类实例别名")；
 *          a.调用方法：window."类实例别名"."原生定义的方法"
 *          b.window.androidObject.callOfJsMethodReturnValue()
 *
 *      2.通过js中重定向链接，并且将数据按照一定的格式传给webViewClient中重写的shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?)方法参数中；
 *          a.如：val webViewClient = object: WebViewClient() {
                        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                            request.toString()
                            return super.shouldOverrideUrlLoading(view, request)
                        }
                    }
            b.从request参数中获取到js重定向携带过来的参数（约定好格式），以一定的格式进行解析，在原生中进行调用方法、或者是其他的处理（包括页面跳转等）

 * 二、原生调用js函数
 *      1.通过webView.loadUrl("javaScript:方法名称")，如：
 *          a.无参数： webView.loadUrl("javascript:jsMethod()");
 *          b.有参数： webView.loadUrl("javascript:jsMethod(param)");
 *      2.通过evaluateJavascript("js函数名", ValueCallback实例) 调用js函数
 *
 *      webView.evaluateJavascript(method, new ValueCallback<String>() {
              @Override
              public void onReceiveValue(String value) {
                    //value返回值
              }
          });
 *
 *
 *
 *
 */