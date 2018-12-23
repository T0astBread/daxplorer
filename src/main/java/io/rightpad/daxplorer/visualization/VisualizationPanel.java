package io.rightpad.daxplorer.visualization;

import io.rightpad.daxplorer.UtilsKt;
import io.rightpad.daxplorer.data.IndexDataPoint;
import io.rightpad.daxplorer.visualization.visualizers.TimeSeriesVisualizer;
import io.rightpad.daxplorer.visualization.visualizers.Visualizer;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class VisualizationPanel extends ChartPanel
{
    private List<Visualizer> visualizers = new ArrayList<>();
    private List<IndexDataPoint> indexData;

    public void addVisualizers(Visualizer... visualizers)
    {
        Arrays.stream(visualizers).forEach(visualizer -> {
            if(visualizer instanceof TimeSeriesVisualizer)
                ((TimeSeriesVisualizer) visualizer).calculate(this.indexData);

            this.visualizers.add(visualizer);
            visualizer.getCharts().forEach(this::addChart);
        });
    }

    public void setIndexData(List<IndexDataPoint> indexData)
    {
        this.indexData = indexData;
        this.visualizers.stream()
                .filter(visualizer -> visualizer instanceof TimeSeriesVisualizer)
                .forEach(visualizer -> ((TimeSeriesVisualizer) visualizer).calculate(this.indexData));
        visualize();
    }

    public Stream<Visualizer> getVisualizers()
    {
        return this.visualizers.stream();
    }

    @Override
    public void setChartWidth(float chartWidth)
    {
        super.setChartWidth(chartWidth);
        visualize();
    }

    @Override
    public void setPosition(PointF position)
    {
        super.setPosition(position);
        visualize();
    }

    public void setTimeOffset(LocalDateTime offset)
    {
        setPosition(new PointF(
                UtilsKt.daysSinceEpoch(offset, ZoneOffset.UTC),
                getPosition().getY()
        ));
    }

    public void visualize()
    {
        visualize(getPosition().getX(), getPosition().getX() + getChartWidth());
        repaint();
    }

    private void visualize(float startX, float endX)
    {
        visualize(
                UtilsKt.asEpochDays((int) Math.floor(startX), ZoneOffset.UTC),
                UtilsKt.asEpochDays((int) Math.ceil(endX), ZoneOffset.UTC)
        );
    }

    private void visualize(LocalDateTime start, LocalDateTime end)
    {
        this.visualizers.forEach(visualizer -> visualizer.visualize(start, end));
    }
}
