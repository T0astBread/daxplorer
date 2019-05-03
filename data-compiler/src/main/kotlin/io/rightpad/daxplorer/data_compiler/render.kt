package io.rightpad.daxplorer.data_compiler

import io.rightpad.daxplorer.data.datapoints.absolute.TimeSeriesDataPoint
import io.rightpad.daxplorer.data.serialization.toCSV

fun render(compiledData: List<List<TimeSeriesDataPoint?>>): String {
    val stringifiedData = compiledData.map { row ->
        row.mapIndexed { i, col ->
            var parts = col?.toCSV()?.split(";")
            if(i != 0) { // Remove timestamp on all but the first column
                parts = parts?.slice(1..(parts.size - 1))
            }
            parts?.joinToString(";")
        }
    }.map { row ->
        row.joinToString(";")
    }.joinToString("\n")

    return stringifiedData
}
