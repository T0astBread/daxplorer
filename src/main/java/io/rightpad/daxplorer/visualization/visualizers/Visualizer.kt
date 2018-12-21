package io.rightpad.daxplorer.visualization.visualizers

import io.rightpad.daxplorer.data.features.Feature
import io.rightpad.daxplorer.data.TimeSeriesDataPoint
import io.rightpad.daxplorer.visualization.charts.Chart
import java.time.LocalDateTime

abstract class Visualizer(val name: String) {
    abstract val charts: List<Chart>

    abstract fun visualize(startTimestamp: LocalDateTime, endTimestamp: LocalDateTime)

    override fun toString() = this.name
}