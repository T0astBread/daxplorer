package io.rightpad.daxplorer;

import io.rightpad.daxplorer.charts.LineChart;
import org.junit.Test;

import java.awt.*;

public class LineChartTest extends ChartTest
{
    @Test
    public void testLineChart()
    {
        LineChart lineChart = new LineChart();
        lineChart.addPoint(1, 12, Color.BLACK);
        lineChart.addPoint(2, 20, Color.BLACK);
        lineChart.addPoint(13, 46, Color.GREEN);
        lineChart.addPoint(19, 4, Color.RED);
        openTestWindow(lineChart);
    }
}
