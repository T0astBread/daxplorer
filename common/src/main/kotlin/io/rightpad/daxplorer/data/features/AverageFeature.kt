package io.rightpad.daxplorer.data.features

import io.rightpad.daxplorer.data.datapoints.absolute.AverageDataPoint
import io.rightpad.daxplorer.data.datapoints.absolute.IndexDataPoint
import io.rightpad.daxplorer.data.datapoints.absolute.SimpleValueDataPoint

class AverageFeature(var span: Int): Feature<AverageDataPoint> {
    override var featureData: List<AverageDataPoint>? = null

    override fun calculate(indexData: List<IndexDataPoint>) {
        val newFeatureData = mutableListOf<AverageDataPoint>()

        val averageSpan = mutableListOf<IndexDataPoint>()

        for(dataPoint in indexData) {
            averageSpan.add(dataPoint)

            if(averageSpan.size > this.span)
                averageSpan.removeAt(0)

            if(averageSpan.size == this.span)
                newFeatureData.add(AverageDataPoint(
                        dataPoint.timestamp,
                        averageOf(averageSpan)
                ))
        }

        this.featureData = newFeatureData
    }

    private fun averageOf(averageSpan: List<IndexDataPoint?>): Float =
            averageSpan.asSequence()
                    .sumByDouble { it!!.average.toDouble() }
                    .div(averageSpan.size)
                    .toFloat()

    override fun toString(): String =
            "average[$span]"
}