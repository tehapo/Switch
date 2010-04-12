package org.vaadin.teemu.switchui.widgetset.client.ui.touch;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.NativeEvent;

/**
 * Simple wrapper around the native TouchEvent object with only a getter for
 * <code>touches</code> array implemented.
 */
public class TouchEvent extends NativeEvent {

    /**
     * Protected constructor required by GWT.
     */
    protected TouchEvent() {

    }

    public final native JsArray<Touch> getTouches()
    /*-{
        return this.touches;
    }-*/;

}
