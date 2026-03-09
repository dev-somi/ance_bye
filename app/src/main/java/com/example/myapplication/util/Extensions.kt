package com.example.myapplication.util

fun FloatArray.softmax(): FloatArray {
    val max = this.maxOrNull() ?: 0f
    val exps = this.map { kotlin.math.exp((it - max).toDouble()) }
    val sum = exps.sum()
    return exps.map { (it / sum).toFloat() }.toFloatArray()
}
