package io.rightpad.daxplorer.utils

import java.io.File
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.stream.Stream
import kotlin.math.floor
import kotlin.streams.asStream

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

fun <T> File.useLinesAsStream(consume: (Stream<String>) -> T): T =
        useLines { consume(it.asStream()) }