package io.rightpad.daxplorer.data.features

import io.rightpad.daxplorer.data.IndexDataPoint
import io.rightpad.daxplorer.data.TimeSeriesDataPoint

interface Feature<T : TimeSeriesDataPoint> {
    var featureData: List<T>?

    fun calculate(indexData: List<IndexDataPoint>)
}