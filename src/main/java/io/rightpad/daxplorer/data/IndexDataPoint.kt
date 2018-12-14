package io.rightpad.daxplorer.data

import java.time.LocalDateTime

class IndexDataPoint(
        timestamp: LocalDateTime,
        val start: Float,
        val end: Float,
        val min: Float,
        val max: Float,
        val volume: Int,
        val trend: Byte
) : TimeSeriesDataPoint(timestamp)