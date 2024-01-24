package com.galaxybookpublication.demo

class DemoClass {
    fun show(arrayList: ArrayList<Int>): MutableList<Int> {
        val arrayListTwo: MutableList<Int> = ArrayList<Int>()
        for (i in arrayList.indices) {
            if (i == 0 ||  i % 5 != 0) {
                arrayListTwo.add(arrayList[i])
            }
        }
        return arrayListTwo
    }
}


fun main() {

    val arrayList = arrayListOf(1, 2, 3, 4, 5, 6, 7, 8)
    val planet = DemoClass().show(arrayList)
    println(planet)
}