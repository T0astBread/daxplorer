package io.rightpad.daxplorer.visualization.visualizers

import io.rightpad.daxplorer.data.SimpleValueDataPoint
import io.rightpad.daxplorer.data.features.AverageFeature
import io.rightpad.daxplorer.data.features.Feature
import io.rightpad.daxplorer.utils.daysSinceEpoch
import io.rightpad.daxplorer.visualization.charts.Chart
import io.rightpad.daxplorer.visualization.charts.LineChart
import java.awt.Color
import java.time.LocalDateTime

class AverageVisualizer(span: Int, var color: Color) : TimeSeriesVisualizer<SimpleValueDataPoint>("Average") {
    private val averageFeature: AverageFeature = AverageFeature(span)
    override val features: List<Feature<SimpleValueDataPoint>> = listOf(this.averageFeature)

    private val lineChart: LineChart = LineChart()
    override val charts: List<Chart> = listOf(this.lineChart)

    override fun visualize(startTimestamp: LocalDateTime, endTimestamp: LocalDateTime) {
        this.averageFeature.featureData!!
//                .takeWhile { it.timestamp >= startTimestamp && it.timestamp < endTimestamp }
                .forEach { this.lineChart.addPoint(
                        it.timestamp.daysSinceEpoch(),
                        it.value,
                        this.color
                ) }
    }
}