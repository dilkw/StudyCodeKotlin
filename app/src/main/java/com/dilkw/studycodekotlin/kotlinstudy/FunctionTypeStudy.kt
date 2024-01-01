package com.dilkw.studycodekotlin.kotlinstudy


/**
 * 函数类型
 * 使用函数的方式来处理函数的声明
 * 创建函数类型的对象的三种方式：
 *      1.双帽号+函数名（例如：定义了函数 fun a(x: Int) {}  即创建对应的函数类型：::a）
 *      2.lambda表达式
 *      3.匿名函数
 * 下面的例子中会对上述三种方式进行代码实现
 */

// 定义一个函数类型变量，其中 x 为变量名，(Int) -> Int 为变量类型
lateinit var x: (Int) -> Int

// 定义一个变量 y 类型为(Int, Int) -> Int函数类型，并通过lambda表达式进行初始化
var y: (Int, Int) -> Int = { i, s -> s.plus(i) }

// 函数类型调用
fun functionType() {
    println("functionType var x:${x(0)}")
    println("functionType var x:${y(2, 3)}")
}


/**
 * 一、高阶函数：函数作为参数或者是返回值的函数称为高阶函数
 */

// 定义一个名为higherOrderFunction，参数名为x，参数类型为()->Int的无返回值的函数
fun higherOrderFunction1(x: () -> Int) {
    println("fun test1 return:${x()}")
}

// 定义一个名为higherOrderFunction2，返回值类型为(Int)->Int的函数
fun higherOrderFunction2(): (Int) -> Int {
    // 这里套用函数类型的两种实例化方式（lambda表达式和匿名函数）

    //return { x ->
    //    println("$x")
    //    x + 2
    //}
    return fun(x: Int): Int {
        return x + 2
    }
}

/**
 * lambda 表达式
 * 1.返回值无需也不能使用return返回值，默认将最后一行作为返回值
 * 2.表达式体中参数的类型可以隐藏
 * 3.当lambda表达式作为最后一个参数传递时，可以将表达式体写在参数括号外
 * lambda 表达式总是括在花括号中， 完整语法形式的参数声明放在花括号内，并有可选的类型标注， 函数体跟在一个 -> 符号之后。如果推断出的该 lambda 的返回类型不是 Unit，那么该 lambda 主体中的最后一个（或可能是单个） 表达式会视为返回值。
 */
// 以下四个变量从功能上来说是一样的，四种不同写法
// 最完整写法
val a: (Int) -> Int = { x: Int -> x }
// lambda表达式体中参数类型省略写法
val b: (Int) -> Int = { x -> x }
// 变量类型省略写法，更具lambda表达式体推断出变量的类型
val c = { x: Int -> x }
// lambda表达式体省略写法，前提条件是参数只有一个，这个参数将会被默认的it代指
val d: (Int) -> Int = { it }
// 当函数类型为多个参数中的最后一个时，可以将lambda表达式体写到括号外面，如下所示：
fun lambdaVar1(x: Int, y: (Int, Int) -> Int): Unit {
    println("lambdaVar: ${x + y(x, x)}")
}
fun lambdaFunction() {
    // 调用最后一个参数为函数类型的lambdaVar1(x: Int, y: (Int, Int) -> Int)函数
    lambdaVar1(0) {
        // 最后一个参数（函数类型）写道括号外面
        x, y ->
            println("${x + y}")
            // 最后一行视为返回值
            x + y
    }
}

// 模仿点击事件，
//      1.在java中通常是通过定义一个接口（如OnClickListener），在接口中定义一个方法(onClick(View view))，
//      2.在需要触发该接口方法的位置中定义一个该接口类型的变量mOnClickListener，并在对应的位置触发onClick方法(mOnClickListener.onClick(view))
//      3.在需要重写方法逻辑的地方创建该接口的实例化对象
//      OnClickListener onClickListener = new OnClickListener() {
//          @Override
//          public void onClick(View v) {
//              对应回调逻辑
//          }
//      }，
//      4.并将其赋值给变量mOnClickListener，如（view.setOnClickListener(onClickListener)）
//      5.而setOnClickListener(OnClickListener： onClickListener) {
//          mOnClickListener = onClickListener
//      }
//      方法中逻辑就是对mOnClickListener赋值
// 以下时通过kotlin的函数类型方式进行点击事件回调的编写
class MyClickListener {
    // 定义函数类型变量
    var onClick: (view: Int) -> Unit = { x -> println("MyClickListener onClick $x") }

    // 定义给onClick变量赋值
    fun setOnClickListener(onClick: (view: Int) -> Unit) {
        this.onClick = onClick
        println("setOnClickListener")
    }

    // 模拟点击事件触发回调onClick(view：Int) -> Unit函数类型
    fun useOnclickFunction() {
        println("useOnclickFunction")
        onClick(0)
    }
}

fun clickListenerTest() {
    val myClickListener = MyClickListener()
    // 可以通过函数引用对函数进行函数类型的实例化
    (::lambdaVar1)(0, fun(x: Int, y: Int): Int {
        return x + y
    })

    // 方式一：可以通过函数引用对已声明的函数进行函数类型的实例化
    val onClick2 = myClickListener::setOnClickListener
    onClick2 { x -> run { println("setOnClickListener回调逻辑：参数为$x") } }

    // 方式二：常规方式
    myClickListener.setOnClickListener { x: Int ->
        println("setOnClickListener回调逻辑：参数为$x")
    }

    // 方式三：当函数类型中的参数只有一个时，可以省略不写，默认用it指代
    myClickListener.setOnClickListener {
        println("setOnClickListener回调逻辑：参数为$it")
    }

    // 通过以下两种方式调用
    // 方式一：函数类型实例化，双冒号+函数名，赋值给函数类型变量click，通过click()调用
    val click = myClickListener::useOnclickFunction
    click()
    // 方式二：常规调用方式
    myClickListener.useOnclickFunction()
}

/**
 * 匿名函数
 * 本质上是一个函数类型的对象，与lambda表达式、双冒号+函数名，这三者是同一种类型，都是函数类型
 */
// 定义一个a1变量，通过匿名函数的方式进行赋值，所以编译器会将其推断为函数类型变量，即为：(Int) -> Int 类型
val a1 = fun(x: Int): Int {
    return x + 2
}

fun <T> returnT(): T {
    return 0 as T
}
fun <T> re(a: T.() -> T): T {
    return returnT()
}

/**
 * 主函数调用测试上述定义的变量以及函数
 */
fun main() {
    /**
     * lambda 表达式
     */
    lambdaVar1(2) { _, _ -> 9 }


    /**
     * 对定义的 x 变量进行初始化赋值（即实例化函数类型），有以下两种方式：
     * 1.通过 lambda表达式
     * 2.通过匿名函数方式
     */

    // lambda 方式进行初始化赋值
    x = { x -> x + 2 }
    // 通过匿名函数方式进行初始化
    x = fun(x: Int): Int { return x + 0 }
    functionType()

    clickListenerTest()
}

