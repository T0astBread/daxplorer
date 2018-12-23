package io.rightpad.daxplorer.visualization.visualizers

import io.rightpad.daxplorer.daysSinceEpoch
import io.rightpad.daxplorer.visualization.charts.Chart
import io.rightpad.daxplorer.visualization.charts.SelectionChart
import java.time.LocalDateTime

class DateSelectionVisualizer : Visualizer("DateSelection") {
    var selectionStart: LocalDateTime? = null
        set(value) {
            field = value
            println("Setting selection start to $value")
        }
    var selectionEnd: LocalDateTime? = null
        set(value) {
            field = value
            println("Setting selection end to $value")
        }

    private val selectionChart = SelectionChart()

    override val charts: List<Chart>
        get() = listOf(this.selectionChart)

    override fun visualize(startTimestamp: LocalDateTime, endTimestamp: LocalDateTime) {
        if(this.selectionStart == null || this.selectionEnd == null) {
            this.selectionChart.isSelected = false
            return
        }
        this.selectionChart.isSelected = true
        this.selectionChart.selectionStart = Math.max(this.selectionStart!!.daysSinceEpoch(), startTimestamp.daysSinceEpoch())
        this.selectionChart.selectionEnd = Math.min(this.selectionEnd!!.daysSinceEpoch(), endTimestamp.daysSinceEpoch())
    }
}
