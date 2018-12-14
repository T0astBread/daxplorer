package io.rightpad.daxplorer.visualization.visualizers

import io.rightpad.daxplorer.data.Feature
import io.rightpad.daxplorer.data.TimeSeriesDataPoint
import io.rightpad.daxplorer.visualization.charts.Chart
import java.time.LocalDateTime

abstract class Visualizer<T : TimeSeriesDataPoint>(
        val name: String,
        var feature: Feature<T>
) {
    abstract val charts: List<Chart>

    abstract fun visualize(startTimestamp: LocalDateTime, endTimestamp: LocalDateTime)
}