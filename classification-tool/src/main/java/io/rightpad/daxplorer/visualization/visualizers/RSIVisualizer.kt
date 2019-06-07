package io.rightpad.daxplorer.visualization.visualizers

import io.rightpad.daxplorer.data.datapoints.absolute.SimpleValueDataPoint
import io.rightpad.daxplorer.data.features.Feature
import io.rightpad.daxplorer.data.features.RelativeStrengthIndex
import io.rightpad.daxplorer.utils.daysSinceEpoch
import io.rightpad.daxplorer.visualization.charts.Chart
import io.rightpad.daxplorer.visualization.charts.LineChart
import java.awt.Color
import java.time.LocalDateTime

class RSIVisualizer(var low: Color, var neutral: Color, var high: Color): TimeSeriesVisualizer<SimpleValueDataPoint>("Relative Strength Index") {
    private val rsiFeature: RelativeStrengthIndex = RelativeStrengthIndex()
    override val features: List<Feature<SimpleValueDataPoint>> = listOf(this.rsiFeature)
    private val lineChart: LineChart = LineChart()
    override val charts: List<Chart> = listOf(this.lineChart)

    private val thresholdLow = 30
    private val thresholdHigh = 70

    override fun construct(startTimestamp: LocalDateTime, endTimestamp: LocalDateTime) {
        if(rsiFeature.featureData == null) return
        this.rsiFeature.featureData!!
//                .filter { it.timestamp.daysSinceEpoch() < startTimestamp.daysSinceEpoch() || it.timestamp.daysSinceEpoch() > endTimestamp.daysSinceEpoch() }
                .takeWhile { it.timestamp >= startTimestamp && it.timestamp < endTimestamp }
                .forEach {
                    this.lineChart.addPoint(it.timestamp.daysSinceEpoch(), (it.value) * rsiFeature.scale / 100, getColor(it.value))

                }
    }

    override fun destroy(startTimestamp: LocalDateTime, endTimestamp: LocalDateTime) {
        this.lineChart.clearBetween(startTimestamp.daysSinceEpoch(), endTimestamp.daysSinceEpoch())
    }

    fun scale(scale: Int) {
        rsiFeature.scale = scale

    }

    private fun getColor(value: Float): Color {
        if(value > thresholdHigh) return high
        else if(value > thresholdLow) return neutral
        else {
            return low
        }
    }
}