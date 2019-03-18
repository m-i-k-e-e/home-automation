package org.domo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class FanSpeed(val speed: Long, val duration: Long, val path: String) : Parcelable {
    SLOW(0, 0, "/slow"), NORMAL(1, 2000, "/normal"), FAST(2, 1000, "/fast");

    companion object {
        @JvmStatic
        fun fromInt(value: Long): FanSpeed {
            return when (value) {
                0L -> SLOW
                1L -> NORMAL
                else -> FAST
            }
        }
    }

    fun next(): FanSpeed {
        return when (speed) {
            0L -> NORMAL
            1L -> FAST
            else -> SLOW
        }
    }

    fun previous(): FanSpeed {
        return when (speed) {
            0L -> FAST
            1L -> SLOW
            else -> NORMAL
        }
    }
}