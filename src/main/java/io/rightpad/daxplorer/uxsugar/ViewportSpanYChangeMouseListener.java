package io.rightpad.daxplorer.uxsugar;

import io.rightpad.daxplorer.visualization.VisualizationPanel;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class ViewportSpanYChangeMouseListener implements MouseWheelListener
{
    private VisualizationPanel visualizationPanel;
    private int heightChangePerScroll = 50;
    private Runnable onScroll;

    public ViewportSpanYChangeMouseListener(VisualizationPanel visualizationPanel)
    {
        this.visualizationPanel = visualizationPanel;
    }

    public Runnable getOnScroll()
    {
        return onScroll;
    }

    public void setOnScroll(Runnable onScroll)
    {
        this.onScroll = onScroll;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        this.visualizationPanel.setChartHeight(this.visualizationPanel.getChartHeight() + e.getUnitsToScroll() * this.heightChangePerScroll);

        if(this.onScroll != null)
            this.onScroll.run();
    }
}
