package org.vaadin.teemu.switchui.widgetset.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Touch;
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
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasAnimation;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VTooltip;
import com.vaadin.terminal.gwt.client.ui.Icon;

/**
 * VSwitch is the client-side implementation of the Switch component.
 * 
 * @author Teemu Pöntelin | Vaadin Ltd. | http://vaadin.com/teemu
 */
public class VSwitch extends FocusWidget implements Paintable, KeyUpHandler,
        MouseDownHandler, MouseUpHandler, MouseMoveHandler, FocusHandler,
        BlurHandler, HasAnimation, TouchStartHandler, TouchEndHandler,
        TouchMoveHandler, TouchCancelHandler {

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
    private com.google.gwt.user.client.Element errorIndicatorElement;
    private Icon icon;
    private boolean immediate;

    private boolean mouseDown;
    private int unvisiblePartWidth = -1;
    private final DragInformation dragInfo = new DragInformation();

    private boolean animated;
    private int tabIndex;
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

        handlers.add(addTouchStartHandler(this));
        handlers.add(addTouchEndHandler(this));
        handlers.add(addTouchCancelHandler(this));
        handlers.add(addTouchMoveHandler(this));
    }

    private void removeHandlers() {
        if (handlers != null) {
            for (HandlerRegistration handler : handlers) {
                handler.removeHandler();
            }
            handlers = null;
        }
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
        }

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

        setValue(uidl.getBooleanVariable("state"), null);
        immediate = uidl.getBooleanAttribute("immediate");

        setAnimationEnabled(uidl.getBooleanAttribute("animated"));

        if (!isEnabled()) {
            removeHandlers();
        } else {
            if (handlers == null) {
                addHandlers();
            }
        }
        updateStyleName();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if (!enabled) {
            super.setTabIndex(-1);
        } else {
            super.setTabIndex(tabIndex);
        }
    }

    @Override
    public void setTabIndex(int index) {
        super.setTabIndex(index);
        tabIndex = index;
    }

    private void setValue(boolean value, NativeEvent event) {
        if (this.value == value) {
            return;
        }

        this.value = value;

        // update the UI
        updateVisibleState();

        if (event != null) {
            // Update the server state if the value change was initiated from an
            // UI event (event != null).
            if (paintableId == null || client == null) {
                return;
            }

            MouseEventDetails details = new MouseEventDetails(event,
                    getElement());
            client.updateVariable(paintableId, "mousedetails",
                    details.serialize(), false);

            client.updateVariable(paintableId, "state", this.value, immediate);
        }
    }

    private void updateVisibleState() {
        updateVisibleState(false);
    }

    private void updateVisibleState(boolean skipAnimation) {
        final int targetLeft = (value ? 0 : -unvisiblePartWidth);

        if (!isAnimationEnabled() || skipAnimation) {
            slider.getStyle().setProperty("left", targetLeft + "px");
            updateStyleName();
        } else {
            Animation a = new Animation() {

                @Override
                protected void onUpdate(double progress) {
                    int currentLeft = slider.getOffsetLeft();
                    int newLeft = (int) (currentLeft + (progress * (targetLeft - currentLeft)));
                    slider.getStyle().setProperty("left", newLeft + "px");
                }

                @Override
                protected void onComplete() {
                    super.onComplete();
                    updateStyleName();
                }
            };
            a.run(ANIMATION_DURATION_MS);
        }
    }

    private void updateStyleName() {
        if (value) {
            addStyleName("on");
            removeStyleName("off");
        } else {
            addStyleName("off");
            removeStyleName("on");
        }
    }

    public void onKeyUp(KeyUpEvent event) {
        if (event.getNativeKeyCode() == 32) {
            // 32 = space bar
            setValue(!value, event.getNativeEvent());
        }
    }

    public void onMouseDown(MouseDownEvent event) {
        handleMouseDown(event.getScreenX());
        event.preventDefault();
    }

    private void handleMouseDown(int clientX) {
        mouseDown = true;
        dragInfo.setDragStartX(clientX);
        dragInfo.setDragStartOffsetLeft(slider.getOffsetLeft());
    }

    public void onMouseUp(MouseUpEvent event) {
        handleMouseUp(event.getNativeEvent());
    }

    private void handleMouseUp(NativeEvent event) {
        if (!dragInfo.isDragging()) {
            setValue(!value, event);
        } else {
            if (slider.getOffsetLeft() < (-unvisiblePartWidth / 2)) {
                setValue(false, event);
            } else {
                setValue(true, event);
            }
            updateVisibleState();
            DOM.releaseCapture(getElement());
        }

        mouseDown = false;
        dragInfo.setDragging(false); // not dragging anymore
    }

    public void onMouseMove(MouseMoveEvent event) {
        handleMouseMove(event.getScreenX());
    }

    private void handleMouseMove(int clientX) {
        if (mouseDown) {
            if (Math.abs(dragInfo.getDragDistanceX(clientX)) > DRAG_THRESHOLD_PIXELS) {
                dragInfo.setDragging(true);
                // Use capture to catch mouse events even if user
                // drags the mouse cursor out of the widget area.
                DOM.setCapture(getElement());
            }

            if (dragInfo.isDragging()) {
                int dragDistance = dragInfo.getDragDistanceX(clientX);

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

    public void onFocus(FocusEvent event) {
        addStyleDependentName("focus");
    }

    public void onBlur(BlurEvent event) {
        removeStyleDependentName("focus");
    }

    public boolean isAnimationEnabled() {
        return animated;
    }

    public void setAnimationEnabled(boolean enable) {
        animated = enable;
    }

    public void onTouchCancel(TouchCancelEvent event) {
        handleMouseUp(event.getNativeEvent());
    }

    public void onTouchMove(TouchMoveEvent event) {
        Touch touch = event.getTouches().get(0).cast();
        handleMouseMove(touch.getPageX());
        event.preventDefault();
    }

    public void onTouchEnd(TouchEndEvent event) {
        handleMouseUp(event.getNativeEvent());
    }

    public void onTouchStart(TouchStartEvent event) {
        Touch touch = event.getTouches().get(0).cast();
        handleMouseDown(touch.getPageX());
        event.preventDefault();
    }
}
