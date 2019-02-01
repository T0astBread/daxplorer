package io.rightpad.daxplorer.data.serialization

import io.rightpad.daxplorer.data.TimeSeriesDataPoint

interface Serializer<T : TimeSeriesDataPoint, R, I> {
    fun serialize(data: Iterable<T>): R
    fun serializeSingle(dataPoint: T): R
    fun deserialize(input: I): List<T>
}
