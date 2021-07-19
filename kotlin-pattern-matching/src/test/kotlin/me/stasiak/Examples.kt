package me.stasiak

import java.lang.IllegalStateException
import kotlin.test.Test

class Examples {


    // DNA RNA


    data class Time(
        val hour: String,
        val minuts: String,
        val second: String,
    )

    @Test
    fun time() {
        val rawTime = "18:10:22".split(":")
        val time: Time = when (rawTime.size) {
            3 -> Time(rawTime[0], rawTime[1], rawTime[2])
            2 -> Time(rawTime[0], rawTime[1], "00")
            1 -> Time(rawTime[0], "00", "00")
            else -> throw IllegalStateException("Unsupported time format!")
        }

    }

    @Test
    fun time2() {
        val rawTime = "18:10".split(":")
        val time: Time = when (rawTime.size) {
            3 -> Time(rawTime[0], rawTime[1], rawTime[2])
            2 -> Time(rawTime[0], rawTime[1], "00")
            1 -> Time(rawTime[0], "00", "00")
            else -> throw IllegalStateException("Unsupported time format!")
        }

    }

    @Test
    fun time3() {
        val time = "18:10".split(":")
        val obj: Time = when {
            time.size == 3 && time[0].isNotEmpty() -> Time(time[0], time[1], time[2])
            time.size == 2 && time[0].isNotEmpty() -> Time(time[0], time[1], "00")
            time.size == 1 && time[0].isNotEmpty() -> Time(time[0], "00", "00")
            else -> throw IllegalStateException("Unsupported time format!")
        }

    }
}