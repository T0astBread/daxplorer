package io.rightpad.daxplorer.visualization.visualizers

import io.rightpad.daxplorer.data.datapoints.absolute.AverageDataPoint
import io.rightpad.daxplorer.data.datapoints.absolute.SimpleValueDataPoint
import io.rightpad.daxplorer.data.features.AverageFeature
import io.rightpad.daxplorer.data.features.Feature
import io.rightpad.daxplorer.utils.daysSinceEpoch
import io.rightpad.daxplorer.visualization.charts.Chart
import io.rightpad.daxplorer.visualization.charts.LineChart
import java.awt.Color
import java.time.LocalDateTime

open class SimpleValueVisualizer<T : SimpleValueDataPoint>(
        name: String,
        val feature: Feature<T>,
        var color: Color
) : TimeSeriesVisualizer<T>(name) {

    override val features: List<Feature<T>> = listOf(this.feature)

    private val lineChart: LineChart = LineChart()
    override val charts: List<Chart> = listOf(this.lineChart)

    override fun construct(startTimestamp: LocalDateTime, endTimestamp: LocalDateTime) {
        this.feature.featureData!!
                .takeWhile { it.timestamp >= startTimestamp && it.timestamp < endTimestamp }
                .forEach { this.lineChart.addPoint(
                        it.timestamp.daysSinceEpoch(),
                        it.value,
                        this.color
                ) }
    }

    override fun destroy(startTimestamp: LocalDateTime, endTimestamp: LocalDateTime) {
        this.lineChart.clearBetween(startTimestamp.daysSinceEpoch(), endTimestamp.daysSinceEpoch())
    }
}