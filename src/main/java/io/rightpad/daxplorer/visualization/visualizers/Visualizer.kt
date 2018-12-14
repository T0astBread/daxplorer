package io.rightpad.daxplorer.visualization.visualizers

import io.rightpad.daxplorer.data.features.Feature
import io.rightpad.daxplorer.data.TimeSeriesDataPoint
import io.rightpad.daxplorer.visualization.charts.Chart
import java.time.LocalDateTime

abstract class Visualizer<T : TimeSeriesDataPoint>(val name: String) {
    abstract val charts: List<Chart>
    abstract val feature: List<Feature<T>>

    abstract fun visualize(startTimestamp: LocalDateTime, endTimestamp: LocalDateTime)
}