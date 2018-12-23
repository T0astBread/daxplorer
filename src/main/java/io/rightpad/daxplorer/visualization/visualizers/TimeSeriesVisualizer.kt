package io.rightpad.daxplorer.visualization.visualizers

import io.rightpad.daxplorer.data.IndexDataPoint
import io.rightpad.daxplorer.data.TimeSeriesDataPoint
import io.rightpad.daxplorer.data.features.Feature

abstract class TimeSeriesVisualizer<T : TimeSeriesDataPoint>(name: String): Visualizer(name) {
    abstract val features: List<Feature<T>>

    fun calculate(indexData: List<IndexDataPoint>) {
        this.features.forEach { feature -> feature.calculate(indexData) }
    }
}