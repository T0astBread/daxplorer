package io.rightpad.daxplorer.data.features

import io.rightpad.daxplorer.data.datapoints.absolute.IndexDataPoint
import io.rightpad.daxplorer.data.datapoints.absolute.SimpleValueDataPoint
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class RelativeStrengthIndex: Feature<SimpleValueDataPoint> {
    override var featureData: List<SimpleValueDataPoint>? = null

    var x = 100
    var smoothingWeight = 14
    var scale = 14000

    override fun calculate(indexData: List<IndexDataPoint>) {
        val rawRSI = MutableList<SimpleValueDataPoint?>(indexData.size) { null }
        val RSlist = MutableList<SimpleValueDataPoint?>(indexData.size) { null }

        if(indexData.isEmpty()) return

        for(i in 1 until x) {
            var listDifference = indexData.subList(0, i).map { (it.end - it.start) }

            var RS = calculateRS(listDifference)

            RSlist[i] = SimpleValueDataPoint(indexData[i].timestamp, RS)
            var RSI = 100 - (100 / (1 + RS))
            // if(loss==0.0)RSI = 100.0;
            if(RSI < 0 || RSI > 100) throw InvalidRSIException("[RSI] Raw RSI [$RSI] is out of bounds [0,100]")
            rawRSI[i] = SimpleValueDataPoint(indexData[i].timestamp, RSI)
        }

        for(i in x until indexData.size) {
            var listDifference = indexData.subList(i - x, i).map { (it.end - it.start).toFloat() }

            var RS = calculateRS(listDifference)

            RSlist[i] = SimpleValueDataPoint(indexData[i].timestamp, RS)

            var RSI = 100 - (100 / (1 + RS))

            if(RSI < 0 || RSI > 100) throw InvalidRSIException("[RSI] Raw RSI [$RSI] is out of bounds [0,100]")
            rawRSI[i] = SimpleValueDataPoint(indexData[i].timestamp, RSI)
        }

        val smoothRSI = MutableList<SimpleValueDataPoint?>(indexData.size) { null }

        for(i in 1 until RSlist.size) {
            var last = RSlist[i - 1]?.value
            var current = RSlist[i]?.value
            if(last == null || current == null) System.err.println("[RSI] A value is null, inserting default value for $i")

            var RS = (((last ?: 1.0f) * (smoothingWeight - 1)) + (current ?: 1.0f)) / smoothingWeight
            var RSI = 100 - 100 / (1 + RS)
            if(RSI < 0 || RSI > 100) throw InvalidRSIException("[RSI] RSI [$RSI] is out of bounds [0,100]")

            if(RSlist[i] != null) {
                var d = SimpleValueDataPoint(RSlist[i]!!.timestamp, RSI)
                smoothRSI.add(i, d)
            }
            else {
                System.err.println("[RSI] $i No RS, Skipped")
            }
        }
        featureData = smoothRSI.filter { it != null && !it.value.isNaN() }.map { it!! }
    }

    private fun calculateRS(listDifference: List<Float>): Float {
        var gain = listDifference.filter { it > 0 }.sum() / (100 * x)
        var loss = listDifference.filter { it < 0 }.sum() / (-100 * x)
        var RS = (gain / loss)
        if(!RS.isFinite()) RS = 1.0f
        return RS
    }

    override fun toString() = "rsi[smoothingWeight=$smoothingWeight,scale=$scale]"
}


class RelativeStrengthIndexFeatureConfig(
        val smoothingWeight: Int,
        val scale: Int? = null
): FeatureConfig<RelativeStrengthIndex, Nothing?> {

    override fun createFeature() =
            RelativeStrengthIndex().apply {
                this.smoothingWeight = this@RelativeStrengthIndexFeatureConfig.smoothingWeight

                if(this@RelativeStrengthIndexFeatureConfig.scale != null)
                    this.scale = this@RelativeStrengthIndexFeatureConfig.scale
            }

    override fun createScaler() = null
}
