package io.rightpad.daxplorer;

import io.rightpad.daxplorer.visualization.VisualizationPanel;
import io.rightpad.daxplorer.visualization.visualizers.DateSelectionVisualizer;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class SelectionChangeMouseListener extends MouseMotionAdapter implements MouseListener
{
    private static final int SELECT_MOUSE_BUTTON = MouseEvent.BUTTON1;
    private static final int SELECT_BUTTON_MODIFIERS = MouseEvent.BUTTON1_DOWN_MASK;

    private VisualizationPanel visualizationPanel;
    private DateSelectionVisualizer selectionVisualizer;
    private Runnable onMouseDrag, onMouseRelease;

    private int dragStartX;

    public SelectionChangeMouseListener(VisualizationPanel visualizationPanel, DateSelectionVisualizer selectionVisualizer)
    {
        this.visualizationPanel = visualizationPanel;
        this.selectionVisualizer = selectionVisualizer;

        this.visualizationPanel.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        this.visualizationPanel.setFocusable(true);
    }

    public Runnable getOnMouseDrag()
    {
        return onMouseDrag;
    }

    public void setOnMouseDrag(Runnable onMouseDrag)
    {
        this.onMouseDrag = onMouseDrag;
    }

    public Runnable getOnMouseRelease()
    {
        return onMouseRelease;
    }

    public void setOnMouseRelease(Runnable onMouseRelease)
    {
        this.onMouseRelease = onMouseRelease;
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        if(e.getModifiersEx() != SELECT_BUTTON_MODIFIERS)
            return;

        int x = e.getX();
        int minX = Math.min(x, this.dragStartX);
        int maxX = Math.max(x, this.dragStartX);
        LocalDateTime selectionStart = getDateFromCoordInPanel(minX);
        LocalDateTime selectionEnd = getDateFromCoordInPanel(maxX);
        this.selectionVisualizer.setSelectionStart(selectionStart);
        this.selectionVisualizer.setSelectionEnd(selectionEnd);

        if(this.onMouseDrag != null)
            this.onMouseDrag.run();

        this.visualizationPanel.visualize();
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        if(e.getButton() != SELECT_MOUSE_BUTTON)
            return;

        this.selectionVisualizer.setSelectionStart(null);
        this.selectionVisualizer.setSelectionEnd(null);
        this.visualizationPanel.visualize();
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        if(e.getButton() != SELECT_MOUSE_BUTTON)
            return;

        this.visualizationPanel.requestFocus();

        this.selectionVisualizer.setSelectionStart(null);
        this.selectionVisualizer.setSelectionEnd(null);
        this.visualizationPanel.visualize();

        this.dragStartX = e.getX();
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        if(this.onMouseRelease != null)
            this.onMouseRelease.run();
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
    }

    private LocalDateTime getDateFromCoordInPanel(int x)
    {
        float relX = this.visualizationPanel.toRelativeX(x);
        return UtilsKt.asEpochDays(relX, ZoneOffset.UTC);
    }
}
