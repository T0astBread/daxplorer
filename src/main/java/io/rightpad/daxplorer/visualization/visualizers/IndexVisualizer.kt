package io.rightpad.daxplorer.visualization.visualizers

import io.rightpad.daxplorer.data.IndexDataPoint
import io.rightpad.daxplorer.data.features.Feature
import io.rightpad.daxplorer.data.features.IndexFeature
import io.rightpad.daxplorer.utils.daysSinceEpoch
import io.rightpad.daxplorer.visualization.charts.Chart
import io.rightpad.daxplorer.visualization.charts.LineChart
import java.awt.Color
import java.time.LocalDateTime

class IndexVisualizer: TimeSeriesVisualizer<IndexDataPoint>("Index") {
    private val lineChart = LineChart()

    override val features: List<Feature<IndexDataPoint>> = listOf(IndexFeature())
    override val charts: List<Chart> = listOf(this.lineChart)

    override fun visualize(startTimestamp: LocalDateTime, endTimestamp: LocalDateTime) {
        this.lineChart.clearPoints()
        this.features[0].featureData
                ?.filter { dataPoint -> dataPoint.timestamp in startTimestamp..endTimestamp }
                ?.sortedBy { dataPoint -> dataPoint.timestamp }
                ?.forEach { dataPoint ->
                    this.lineChart.addPoint(
                            dataPoint.timestamp.daysSinceEpoch(),
                            dataPoint.end,
                            when(dataPoint.trend) {
                                (-1).toByte() -> Color.RED
                                (1).toByte()  -> Color.GREEN
                                else          -> Color.BLACK
                            }
                    )
                }
    }

}