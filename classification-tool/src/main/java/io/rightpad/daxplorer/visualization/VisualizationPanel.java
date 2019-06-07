package io.rightpad.daxplorer.visualization;

import io.rightpad.daxplorer.ConstKt;
import io.rightpad.daxplorer.data.datapoints.absolute.IndexDataPoint;
import io.rightpad.daxplorer.utils.UtilsKt;
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

    public VisualizationPanel()
    {
        setXAxisLabelConverter(x ->
                UtilsKt.asEpochDays(x, ZoneOffset.UTC)
                        .plusDays(1) // needed, so that the step line marks the start of the next day, not the end of the previous
                        .format(ConstKt.getDATE_FORMAT())
        );
    }

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
        float oldWidth = getChartWidth();
        super.setChartWidth(chartWidth);
        float newWidth = getChartWidth();
        float x = getPosition().getX();
        updateViewportEnd(x + oldWidth, x + newWidth);
//        visualize();
    }

    @Override
    public void setPosition(PointF position)
    {
        float oldX = getPosition().getX();
        super.setPosition(position);
        float newX = getPosition().getX();

        float width = getChartWidth();
        float padding = width * .1f;

        updateViewportStart(oldX - padding, newX - padding);
        updateViewportEnd(oldX + width + padding, newX + width + padding);
//        visualize();
    }

    public void setTimeOffset(LocalDateTime offset)
    {
        setPosition(new PointF(
                UtilsKt.daysSinceEpoch(offset, ZoneOffset.UTC),
                getPosition().getY()
        ));
    }

    private void updateViewportEnd(float oldEndX, float newEndX)
    {
        if(oldEndX > newEndX)
            removeFromViewport((int) Math.ceil(newEndX), (int) Math.ceil(oldEndX));
        else
//        if(oldEndX < newEndX)
            addToViewport(oldEndX, newEndX);
    }

    private void updateViewportStart(float oldStartX, float newStartX)
    {
        if(oldStartX > newStartX)
            addToViewport(newStartX, oldStartX);
        else
            removeFromViewport((int) Math.floor(oldStartX), (int) Math.floor(newStartX));
    }

    private void removeFromViewport(int startX, int endX)
    {
        removeFromViewport(
                UtilsKt.asEpochDays(startX, ZoneOffset.UTC),
                UtilsKt.asEpochDays(endX, ZoneOffset.UTC)
        );
    }

    private void removeFromViewport(LocalDateTime start, LocalDateTime end)
    {
        this.visualizers.forEach(visualizer -> visualizer.destroy(start, end));
        repaint();
    }

    private void addToViewport(float startX, float endX)
    {
        addToViewport(
                UtilsKt.asEpochDays((int) Math.floor(startX), ZoneOffset.UTC),
                UtilsKt.asEpochDays((int) Math.ceil(endX), ZoneOffset.UTC)
        );
    }

    private void addToViewport(LocalDateTime start, LocalDateTime end)
    {
        this.visualizers.forEach(visualizer -> visualizer.construct(start, end));
        repaint();
    }

    public void visualize()
    {
//        visualize(getPosition().getX(), getPosition().getX() + getChartWidth());
        int startX = (int) Math.floor(getPosition().getX());
        int endX = (int) Math.ceil(startX + getChartWidth());
        removeFromViewport(startX, endX);
        addToViewport(startX, endX);
        repaint();
    }
//
//    private void visualize(float startX, float endX)
//    {
//        visualize(
//                UtilsKt.asEpochDays((int) Math.floor(startX), ZoneOffset.UTC),
//                UtilsKt.asEpochDays((int) Math.ceil(endX), ZoneOffset.UTC)
//        );
//    }
//
//    private void visualize(LocalDateTime start, LocalDateTime end)
//    {
//        this.visualizers.forEach(visualizer -> visualizer.visualize(start, end));
//    }
}
