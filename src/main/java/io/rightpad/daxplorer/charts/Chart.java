package io.rightpad.daxplorer.charts;

import io.rightpad.daxplorer.ChartPanel;

import java.awt.*;

public interface Chart
{
    void draw(ChartPanel panel, Graphics2D g2d);
}
