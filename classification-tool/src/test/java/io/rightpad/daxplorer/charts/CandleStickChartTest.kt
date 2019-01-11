package io.rightpad.daxplorer.charts

import io.rightpad.daxplorer.visualization.charts.CandleStickChart
import org.junit.Test
import java.awt.Color

class CandleStickChartTest : ChartTest() {

    @Test
    fun testCandleStickChart() {
        val chart = CandleStickChart()
        val bodyColor = Color.GREEN
        chart.addCandleStick(CandleStickChart.CandleStick(
                3f,
                50f,
                70f,
                25f,
                90f,
                Color.BLACK,
                Color(bodyColor.red, bodyColor.green, bodyColor.blue, 50)
        ))
        openTestWindow(chart)
    }
}