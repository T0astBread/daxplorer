package io.rightpad.daxplorer.data.features

import io.rightpad.daxplorer.data.IndexDataPoint
import io.rightpad.daxplorer.data.TimeSeriesDataPoint

interface Feature<T : TimeSeriesDataPoint> {
    val featureData: List<T>

    fun calculate(indexData: List<IndexDataPoint>)
}