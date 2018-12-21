package io.rightpad.daxplorer.visualization;

import io.rightpad.daxplorer.data.TimeSeriesDataPoint;
import io.rightpad.daxplorer.visualization.visualizers.Visualizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class VisualizationPanel extends ChartPanel
{
    private List<Visualizer> visualizers = new ArrayList<>();

    public void addVisualizers(Visualizer... visualizers)
    {
        this.visualizers.addAll(Arrays.asList(visualizers));
    }

    public Stream<Visualizer> getVisualizers()
    {
        return this.visualizers.stream();
    }
}
