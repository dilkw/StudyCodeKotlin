function sum(a, b) {
    let r = a + b;
    window.alert('js sum方法被调用，结果值为：' + r)
    return r.toString(); // make sure we return String instance
};

function androidCallJsMethod() {
    window.alert("js弹窗：安卓原生调用js方法")
};

function jsCallAndroidMethod() {
    window.androidObject.callOfJsMethod()
};

function jsCallAndroidMethodReturnValue() {
    let value = window.androidObject.callOfJsMethodReturnValue()
    document.getElementById('returnValue').innerHTML = value;
};