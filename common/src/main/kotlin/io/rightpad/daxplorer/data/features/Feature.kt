package io.rightpad.daxplorer.data.features

import io.rightpad.daxplorer.data.datapoints.absolute.IndexDataPoint
import io.rightpad.daxplorer.data.datapoints.absolute.TimeSeriesDataPoint

interface Feature<T : TimeSeriesDataPoint> {
    var featureData: List<T>?

    fun calculate(indexData: List<IndexDataPoint>)
}