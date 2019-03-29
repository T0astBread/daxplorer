package io.rightpad.daxplorer.data.serialization

import io.rightpad.daxplorer.data.datapoints.absolute.IndexDataPoint
import io.rightpad.daxplorer.data.datapoints.absolute.SimpleValueDataPoint
import io.rightpad.daxplorer.data.datapoints.absolute.TimeSeriesDataPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.stream.Collector

val CSV_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd")

fun TimeSeriesDataPoint.toCSV(): String {
    if(this is IndexDataPoint)
        return (this as IndexDataPoint).toCSV()
    if(this is SimpleValueDataPoint)
        return (this as SimpleValueDataPoint).toCSV()
    return ""
}

fun IndexDataPoint.toCSV() =
        arrayOf(
                this.timestamp.format(CSV_DATE_FORMAT),
                this.start,
                this.max,
                this.min,
                this.end,
                this.volume,
                this.trend
        ).joinToString(";")

fun String.fromCSVToIndexDataPoint(): IndexDataPoint {
    val tokens = split(";")
    return IndexDataPoint(
            timestamp = LocalDate.parse(tokens[0], CSV_DATE_FORMAT).atStartOfDay(),
            start = tokens[1].toFloat(),
            max = tokens[2].toFloat(),
            min = tokens[3].toFloat(),
            end = tokens[4].toFloat(),
            volume = tokens[5].toInt(),
            trend = if(tokens.size < 7) 0 else tokens[6].toByte()
    )
}

fun SimpleValueDataPoint.toCSV(): String =
        "${timestamp.format(CSV_DATE_FORMAT)};$value"

val csvCollector = Collector.of<IndexDataPoint, MutableList<String>, String>(
        { mutableListOf() },
        { container, dataPoint -> container.add(dataPoint.toCSV()) },
        { container1, container2 ->
            container1.addAll(container2)
            container1
        },
        { container -> container.joinToString("\n") },
        arrayOf()
)!!