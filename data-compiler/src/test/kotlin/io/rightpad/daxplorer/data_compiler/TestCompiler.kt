package io.rightpad.daxplorer.data_compiler

import io.rightpad.daxplorer.data.datapoints.absolute.IndexDataPoint
import io.rightpad.daxplorer.data.datapoints.absolute.TimeSeriesDataPoint
import io.rightpad.daxplorer.data.features.AverageFeature
import io.rightpad.daxplorer.data.features.Feature
import org.junit.Assert
import org.junit.Test
import java.time.LocalDateTime
import java.time.Month

class TestCompiler {
    @Test
    fun testCompile() {
        val indexData = listOf(
                IndexDataPoint(
                        timestamp = LocalDateTime.of(
                                2018,
                                Month.FEBRUARY,
                                15,
                                0,
                                0
                        ),
                        start = 1000.1f,
                        end = 1200f,
                        min = 800f,
                        max = 1500f,
                        volume = 10000,
                        trend = 1
                ),
                IndexDataPoint(
                        timestamp = LocalDateTime.of(
                                2018,
                                Month.FEBRUARY,
                                16,
                                0,
                                0
                        ),
                        start = 1200f,
                        end = 2200f,
                        min = 1100f,
                        max = 2500f,
                        volume = 35000,
                        trend = 1
                ),
                IndexDataPoint(
                        timestamp = LocalDateTime.of(
                                2018,
                                Month.FEBRUARY,
                                17,
                                0,
                                0
                        ),
                        start = 2200f,
                        end = 1800f,
                        min = 1800f,
                        max = 2300f,
                        volume = 15000,
                        trend = -1
                )
        )
        val expected = """
            20180215;1000.1;1500.0;800.0;1200.0;10000;1;null
            20180216;1200.0;2500.0;1100.0;2200.0;35000;1;1400.025
            20180217;2200.0;2300.0;1800.0;1800.0;15000;-1;1850.0
        """.trimIndent()
        val actual = compile(indexData, listOf(
                AverageFeature(2) as Feature<TimeSeriesDataPoint>
        ))
        Assert.assertEquals(expected, actual)
    }
}
