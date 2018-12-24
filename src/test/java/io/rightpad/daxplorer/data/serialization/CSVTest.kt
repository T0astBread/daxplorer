package io.rightpad.daxplorer.data.serialization

import io.rightpad.daxplorer.data.IndexDataPoint
import junit.framework.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime
import kotlin.streams.asStream

class CSVTest {
    private val EXAMPLE_DATA_POINT = IndexDataPoint(
            timestamp = LocalDateTime.of(2018, 12, 24, 3, 38),
            start = 1000f,
            max = 1400f,
            min = 900f,
            end = 1100f,
            volume = 10000,
            trend = 1
    )
    private val EXAMPLE_DATA_POINT_CSV = "20181224;1000.0;1400.0;900.0;1100.0;10000;1"

    @Test
    fun testToCSV() {
        assertEquals(EXAMPLE_DATA_POINT_CSV, EXAMPLE_DATA_POINT.toCSV())
    }

    @Test
    fun testFromCSV() {
        assertEquals(EXAMPLE_DATA_POINT, EXAMPLE_DATA_POINT_CSV.fromCSVToIndexDataPoint())
    }

    @Test
    fun testCSVCollector() {
        val length = 3
        val expected = Array(length) { EXAMPLE_DATA_POINT_CSV }
                .joinToString("\n")
        val actual = Array(length) { EXAMPLE_DATA_POINT }
                .asSequence()
                .asStream()
                .collect(csvCollector)
        assertEquals(expected, actual)
    }
}