package io.rightpad.daxplorer.data_compiler

import io.rightpad.daxplorer.data.datapoints.absolute.IndexDataPoint
import io.rightpad.daxplorer.data.datapoints.absolute.TimeSeriesDataPoint
import io.rightpad.daxplorer.data.features.Feature
import io.rightpad.daxplorer.data.serialization.toCSV

fun generateHeader(features: List<Feature<TimeSeriesDataPoint>>): String =
        "timestamp;start;max;min;end;volume;trend${
        if(features.isNotEmpty())
            ";" + features.map { it.toString() }.joinToString(";")
        else ""
        }\n"

fun compile(indexData: List<IndexDataPoint>, features: List<Feature<TimeSeriesDataPoint>>): List<List<TimeSeriesDataPoint?>> {
    features.forEach { f -> f.calculate(indexData) }
    val featureData = features.map { f -> f.featureData }
    val compiledData = indexData.map { dataPoint ->
        listOf(dataPoint).plus(featureData.map { fd ->
            fd?.firstOrNull { featureDataPoint ->
                featureDataPoint.timestamp == dataPoint.timestamp
            }
        })
    }
    return compiledData
}
