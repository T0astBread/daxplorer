package io.rightpad.daxplorer.visualization.charts;

import io.rightpad.daxplorer.visualization.ChartPanel;

import java.awt.*;

public interface Chart
{
    void draw(ChartPanel panel, Graphics2D g2d);
}
