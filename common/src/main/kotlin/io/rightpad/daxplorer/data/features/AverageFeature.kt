package io.rightpad.daxplorer.data.features

import io.rightpad.daxplorer.data.IndexDataPoint
import io.rightpad.daxplorer.data.SimpleValueDataPoint

class AverageFeature(var span: Int): Feature<SimpleValueDataPoint> {
    override var featureData: List<SimpleValueDataPoint>? = null

    override fun calculate(indexData: List<IndexDataPoint>) {
        val newFeatureData = mutableListOf<SimpleValueDataPoint>()

        val averageSpan = arrayOfNulls<IndexDataPoint>(this.span)

        var i = 0
        for(dataPoint in indexData) {
            averageSpan[i++] = dataPoint
            if(i == this.span) {
                newFeatureData.add(SimpleValueDataPoint(
                        dataPoint.timestamp,
                        averageOf(averageSpan)
                ))
                i = 0
            }
        }

        this.featureData = newFeatureData
    }

    private fun averageOf(averageSpan: Array<IndexDataPoint?>): Float =
            averageSpan.asSequence()
                    .sumByDouble { it!!.average.toDouble() }
                    .div(averageSpan.size)
                    .toFloat()
}