package com.dilkw.studycodekotlin.flow

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

// flow 底层是通过 kotlin 的协程实现的

fun main() {
    // 定义数据流，通过 flow 操作符
    val flow: Flow<Int> =  flow {
        for (i in 0..5) {
            // 关键操作符，将数据流通过 emit 操作符进行发送
            emit(i)
            delay(2000)
        }
        throw java.lang.RuntimeException()
    }

    runBlocking<Unit> {
        // flow 中的 catch 操作符 lambda表达式中可以向数据流发出数据项即 emit(Int)
        // 通过 collect 操作符对数据流试用 emit 发射出的数据进行收集
        flow.catch {
            e -> e.printStackTrace()
            // 在 catch 中可以向数据流发送数据
            emit(0)
        }.collect {
            println("emit value is : $it")
        }
        println("Done")

        // 而通过 try 和 catch 包裹数据流的收集操作时，无法做到像数据流发送数据的操作
        try {
            flow.collect {
                println("emit value is : $it")
            }
        }catch(e: Throwable) {
            e.printStackTrace()
        }
        println("Done")
    }

}