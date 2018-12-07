package io.rightpad.daxplorer.charts;

import io.rightpad.daxplorer.PointF;
import io.rightpad.daxplorer.ChartPanel;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class LineChart implements Chart
{
    private List<LinePoint> points = new LinkedList<>();
    private Stroke stroke = new BasicStroke(2);

    public Stroke getStroke()
    {
        return stroke;
    }

    public void setStroke(Stroke stroke)
    {
        this.stroke = stroke;
    }

    public void addPoint(float x, float y, Color lineColor)
    {
        this.points.add(new LinePoint(x, y, lineColor));
        sortPoints();
    }

    private void sortPoints()
    {
        this.points.sort((a, b) -> Float.compare(a.getX(), b.getX()));
    }

    @Override
    public void draw(ChartPanel panel, Graphics2D g2d)
    {
        LinePoint prev = null;
        for(LinePoint curr : this.points) {
            if(prev != null) {
                g2d.setColor(curr.lineColor);
                g2d.setStroke(getStroke());
                g2d.drawLine(
                        panel.toAbsoluteX(prev.getX()),
                        panel.toAbsoluteY(prev.getY()),
                        panel.toAbsoluteX(curr.getX()),
                        panel.toAbsoluteY(curr.getY())
                );
            }
            prev = curr;
        }
    }

    private static class LinePoint extends PointF
    {
        private Color lineColor;

        private LinePoint(float x, float y, Color lineColor)
        {
            super(x, y);
            this.lineColor = lineColor;
        }
    }
}
