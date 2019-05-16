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

    fun resscale() {
        // make a second list that is scaled
        // rescale it in this method
    }

    override fun calculate(indexData: List<IndexDataPoint>) {

        val rawRSI = MutableList<SimpleValueDataPoint?>(indexData.size) { null }

        val RSlist = MutableList<SimpleValueDataPoint?>(indexData.size) { null }
        // relRSI   = mutableListOf<DoubleDataPoint?>();
        // val AvgGains = MutableList<Double?>(indexData.size) {null}
        //   val AvgLosses = MutableList<Double?>(indexData.size) {null}

        if(indexData.isEmpty()) return

        for(i in 1 until x) {

            var listDifference = indexData.subList(0, i).map { (it.end - it.start).toFloat() }

            var RS = calculateRS(listDifference)


            RSlist[i] = SimpleValueDataPoint(indexData[i].timestamp, RS)
            var RSI = 100 - (100 / (1 + RS))
            // if(loss==0.0)RSI = 100.0;
            if(RSI < 0 || RSI > 100) throw InvalidRSIException("Raw RSI [$RSI] is out of bounds [0,100]")
            rawRSI[i] = SimpleValueDataPoint(indexData[i].timestamp, RSI)
        }


        for(i in x until indexData.size) {
            var listDifference = indexData.subList(i - x, i).map { (it.end - it.start).toFloat() }

            var RS = calculateRS(listDifference)

            RSlist[i] = SimpleValueDataPoint(indexData[i].timestamp, RS)

            var RSI = 100 - (100 / (1 + RS))

            if(RSI < 0 || RSI > 100) throw InvalidRSIException("Raw RSI [$RSI] is out of bounds [0,100]")
            rawRSI[i] = SimpleValueDataPoint(indexData[i].timestamp, RSI)

        }

        val smoothRSI = MutableList<SimpleValueDataPoint?>(indexData.size) { null }
        // val smoothRS = mutableListOf<DoubleDataPoint?>();
        //smoothRS[0]=RSlist[0];

        // var filteredRS = RSlist;

        for(i in 1 until RSlist.size) {
            var last = RSlist[i - 1]?.value
            var current = RSlist[i]?.value
            if(last == null || current == null) System.err.println("An value is null, inserting default value for $i")

            var RS = (((last ?: 1.0f) * (smoothingWeight - 1)) + (current ?: 1.0f)) / smoothingWeight
            var RSI = 100 - 100 / (1 + RS)
            if(RSI < 0 || RSI > 100) throw InvalidRSIException("RSI [$RSI] is out of bounds [0,100]")

            // var d = DoubleDataPoint(RSI,RSlist[i]?.timestamp?:extrapolateTimeStamp(RSlist,i))
            System.err.println("$i RSI:$RSI")

            if(RSlist[i] != null) {
                var d = SimpleValueDataPoint(RSlist[i]!!.timestamp, RSI)
                smoothRSI.add(i, d)
            }
            else {
                System.err.println("$i No RS, Skipped")
            }
        }
        featureData = smoothRSI.filter { it != null && !it.value.isNaN() }.map { it!! }

        System.err.print("")


    }

    private fun calculateRS(listDifference: List<Float>): Float {
        var gain = listDifference.filter { it > 0 }.sum() / (100 * x)
        var loss = listDifference.filter { it < 0 }.sum() / (-100 * x)
        var RS = (gain / loss)
        if(!RS.isFinite()) RS = 1.0f
        return RS
    }

    private fun extrapolateTimeStamp(list: MutableList<SimpleValueDataPoint?>, index: Int): LocalDateTime {
        var before: LocalDateTime? = null
        var after: LocalDateTime? = null

        var i: Int = 0
        while((after == null || before == null) && (index + i) < list.size - 1) {
            i++
            if(index + i > list.size - 1 && before != null) after = before.plusDays(i * 2L)
            if(index - i < 0 && after != null) before = after.minusDays(i * 2L)

            if(after == null) after = list[index + i]?.timestamp

            if(before == null && (index - i) > 0) before = list[index - i]?.timestamp
        }

        before!!

        before = before.plusDays(ChronoUnit.DAYS.between(before, after) / 2)
        return before
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
