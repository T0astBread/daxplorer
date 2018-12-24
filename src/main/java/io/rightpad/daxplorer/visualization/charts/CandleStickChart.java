package io.rightpad.daxplorer.visualization.charts;

import io.rightpad.daxplorer.visualization.ChartPanel;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;

public class CandleStickChart implements Chart
{
    public static final Stroke CANDLE_STICK_STROKE = new BasicStroke(1);

    private List<CandleStick> candleSticks = new ArrayList<>();
    private float candleStickWidth = .25f;

    public void addCandleStick(CandleStick c)
    {
        this.candleSticks.add(c);
    }

    public void clearCandleSticks()
    {
        this.candleSticks.clear();
    }

    @Override
    public void draw(ChartPanel panel, Graphics2D g2d)
    {
        this.candleSticks.forEach(candleStick -> drawCandleStick(candleStick, panel, g2d));
    }

    private void drawCandleStick(CandleStick cs, ChartPanel cp, Graphics2D g2d)
    {
        g2d.setStroke(CANDLE_STICK_STROKE);

        float startEndMin = Math.min(cs.start, cs.end);
        float startEndMax = Math.max(cs.start, cs.end);

        int absLeftCandleStickX = cp.toAbsoluteX(cs.x - this.candleStickWidth);
        int absRightCandleStickX = cp.toAbsoluteX(cs.x + this.candleStickWidth);

        int absCandleStickStartEndMin = cp.toAbsoluteY(startEndMin);
        int absCandleStickStartEndMax = cp.toAbsoluteY(startEndMax);

        int candleStickBodyWidth = absRightCandleStickX - absLeftCandleStickX;
        int candleStickBodyHeight = Math.abs(cp.toAbsoluteY(cs.end) - cp.toAbsoluteY(cs.start));

        g2d.setColor(cs.bodyColor);
        g2d.fillRect(
                absLeftCandleStickX,
                absCandleStickStartEndMax,
                candleStickBodyWidth,
                candleStickBodyHeight
        );
        g2d.setColor(cs.strokeColor);
        g2d.drawRect(
                absLeftCandleStickX,
                absCandleStickStartEndMax,
                candleStickBodyWidth,
                candleStickBodyHeight
        );

        int absCandleStickX = cp.toAbsoluteX(cs.x);

        int absCandleStickMin = cp.toAbsoluteY(cs.min);
        g2d.drawLine(
                absCandleStickX,
                absCandleStickStartEndMin,
                absCandleStickX,
                absCandleStickMin
        );

        int absCandleStickMax = cp.toAbsoluteY(cs.max);
        g2d.drawLine(
                absCandleStickX,
                absCandleStickStartEndMax,
                absCandleStickX,
                absCandleStickMax
        );

        g2d.drawLine(
                absLeftCandleStickX,
                absCandleStickMin,
                absRightCandleStickX,
                absCandleStickMin
        );
        g2d.drawLine(
                absLeftCandleStickX,
                absCandleStickMax,
                absRightCandleStickX,
                absCandleStickMax
        );
    }

    public static class CandleStick {
        public final float x, start, end, min, max;
        public final Color strokeColor, bodyColor;

        public CandleStick(float x, float start, float end, float min, float max, Color strokeColor, Color bodyColor)
        {
            this.x = x;
            this.start = start;
            this.end = end;
            this.min = min;
            this.max = max;
            this.strokeColor = strokeColor;
            this.bodyColor = bodyColor;
        }
    }
}
