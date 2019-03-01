package io.rightpad.daxplorer.data_compiler

import io.rightpad.daxplorer.data.IndexDataPoint
import io.rightpad.daxplorer.data.TimeSeriesDataPoint
import io.rightpad.daxplorer.data.features.Feature
import io.rightpad.daxplorer.data.serialization.toCSV

fun compile(indexData: List<IndexDataPoint>, features: List<Feature<TimeSeriesDataPoint>>): String {
    features.forEach { f -> f.calculate(indexData) }
    val featureData = features.map { f -> f.featureData }
    val compilableData = indexData.map { dataPoint ->
        listOf(dataPoint).plus(featureData.map { fd ->
            fd?.firstOrNull { featureDataPoint ->
                featureDataPoint.timestamp == dataPoint.timestamp
            }
        })
    }

    val stringifiedData = compilableData.map { row ->
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