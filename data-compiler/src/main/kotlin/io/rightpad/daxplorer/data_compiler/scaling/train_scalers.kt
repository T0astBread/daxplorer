package io.rightpad.daxplorer.data_compiler.scaling

import io.rightpad.daxplorer.data.datapoints.absolute.AverageDataPoint
import io.rightpad.daxplorer.data.datapoints.absolute.IndexDataPoint
import io.rightpad.daxplorer.data.datapoints.absolute.SimpleValueDataPoint
import io.rightpad.daxplorer.data.datapoints.absolute.TimeSeriesDataPoint
import io.rightpad.daxplorer.data.scalers.PrimitiveScalerConfig
import io.rightpad.daxplorer.data_compiler.ScalerConfig

fun createConfigFromData(
        compiledData: List<List<TimeSeriesDataPoint?>>
): ScalerConfig {
    val flattenedDataPoints = compiledData.flatten()
    val indexDataPoints = flattenedDataPoints
            .mapNotNull { dataPoint -> dataPoint as? IndexDataPoint }

    val moneyValues1 = flattenedDataPoints
            .filter { dataPoint -> dataPoint is AverageDataPoint }
            .map { dataPoint -> (dataPoint as SimpleValueDataPoint).value }
    val moneyValues2 = indexDataPoints
            .flatMap { indexDataPoint -> listOf(
                    indexDataPoint.start,
                    indexDataPoint.end,
                    indexDataPoint.min,
                    indexDataPoint.max
            ) }
    val moneyValues = moneyValues1.plus(moneyValues2)
    val moneyMin = moneyValues.min()!!
    val moneyMax = moneyValues.max()!!

    val volumeValues = indexDataPoints
            .map { indexDataPoint -> indexDataPoint.volume }
    val volumeMin = volumeValues.min()!!.toFloat()
    val volumeMax = volumeValues.max()!!.toFloat()

    return ScalerConfig(
            moneyScalerConfig = PrimitiveScalerConfig(moneyMin, moneyMax),
            volumeScalerConfig = PrimitiveScalerConfig(volumeMin, volumeMax)
    )
}
