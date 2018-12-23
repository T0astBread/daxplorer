package io.rightpad.daxplorer

import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.floor

fun LocalDateTime.daysSinceEpoch(zoneOffset: ZoneOffset = ZoneOffset.UTC): Float =
        this.toEpochSecond(zoneOffset) / 60f / 60 / 24

fun Float.asEpochDays(zoneOffset: ZoneOffset = ZoneOffset.UTC): LocalDateTime {
    val epochSecs = this * 60 * 60 * 24
    val epochSecsDecimal = epochSecs - floor(epochSecs)
    val nanoOfEpochSecs = (epochSecsDecimal * 1000000000).toInt()
    return LocalDateTime.ofEpochSecond(epochSecs.toLong(), nanoOfEpochSecs, zoneOffset)
}

fun LocalDateTime?.floor() =
        this?.toLocalDate()?.atStartOfDay()

fun LocalDateTime?.ceil() =
        this?.toLocalDate()?.plusDays(1)?.atStartOfDay()