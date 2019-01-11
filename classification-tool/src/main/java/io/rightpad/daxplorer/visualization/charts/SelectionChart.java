package io.rightpad.daxplorer.visualization.charts;

import io.rightpad.daxplorer.visualization.ChartPanel;

import java.awt.*;

public class SelectionChart implements Chart
{
    private boolean isSelected;
    private float selectionStart, selectionEnd;
    private Color color = new Color(93, 194, 81, 100);

    public boolean isSelected()
    {
        return isSelected;
    }

    public void setSelected(boolean selected)
    {
        isSelected = selected;
    }

    public float getSelectionStart()
    {
        return selectionStart;
    }

    public void setSelectionStart(float selectionStart)
    {
        this.selectionStart = selectionStart;
    }

    public float getSelectionEnd()
    {
        return selectionEnd;
    }

    public void setSelectionEnd(float selectionEnd)
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
        if(!this.isSelected)
            return;

        int startX = panel.toAbsoluteX(selectionStart);
        int endX = panel.toAbsoluteX(selectionEnd);
        int width = endX - startX;
        g2d.setColor(this.color);
        g2d.fillRect(startX, 0, width, panel.getHeight());
        g2d.fillRect(startX, 0, width, 10);
    }
}
