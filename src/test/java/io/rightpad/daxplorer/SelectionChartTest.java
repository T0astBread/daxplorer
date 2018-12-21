package io.rightpad.daxplorer;

import io.rightpad.daxplorer.visualization.charts.SelectionChart;
import org.junit.Test;

public class SelectionChartTest extends ChartTest
{
    @Test
    public void testSelectionChart()
    {
        SelectionChart selectionChart = new SelectionChart();
        selectionChart.setSelectionStart(4);
        selectionChart.setSelectionEnd(18);
        openTestWindow(selectionChart);
    }
}
