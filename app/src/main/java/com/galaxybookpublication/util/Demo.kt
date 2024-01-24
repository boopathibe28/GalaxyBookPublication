package com.galaxybookpublication.util

// Online Java Compiler
// Use this editor to write, compile and run your Java code online
// Online Java Compiler
// Use this editor to write, compile and run your Java code online
class Demo {
    fun main() {
        val arr = arrayOf(3, 4, 7, 3, 1, 3).toMutableList()
        val k = 3
        var sum = 0
        val subArray = ArrayList<Int>()
        for (index in arr.indices) {
            subArray.add(arr.subList(index, k).sum())
            arr.removeFirst()
        }
        subArray.min()
    }
}

