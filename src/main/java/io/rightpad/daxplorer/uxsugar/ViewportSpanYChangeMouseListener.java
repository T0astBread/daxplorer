package io.rightpad.daxplorer.uxsugar;

import io.rightpad.daxplorer.visualization.VisualizationPanel;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class ViewportSpanYChangeMouseListener implements MouseWheelListener
{
    private VisualizationPanel visualizationPanel;
    private int heightChangePerScroll = 50;

    public ViewportSpanYChangeMouseListener(VisualizationPanel visualizationPanel)
    {
        this.visualizationPanel = visualizationPanel;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        this.visualizationPanel.setChartHeight(this.visualizationPanel.getChartHeight() + e.getUnitsToScroll() * this.heightChangePerScroll);
    }
}
