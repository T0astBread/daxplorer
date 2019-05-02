package io.rightpad.daxplorer.data.datapoints.relative

import io.rightpad.daxplorer.data.datapoints.absolute.TimeSeriesDataPoint
import java.time.LocalDateTime

class RelativeIndexDataPoint(
        timestamp: LocalDateTime,
        val start: Float,
        val end: Float,
        val min: Float,
        val max: Float,
        val volume: Float,
        var trend: Byte
) : TimeSeriesDataPoint(timestamp)
