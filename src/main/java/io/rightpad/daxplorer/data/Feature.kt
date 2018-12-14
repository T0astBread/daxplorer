package io.rightpad.daxplorer.data

interface Feature<T : TimeSeriesDataPoint> {
    val featureData: List<T>

    fun calculate(indexData: List<IndexDataPoint>)
}