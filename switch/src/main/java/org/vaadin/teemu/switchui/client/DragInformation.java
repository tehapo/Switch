package org.vaadin.teemu.switchui.client;

public class DragInformation {

    private boolean dragging;
    private int dragStartX;
    private int dragStartOffset;

    public boolean isDragging() {
        return dragging;
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    public int getDragStartX() {
        return dragStartX;
    }

    public void setDragStartX(int dragStartX) {
        this.dragStartX = dragStartX;
    }

    public int getDragDistanceX(int currentX) {
        return currentX - getDragStartX();
    }

    public void setDragStartOffsetLeft(int offsetLeft) {
        dragStartOffset = offsetLeft;
    }

    public int getDragStartOffsetLeft() {
        return dragStartOffset;
    }

}
