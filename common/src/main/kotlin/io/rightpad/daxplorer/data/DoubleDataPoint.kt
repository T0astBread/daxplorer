package io.rightpad.daxplorer.data

import io.rightpad.daxplorer.data.datapoints.absolute.TimeSeriesDataPoint
import java.time.LocalDateTime

class DoubleDataPoint(val featureValue:Double, timestamp: LocalDateTime): TimeSeriesDataPoint(timestamp)
