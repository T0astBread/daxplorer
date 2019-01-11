package io.rightpad.daxplorer.visualization;

import io.rightpad.daxplorer.visualization.charts.Chart;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class ChartPanel extends JPanel
{
    private PointF position = new PointF(0, 0);
    private float chartWidth = 100, chartHeight = 100;
    private float stepWidthX;
    private Function<Float, String> xAxisLabelConverter;

    private List<ChartEntry> charts = new LinkedList<>();


    public ChartPanel()
    {
        setXAxisLabelConverter(null);
    }

    public PointF getPosition()
    {
        return position;
    }

    public void setPosition(PointF position)
    {
        System.out.println("Changing position from " + this.position + " to " + position);
        this.position = position;
        repaint();
    }

    public void setPositionY(float posY)
    {
        setPosition(new PointF(getPosition().getX(), posY));
    }

    public float getChartWidth()
    {
        return chartWidth;
    }

    public void setChartWidth(float chartWidth)
    {
        System.out.println("Changing chart width from " + this.chartWidth + " to " + chartWidth);
        this.chartWidth = chartWidth;
        updateStepWidthX();
        repaint();
    }

    private void updateStepWidthX()
    {
        this.stepWidthX = calcStepWidthX();
    }

    private float calcStepWidthX()
    {
        double width = 0;
        for(int i = -10; i < 10; i++) {
            double w = Math.pow(10, i);
            if(this.chartWidth - w < 0)
                break;
            width = w;
        }
        return (float) width;
    }

    public float getChartHeight()
    {
        return chartHeight;
    }

    public void setChartHeight(float chartHeight)
    {
        System.out.println("Changing chart height from " + this.chartHeight + " to " + chartHeight);
        this.chartHeight = chartHeight;
        repaint();
    }

    public Function<Float, String> getXAxisLabelConverter()
    {
        return xAxisLabelConverter;
    }

    public void setXAxisLabelConverter(Function<Float, String> xAxisLabelConverter)
    {
        this.xAxisLabelConverter = xAxisLabelConverter != null ? xAxisLabelConverter : Object::toString;
    }

    private float getWidthRelation()
    {
        return getWidth() / this.chartWidth;
    }

    private float getHeightRelation()
    {
        return getHeight() / this.chartHeight;
    }

    public int toAbsoluteX(float relX)
    {
        return (int) ((relX - this.position.getX()) * getWidthRelation());
    }

    public int toAbsoluteY(float relY)
    {
        return getHeight() - (int) ((relY - this.position.getY()) * getHeightRelation());
    }

    public float toRelativeX(int absX)
    {
        return absX / getWidthRelation() + this.position.getX();
    }

    public float toRelativeY(int absY)
    {
        return (getHeight() - absY) / getHeightRelation() + this.position.getY();
    }

    public void addChart(Chart chart)
    {
        System.out.println("Adding chart " + chart);
        this.charts.add(new ChartEntry(chart));
        repaint();
    }

    public void setChartEnabled(Chart chart, boolean enabled)
    {
        this.charts.stream()
                .filter(c -> c.chart == chart)
                .forEach(c -> c.enabled = enabled);
    }

    public void removeChart(Chart chart)
    {
        this.charts.removeIf(c -> c.chart == chart);
    }

    private static final Color AXIS_COLOR = Color.DARK_GRAY;
    private static final int AXIS_MAIN_LINE_STROKE_WIDTH = 3;
    private static final Stroke AXIS_MAIN_LINE_STROKE = new BasicStroke(AXIS_MAIN_LINE_STROKE_WIDTH);
    private static final Stroke AXIS_STEP_LINES_STROKE = new BasicStroke(2);

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        paintCharts(g2d);
        drawYAxis(g2d);
        drawXAxis(g2d);
    }

    private void paintCharts(Graphics2D g2d)
    {
        this.charts.forEach(c -> c.chart.draw(this, g2d));
    }

    private void drawYAxis(Graphics2D g2d)
    {
        g2d.setColor(AXIS_COLOR);
        g2d.setStroke(AXIS_MAIN_LINE_STROKE);
        int x = AXIS_MAIN_LINE_STROKE_WIDTH / 2;
        g2d.drawLine(x, 0, x, getHeight());

        g2d.setStroke(AXIS_STEP_LINES_STROKE);
        drawYStepLines(g2d);
    }

    private void drawYStepLines(Graphics2D g2d)
    {
        float stepWidth = this.chartHeight / 10;
        int start = (int) (this.position.getY() / stepWidth);
        int end = (int) ((this.position.getY() + this.chartHeight) / stepWidth) + 1;
        for(int i = start; i < end; i++) {
            int relStepLineY = (int) (i * stepWidth);
            int stepLineY = toAbsoluteY(relStepLineY);
            g2d.drawLine(0, stepLineY, 10, stepLineY);
            g2d.drawString(Float.toString(relStepLineY), 15, stepLineY + 4);
        }
    }

    private void drawXAxis(Graphics2D g2d)
    {
        int xAxisYCoord = getHeight() - 15;
        g2d.setStroke(AXIS_MAIN_LINE_STROKE);
        g2d.setColor(AXIS_COLOR);
        g2d.drawLine(0, xAxisYCoord, getWidth(), xAxisYCoord);

        g2d.setStroke(AXIS_STEP_LINES_STROKE);
        drawXStepLines(g2d);
    }

    private void drawXStepLines(Graphics2D g2d)
    {
        float stepWidth = this.stepWidthX;
        float start = this.position.getX() / stepWidth;
        float end = (this.position.getX() + this.chartWidth) / stepWidth + 1;
        for(int i = (int) start; i < end; i++) {
            float relStepLineX = i * stepWidth;
            int stepLineX = toAbsoluteX(relStepLineX);
            g2d.drawLine(stepLineX, getHeight() - 10, stepLineX, getHeight() - 20);

            String label = this.xAxisLabelConverter.apply(relStepLineX);
            int labelWidth = g2d.getFontMetrics().stringWidth(label);
            g2d.drawString(label, stepLineX - labelWidth / 2, getHeight());
        }
    }

    private static class ChartEntry
    {
        final Chart chart;
        boolean enabled;

        public ChartEntry(Chart chart)
        {
            this.chart = chart;
            this.enabled = true;
        }
    }
}
