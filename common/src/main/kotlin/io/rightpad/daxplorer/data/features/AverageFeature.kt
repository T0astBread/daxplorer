package io.rightpad.daxplorer.data.features

import io.rightpad.daxplorer.data.IndexDataPoint
import io.rightpad.daxplorer.data.SimpleValueDataPoint

class AverageFeature(var span: Int): Feature<SimpleValueDataPoint> {
    override var featureData: List<SimpleValueDataPoint>? = null

    override fun calculate(indexData: List<IndexDataPoint>) {
        val newFeatureData = mutableListOf<SimpleValueDataPoint>()

        val averageSpan = mutableListOf<IndexDataPoint>()

        for(dataPoint in indexData) {
            averageSpan.add(dataPoint)

            if(averageSpan.size > this.span)
                averageSpan.removeAt(0)

            if(averageSpan.size == this.span)
                newFeatureData.add(SimpleValueDataPoint(
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
}