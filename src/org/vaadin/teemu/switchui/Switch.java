package org.vaadin.teemu.switchui;

import com.vaadin.data.Property;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.CheckBox;

/**
 * Switch is basically a decorated CheckBox. Server-side API has all the same
 * functionality and added support for enabling and disabling animation.
 * 
 * @see com.vaadin.ui.CheckBox
 * @author Teemu Pöntelin | IT Mill Ltd. | http://vaadin.com/teemu
 */
@com.vaadin.ui.ClientWidget(org.vaadin.teemu.switchui.widgetset.client.ui.VSwitch.class)
@SuppressWarnings("serial")
public class Switch extends CheckBox {

    private boolean animated = true;

    public Switch() {
        super();
    }

    public Switch(String caption, boolean initialState) {
        super(caption, initialState);
    }

    public Switch(String caption, ClickListener listener) {
        super(caption, listener);
    }

    public Switch(String caption, Object target, String methodName) {
        super(caption, target, methodName);
    }

    public Switch(String caption, Property dataSource) {
        super(caption, dataSource);
    }

    public Switch(String caption) {
        super(caption);
    }

    public boolean isAnimationEnabled() {
        return animated;
    }

    public void setAnimationEnabled(boolean enable) {
        if (animated != enable) {
            animated = enable;
            requestRepaint();
        }
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        target.addAttribute("animated", isAnimationEnabled());
    }
}
