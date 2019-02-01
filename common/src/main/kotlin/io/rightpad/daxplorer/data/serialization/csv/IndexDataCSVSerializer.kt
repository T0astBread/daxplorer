package io.rightpad.daxplorer.data.serialization.csv

import io.rightpad.daxplorer.data.IndexDataPoint
import io.rightpad.daxplorer.data.serialization.Serializer
import java.time.LocalDate

class IndexDataCSVSerializer: Serializer<IndexDataPoint, String, String> {
    override fun serialize(data: Iterable<IndexDataPoint>): String = data
            .map { serializeSingle(it) }
            .joinToString { "\n" }

    override fun serializeSingle(dataPoint: IndexDataPoint): String =
            arrayOf(
                    dataPoint.timestamp.format(CSV_DATE_FORMAT),
                    dataPoint.start,
                    dataPoint.max,
                    dataPoint.min,
                    dataPoint.end,
                    dataPoint.volume,
                    dataPoint.trend
            ).joinToString(";")

    override fun deserialize(input: String): List<IndexDataPoint> = input.lines()
            .map { it.split(";") }
            .map { tokens ->
                IndexDataPoint(
                        timestamp = LocalDate.parse(tokens[0], io.rightpad.daxplorer.data.serialization.CSV_DATE_FORMAT).atStartOfDay(),
                        start = tokens[1].toFloat(),
                        max = tokens[2].toFloat(),
                        min = tokens[3].toFloat(),
                        end = tokens[4].toFloat(),
                        volume = tokens[5].toInt(),
                        trend = if(tokens.size < 7) 0 else tokens[6].toByte()
                )
            }
}