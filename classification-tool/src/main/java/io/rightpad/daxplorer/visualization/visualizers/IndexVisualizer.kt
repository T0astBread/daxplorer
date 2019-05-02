package io.rightpad.daxplorer.visualization.visualizers

import io.rightpad.daxplorer.data.datapoints.absolute.IndexDataPoint
import io.rightpad.daxplorer.data.features.Feature
import io.rightpad.daxplorer.data.features.IndexFeature
import io.rightpad.daxplorer.utils.daysSinceEpoch
import io.rightpad.daxplorer.visualization.charts.CandleStickChart
import io.rightpad.daxplorer.visualization.charts.Chart
import io.rightpad.daxplorer.visualization.charts.LineChart
import java.awt.Color
import java.time.LocalDateTime

class IndexVisualizer: TimeSeriesVisualizer<IndexDataPoint>("Index") {
    private val lineChart = LineChart()
    private val candleStickChart = CandleStickChart()

    override val features: List<Feature<IndexDataPoint>> = listOf(IndexFeature())
    override val charts: List<Chart> = listOf(
            this.lineChart,
            this.candleStickChart
    )

    override fun visualize(startTimestamp: LocalDateTime, endTimestamp: LocalDateTime) {
        this.lineChart.clearPoints()
        this.candleStickChart.clearCandleSticks()
        this.features[0].featureData
                ?.filter { dataPoint -> dataPoint.timestamp in startTimestamp..endTimestamp }
                ?.sortedBy { dataPoint -> dataPoint.timestamp }
                ?.forEach { dataPoint ->
                    val trendColor = when(dataPoint.trend) {
                        (-1).toByte() -> Color.RED
                        (1).toByte()  -> Color.GREEN
                        else          -> Color.BLACK
                    }
                    this.lineChart.addPoint(
                            dataPoint.timestamp.daysSinceEpoch(),
                            dataPoint.end,
                            trendColor
                    )
                    this.candleStickChart.addCandleStick(CandleStickChart.CandleStick(
                            dataPoint.timestamp.daysSinceEpoch(),
                            dataPoint.start,
                            dataPoint.end,
                            dataPoint.min,
                            dataPoint.max,
                            trendColor,
                            if(dataPoint.start > dataPoint.end)
                                BEARISH_STICK_COLOR
                            else
                                BULLISH_STICK_COLOR
                    ))
                }
    }

    companion object {
        private val BULLISH_STICK_COLOR = Color.GREEN
        private val BEARISH_STICK_COLOR = Color.RED
    }
}