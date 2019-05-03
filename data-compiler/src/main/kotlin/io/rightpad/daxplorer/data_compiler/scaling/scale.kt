package io.rightpad.daxplorer.data_compiler.scaling

import io.rightpad.daxplorer.data.datapoints.absolute.TimeSeriesDataPoint
import io.rightpad.daxplorer.data.scalers.Scaler

fun scale(
        compiledData: List<List<TimeSeriesDataPoint?>>,
        scalers: List<Scaler<TimeSeriesDataPoint, TimeSeriesDataPoint, Any>?>
): List<List<TimeSeriesDataPoint?>> {
    val scaledData = compiledData.map { row ->
        row.mapIndexed { i, featureDataPoint ->
            if(featureDataPoint != null) scalers[i]?.scale(featureDataPoint) ?: featureDataPoint
            else null
        }
    }
    return scaledData
}
