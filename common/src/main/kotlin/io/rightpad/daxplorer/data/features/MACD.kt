package io.rightpad.daxplorer.data.features

import io.rightpad.daxplorer.data.datapoints.absolute.IndexDataPoint
import io.rightpad.daxplorer.data.datapoints.absolute.MACDDataPoint
import io.rightpad.daxplorer.data.datapoints.absolute.SimpleValueDataPoint

class MACD(private val feature1: AverageFeature, private val feature2: AverageFeature): Feature<MACDDataPoint> {
    override var featureData: List<MACDDataPoint>? = null

    override fun calculate(indexData: List<IndexDataPoint>) {
        feature1.calculate(indexData)
        feature2.calculate(indexData)
        val ew1: Float = 2 / (feature1.span + 1).toFloat()
        val ew2: Float = 2 / (feature2.span + 1).toFloat()
        val listEma1 = mutableListOf<SimpleValueDataPoint>()
        val listEma2 = mutableListOf<SimpleValueDataPoint>()
        var ema1: Float = 0f
        var ema2: Float = 0f
        for(item in feature1.featureData!!) {
            ema1 = calculateEma(item.value, ema1, ew1)
            listEma1.add(SimpleValueDataPoint(item.timestamp, ema1))
        }
        for(item in feature2.featureData!!) {
            ema2 = calculateEma(item.value, ema2, ew2)
            listEma2.add(SimpleValueDataPoint(item.timestamp, ema2))
        }

        val listMacd = mutableListOf<SimpleValueDataPoint>()

        if(feature1.featureData!!.size > feature2.featureData!!.size) {
            for(i in 0 until feature1.featureData!!.size) {
                if(i < listEma1.size && i < listEma2.size) {
                    val value: Float = listEma2[i].value - listEma1[i].value
                    listMacd.add(SimpleValueDataPoint(feature1.featureData!![i].timestamp, value))
                }
            }
        }
        else {
            for(i in 0 until feature2.featureData!!.size) {
                if(i < listEma1.size && i < listEma2.size) {
                    val value: Float = listEma2[i].value - listEma1[i].value
                    listMacd.add(SimpleValueDataPoint(feature2.featureData!![i].timestamp, value))
                }
            }
        }
        featureData = listMacd
    }

    private fun calculateEma(value: Float, Ema: Float, Ew: Float): Float =
            ((value - Ema) * Ew) + Ema

    override fun toString() = "macd[${feature1},${feature2}]"
}

class MACDConfig(
        val average1: AverageFeatureConfig,
        val average2: AverageFeatureConfig
): FeatureConfig<MACD, Nothing?> {
    override fun createFeature(): MACD {
        val avgFeature1 = this.average1.createFeature()
        val avgFeature2 = this.average2.createFeature()

        return MACD(avgFeature1, avgFeature2)
    }

    override fun createScaler() = null
}
