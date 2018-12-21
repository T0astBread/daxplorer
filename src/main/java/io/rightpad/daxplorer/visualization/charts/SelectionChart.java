package io.rightpad.daxplorer.visualization.charts;

import io.rightpad.daxplorer.visualization.ChartPanel;

import java.awt.*;

public class SelectionChart implements Chart
{
    private int selectionStart, selectionEnd;
    private Color color = new Color(93, 194, 81, 100);

    public int getSelectionStart()
    {
        return selectionStart;
    }

    public void setSelectionStart(int selectionStart)
    {
        this.selectionStart = selectionStart;
    }

    public int getSelectionEnd()
    {
        return selectionEnd;
    }

    public void setSelectionEnd(int selectionEnd)
    {
        this.selectionEnd = selectionEnd;
    }

    public Color getColor()
    {
        return color;
    }

    public void setColor(Color color)
    {
        this.color = color;
    }

    @Override
    public void draw(ChartPanel panel, Graphics2D g2d)
    {
        int startX = panel.toAbsoluteX(selectionStart);
        int endX = panel.toAbsoluteX(selectionEnd);
        int width = endX - startX;
        g2d.setColor(this.color);
        g2d.fillRect(startX, 0, width, panel.getHeight());
        g2d.fillRect(startX, 0, width, 10);
    }
}
