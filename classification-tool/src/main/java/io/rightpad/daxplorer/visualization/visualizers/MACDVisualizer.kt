package io.rightpad.daxplorer.visualization.visualizers

import io.rightpad.daxplorer.data.datapoints.absolute.MACDDataPoint
import io.rightpad.daxplorer.data.features.AverageFeature
import io.rightpad.daxplorer.data.features.Feature
import io.rightpad.daxplorer.data.features.MACD
import io.rightpad.daxplorer.visualization.charts.Chart
import java.time.LocalDateTime

class MACDVisualizer(
        private val averageVisualizer1: AverageVisualizer,
        private val averageVisualizer2: AverageVisualizer
) : TimeSeriesVisualizer<MACDDataPoint>("MACD") {

    private val averageFeature1 = this.averageVisualizer1.features[0] as AverageFeature
    private val averageFeature2 = this.averageVisualizer2.features[0] as AverageFeature

    private val macdFeature = MACD(this.averageFeature1, this.averageFeature2)
    override val features: List<Feature<MACDDataPoint>> = listOf(this.macdFeature)


    override val charts: List<Chart> = this.averageVisualizer1.charts + this.averageVisualizer2.charts


    override fun visualize(startTimestamp: LocalDateTime, endTimestamp: LocalDateTime) {
        this.averageVisualizer1.visualize(startTimestamp, endTimestamp)
        this.averageVisualizer2.visualize(startTimestamp, endTimestamp)
    }
}