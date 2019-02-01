package io.rightpad.daxplorer.data_compiler

import io.rightpad.daxplorer.data.IndexDataPoint
import io.rightpad.daxplorer.data.TimeSeriesDataPoint
import io.rightpad.daxplorer.data.features.Feature
import io.rightpad.daxplorer.data.serialization.Serializer
import java.time.LocalDateTime

class Compiler<R>(
        val featureColumns: List<FeatureConfig<TimeSeriesDataPoint, R>> = mutableListOf(),
        val mergeColumns: (Iterable<SerializedDataPoint<R>>) -> R
) {
    fun compile(indexData: List<IndexDataPoint>): Iterable<R> {
        // Calculate index data
        featureColumns.forEach { it.feature.calculate(indexData) }

        return featureColumns.asSequence()
                .filter { it.feature.featureData != null }
                .map { featureConf -> // Map to seq of serialized feature cols
                    featureConf.feature.featureData!!.map { dataPoint -> // Map to serialized feature col
                        SerializedDataPoint(
                                dataPoint.timestamp,
                                featureConf.feature,
                                featureConf.serializer.serializeSingle(dataPoint)
                        )
                    }
                }
                // Un-group by column (feature) and group by row (timestamp) instead
                .flatten()
                .groupBy { it.timestamp }
                // Merge rows
                .map { mergeColumns(it.value) }
    }

    data class FeatureConfig<T: TimeSeriesDataPoint, R>(
            val feature: Feature<T>,
            val serializer: Serializer<T, R, *>,
            val defaultValue: R? = null
    )

    data class SerializedDataPoint<R>(
            val timestamp: LocalDateTime,
            val feature: Feature<TimeSeriesDataPoint>,
            val dataPoint: R
    )
}