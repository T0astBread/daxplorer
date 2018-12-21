package io.rightpad.daxplorer.visualization.visualizers

import io.rightpad.daxplorer.daysSinceEpoch
import io.rightpad.daxplorer.visualization.charts.Chart
import io.rightpad.daxplorer.visualization.charts.SelectionChart
import java.time.LocalDateTime

class DateSelectionVisualizer : Visualizer("DateSelection") {
    private val selectionChart = SelectionChart()

    override val charts: List<Chart>
        get() = listOf(this.selectionChart)

    override fun visualize(startTimestamp: LocalDateTime, endTimestamp: LocalDateTime) {
        this.selectionChart.selectionStart = startTimestamp.daysSinceEpoch()
        this.selectionChart.selectionEnd = endTimestamp.daysSinceEpoch()
    }
}
