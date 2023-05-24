package com.dilkw.studycodekotlin

enum class ItemEnum(code: Int, itemName: String) {

    ANIMATION(100, "Animation(动画)"),
    SERVICES(101, "Service(四大组件之服务)"),
    WEB_VIEW(102, "WebView"),
    SYSTEM_BARS(103, "SystemBars"),
    SERVICE(104, "Service"),
    MEDIA(105, "media音频"),
    API(106, "网络库应用"),
    CUSTOMIZE_VIEW(107, "自定义View");

    var code: Int
    var itemName: String

    init {
        this.code = code
        this.itemName = itemName
    }

}