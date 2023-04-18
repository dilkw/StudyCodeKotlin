package com.dilkw.studycodekotlin

fun main() {
    val str1: String = String(CharArray(5, {
        'a'
    }))
    val str2 = "a"
    val str3 = "a"
    print(str1.equals(str2))
    print(str1 == str2)
    print(str2.equals(str3))
    print(str2 == str3)
}