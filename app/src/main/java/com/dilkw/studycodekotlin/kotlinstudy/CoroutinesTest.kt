package com.dilkw.studycodekotlin.kotlinstudy

import kotlinx.coroutines.*

const val TAG = "Coroutines Test"

fun main() {
    runBlocking(CoroutineName("coroutine-1") + Dispatchers.Default) {
        launch {
            delay(200L)
            println("Task from runBlocking")
            val a = async { a() }
            val b = async { b() }
            println("coroutine name:${this.coroutineContext[CoroutineName]} val a : ${a.await()}   val b: ${b.await()}")
        }
        coroutineScope { // 创建一个协程作用域
            launch {
                delay(500L)
                println("Task from nested launch")
            }
            delay(1000L)
            println("Task from coroutine scope") // 这一行会在内嵌 launch 之前输出
        }
        GlobalScope.launch {
            println("Coroutine scope is GlobalScope")
        }
    }
    println("Coroutine scope is over")
}

suspend fun a(): Int {
    withContext(CoroutineName("coroutine-2") + Dispatchers.IO) {
        repeat(100) {
            println("coroutine name:${this.coroutineContext[CoroutineName]}   fun a : $it")
        }
    }
    return 1
}

suspend fun b(): Int {
    withContext(CoroutineName("coroutine-3") + Dispatchers.IO) {
        repeat(100) {
            println("coroutine name:${this.coroutineContext[CoroutineName]}   fun b : $it")
        }
    }
    return 2
}


