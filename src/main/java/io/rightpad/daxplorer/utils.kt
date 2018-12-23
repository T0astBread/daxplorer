package io.rightpad.daxplorer

import java.time.LocalDateTime
import java.time.ZoneOffset

fun LocalDateTime.daysSinceEpoch(zoneOffset: ZoneOffset = ZoneOffset.UTC): Int =
        (this.toEpochSecond(zoneOffset) / 60 / 60 / 24).toInt()

fun Int.asEpochDays(zoneOffset: ZoneOffset = ZoneOffset.UTC): LocalDateTime =
        LocalDateTime.ofEpochSecond(this.toLong() * 60 * 60 * 24, 0, zoneOffset);