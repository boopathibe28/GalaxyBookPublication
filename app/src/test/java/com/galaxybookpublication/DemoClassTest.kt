package com.galaxybookpublication

import com.galaxybookpublication.demo.DemoClass
import junit.framework.TestCase.assertEquals
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
class DemoClassTest {

    lateinit var demoClass : DemoClass
    @Before
    fun setUp() {
        demoClass = DemoClass()
    }

    @After
    fun tearDown() {

    }

    @Test
    fun `Adding Elements To New ArrayList`() {
        val arrayList = arrayListOf<Int>(1,2,3,4,5,6,7,8,7,8,9,10,47,5,10,20,25,56,88,10)
        assertThat(arrayListOf(1,2,3,4,5,7,8,7,8,10,47,5,10,25,56,88,10), `is`(demoClass.show(arrayList)))
    }

}