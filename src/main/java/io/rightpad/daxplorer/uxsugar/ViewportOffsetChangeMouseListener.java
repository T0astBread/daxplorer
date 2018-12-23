package io.rightpad.daxplorer.uxsugar;

import io.rightpad.daxplorer.visualization.PointF;
import io.rightpad.daxplorer.visualization.VisualizationPanel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;

public class ViewportOffsetChangeMouseListener extends MouseMotionAdapter implements MouseListener
{
    private static final int OFFSET_DRAG_MOUSE_BUTTON = MouseEvent.BUTTON2;
    private static final int OFFSET_DRAG_MOUSE_MODIFIER = MouseEvent.BUTTON2_DOWN_MASK;

    private VisualizationPanel visualizationPanel;
    private Runnable onDrag;

    private float dragStartPosX;

    public ViewportOffsetChangeMouseListener(VisualizationPanel visualizationPanel)
    {
        this.visualizationPanel = visualizationPanel;
    }

    public Runnable getOnDrag()
    {
        return onDrag;
    }

    public void setOnDrag(Runnable onDrag)
    {
        this.onDrag = onDrag;
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        if(e.getModifiersEx() != OFFSET_DRAG_MOUSE_MODIFIER)
            return;

        float relX = this.visualizationPanel.toRelativeX(e.getX()) - this.visualizationPanel.getPosition().getX();
        this.visualizationPanel.setPosition(new PointF(
                this.dragStartPosX - relX,
                this.visualizationPanel.getPosition().getY()
        ));

        if(this.onDrag != null)
            this.onDrag.run();
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        if(e.getButton() != OFFSET_DRAG_MOUSE_BUTTON)
            return;

        this.dragStartPosX = this.visualizationPanel.toRelativeX(e.getX());
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
    }
}
