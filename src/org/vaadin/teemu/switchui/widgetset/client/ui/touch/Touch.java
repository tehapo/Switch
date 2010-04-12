package org.vaadin.teemu.switchui.widgetset.client.ui.touch;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Simple wrapper around the native Touch object with only getters for
 * <code>pageX</code> and <code>pageY</code> currently implemented.
 */
public class Touch extends JavaScriptObject {

    /**
     * Protected constructor required by GWT.
     */
    protected Touch() {

    }

    public final native int getPageX()
    /*-{
        return this.pageX;
    }-*/;

    public final native int getPageY()
    /*-{
        return this.pageY;
    }-*/;

}
