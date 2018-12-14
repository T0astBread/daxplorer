package io.rightpad.daxplorer.data

import java.sql.Timestamp
import java.time.LocalDateTime

class IndexDataPoint(
        timestamp: LocalDateTime,
        val start: Float,
        val end: Float,
        val min: Float,
        val max: Float,
        val volume: Int
) : TimeSeriesDataPoint(timestamp)