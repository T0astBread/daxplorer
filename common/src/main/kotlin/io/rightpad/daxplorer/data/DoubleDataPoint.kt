package io.rightpad.daxplorer.data

import java.time.LocalDateTime

class DoubleDataPoint( val featureValue:Double, timestamp: LocalDateTime): TimeSeriesDataPoint(timestamp) {

}