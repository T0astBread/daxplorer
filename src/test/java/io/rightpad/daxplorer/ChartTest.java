package io.rightpad.daxplorer;

import io.rightpad.daxplorer.visualization.ChartPanel;
import io.rightpad.daxplorer.visualization.charts.Chart;

import javax.swing.*;
import java.awt.*;

public class ChartTest
{
    public void openTestWindow(Chart chart)
    {
        TestWindow window = new TestWindow();

        JDialog testWindow = new JDialog();
        testWindow.setModal(true);
        testWindow.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        testWindow.setContentPane(window.mainPanel);
        testWindow.setPreferredSize(new Dimension(600, 500));
        testWindow.pack();

        ChartPanel chartPanel = window.chartPanel;
        chartPanel.addChart(chart);

        testWindow.setVisible(true);
    }
}
