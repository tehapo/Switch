package com.vaadin.teemu.switchui.widgetset.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasAnimation;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VTooltip;
import com.vaadin.terminal.gwt.client.ui.Icon;

/**
 * VSwitch is the client-side implementation of the Switch component.
 * 
 * @author Teemu Pöntelin | IT Mill Ltd. | http://vaadin.com/teemu
 */
public class VSwitch extends FocusWidget implements Paintable, KeyUpHandler,
        MouseDownHandler, MouseUpHandler, MouseMoveHandler, FocusHandler,
        BlurHandler, HasAnimation {

    /** Set the CSS class name to allow styling. */
    public static final String CLASSNAME = "v-switch";
    private final int DRAG_THRESHOLD_PIXELS = 10;
    private final int ANIMATION_DURATION_MS = 300;

    /** The client side widget identifier */
    protected String paintableId;

    /** Reference to the server connection object. */
    ApplicationConnection client;

    private Element slider;
    private boolean value;
    private Element errorIndicatorElement;
    private Icon icon;
    private boolean immediate;

    private boolean mouseDown;
    private int unvisiblePartWidth = -1;
    private final DragInformation dragInfo = new DragInformation();

    private boolean animated;
    private List<HandlerRegistration> handlers;

    /**
     * The constructor should first call super() to initialize the component and
     * then handle any initialization relevant to Vaadin.
     */
    public VSwitch() {
        // Change to proper element or remove if extending another widget
        setElement(Document.get().createDivElement());

        // This method call of the Paintable interface sets the component
        // style name in DOM tree
        setStyleName(CLASSNAME);

        // build the DOM
        slider = Document.get().createDivElement();
        slider.setClassName(CLASSNAME + "-" + "slider");
        getElement().appendChild(slider);

        addHandlers();
    }

    private void addHandlers() {
        handlers = new ArrayList<HandlerRegistration>();
        handlers.add(addKeyUpHandler(this));
        handlers.add(addMouseMoveHandler(this));
        handlers.add(addMouseDownHandler(this));
        handlers.add(addMouseUpHandler(this));
        handlers.add(addFocusHandler(this));
        handlers.add(addBlurHandler(this));
    }

    private void removeHandlers() {
        for (HandlerRegistration handler : handlers) {
            handler.removeHandler();
        }
        handlers = null;
    }

    /**
     * Called whenever an update is received from the server
     */
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // This call should be made first.
        // It handles sizes, captions, tooltips, etc. automatically.
        if (client.updateComponent(this, uidl, true)) {
            // If client.updateComponent returns true there has been no changes
            // and we do not need to update anything.
            return;
        }

        if (this.client == null) {
            // Calculate the width of the part that is not currently visible
            // and init the state on the first rendering.
            int width = this.getElement().getClientWidth();
            int sliderWidth = this.slider.getClientWidth();
            unvisiblePartWidth = sliderWidth - width;

            boolean skipAnimation = true;
            updateVisibleState(skipAnimation);
        }

        // Save reference to server connection object to be able to send
        // user interaction later
        this.client = client;

        // Save the client side identifier (paintable id) for the widget
        paintableId = uidl.getId();

        // All the following is mostly just copied from VCheckBox
        /*-
        if (uidl.hasAttribute("error")) {
            if (errorIndicatorElement == null) {
                errorIndicatorElement = DOM.createSpan();
                errorIndicatorElement.setInnerHTML("&nbsp;");
                DOM.setElementProperty(errorIndicatorElement, "className",
                        "v-errorindicator");
                DOM.appendChild(getElement(), errorIndicatorElement);
                DOM.sinkEvents(errorIndicatorElement, VTooltip.TOOLTIP_EVENTS
                        | Event.ONCLICK);
            }
        } else if (errorIndicatorElement != null) {
            DOM.setStyleAttribute(errorIndicatorElement, "display", "none");
        }*/

        if (uidl.hasAttribute("readonly")) {
            setEnabled(false);
        }

        if (uidl.hasAttribute("icon")) {
            if (icon == null) {
                icon = new Icon(client);
                DOM.insertChild(getElement(), icon.getElement(), 1);
                icon.sinkEvents(VTooltip.TOOLTIP_EVENTS);
                icon.sinkEvents(Event.ONCLICK);
            }
            icon.setUri(uidl.getStringAttribute("icon"));
        } else if (icon != null) {
            // detach icon
            DOM.removeChild(getElement(), icon.getElement());
            icon = null;
        }

        // Set text
        // setText(uidl.getStringAttribute("caption"));
        setValue(uidl.getBooleanVariable("state"));
        immediate = uidl.getBooleanAttribute("immediate");

        setAnimationEnabled(uidl.getBooleanAttribute("animated"));

        if (!isEnabled()) {
            removeHandlers();
        } else {
            if (handlers == null) {
                addHandlers();
            }
        }
    }

    private void setValue(boolean value) {
        if (this.value == value || !isEnabled()) {
            return;
        }

        // update the server state
        if (paintableId == null || client == null) {
            return;
        }
        this.value = value;
        client.updateVariable(paintableId, "state", this.value, immediate);

        // update the UI
        updateVisibleState();
    }

    private void updateVisibleState() {
        updateVisibleState(false);
    }

    private void updateVisibleState(boolean skipAnimation) {
        final int targetLeft = (value ? 0 : -unvisiblePartWidth);

        if (!isAnimationEnabled() || skipAnimation) {
            slider.getStyle().setProperty("left", targetLeft + "px");
        } else {
            Animation a = new Animation() {

                @Override
                protected void onUpdate(double progress) {
                    int currentLeft = slider.getOffsetLeft();
                    int newLeft = (int) (currentLeft + (progress * (targetLeft - currentLeft)));
                    slider.getStyle().setProperty("left", newLeft + "px");
                }
            };
            a.run(ANIMATION_DURATION_MS);
        }
    }

    @Override
    public void onKeyUp(KeyUpEvent event) {
        if (event.getNativeKeyCode() == 32) {
            // 32 = space bar
            setValue(!value);
        }
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        mouseDown = true;
        dragInfo.setDragStartX(event.getClientX());
        dragInfo.setDragStartOffsetLeft(slider.getOffsetLeft());
        event.preventDefault();
    }

    @Override
    public void onMouseUp(MouseUpEvent event) {
        if (!dragInfo.isDragging()) {
            setValue(!value);
        } else {
            if (slider.getOffsetLeft() < (-unvisiblePartWidth / 2)) {
                setValue(false);
            } else {
                setValue(true);
            }
            updateVisibleState();
            DOM.releaseCapture(getElement());
        }

        mouseDown = false;
        dragInfo.setDragging(false); // not dragging anymore
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        if (mouseDown) {
            if (Math.abs(dragInfo.getDragDistanceX(event.getClientX())) > DRAG_THRESHOLD_PIXELS) {
                dragInfo.setDragging(true);
                // Use capture to catch mouse events even if user
                // drags the mouse cursor out of the widget area.
                DOM.setCapture(getElement());
            }

            if (dragInfo.isDragging()) {
                int dragDistance = dragInfo
                        .getDragDistanceX(event.getClientX());

                // calculate new left position and
                // check for boundaries
                int left = dragInfo.getDragStartOffsetLeft() + dragDistance;
                if (left < -unvisiblePartWidth) {
                    left = -unvisiblePartWidth;
                } else if (left > 0) {
                    left = 0;
                }

                // set the CSS left
                slider.getStyle().setProperty("left", left + "px");
            }
        }
    }

    @Override
    public void onFocus(FocusEvent event) {
        addStyleDependentName("focus");
    }

    @Override
    public void onBlur(BlurEvent event) {
        removeStyleDependentName("focus");
    }

    @Override
    public boolean isAnimationEnabled() {
        return animated;
    }

    @Override
    public void setAnimationEnabled(boolean enable) {
        animated = enable;
    }

}
