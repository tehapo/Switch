package org.vaadin.teemu.switchui.client;

import org.vaadin.teemu.switchui.Switch;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.EventHelper;
import com.vaadin.client.VTooltip;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractFieldConnector;
import com.vaadin.client.ui.Icon;
import com.vaadin.shared.communication.FieldRpc.FocusAndBlurServerRpc;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.checkbox.CheckBoxServerRpc;

@SuppressWarnings("serial")
@Connect(Switch.class)
public class SwitchConnector extends AbstractFieldConnector implements
        FocusHandler, BlurHandler {

    private HandlerRegistration focusHandlerRegistration;
    private HandlerRegistration blurHandlerRegistration;

    @Override
    protected void init() {
        super.init();

        getWidget().addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                if (getState().checked != getWidget().getValue()) {
                    getState().checked = getWidget().getValue();
                    getRpcProxy(CheckBoxServerRpc.class).setChecked(
                            getState().checked, null);
                    if (getState().immediate) {
                        getConnection().sendPendingVariableChanges();
                    }
                }
            }
        });
    }

    @Override
    public SwitchState getState() {
        return (SwitchState) super.getState();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(SwitchWidget.class);
    }

    @Override
    public SwitchWidget getWidget() {
        return (SwitchWidget) super.getWidget();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        // This method is mostly copied from CheckBoxConnector.
        focusHandlerRegistration = EventHelper.updateFocusHandler(this,
                focusHandlerRegistration);
        blurHandlerRegistration = EventHelper.updateBlurHandler(this,
                blurHandlerRegistration);

        if (null != getState().errorMessage) {
            // getWidget().setAriaInvalid(true);

            if (getWidget().errorIndicatorElement == null) {
                getWidget().errorIndicatorElement = DOM.createSpan();
                getWidget().errorIndicatorElement.setInnerHTML("&nbsp;");
                DOM.setElementProperty(getWidget().errorIndicatorElement,
                        "className", "v-errorindicator");
                DOM.appendChild(getWidget().getElement(),
                        getWidget().errorIndicatorElement);
                DOM.sinkEvents(getWidget().errorIndicatorElement,
                        VTooltip.TOOLTIP_EVENTS | Event.ONCLICK);
            } else {
                getWidget().errorIndicatorElement.getStyle().clearDisplay();
            }
        } else if (getWidget().errorIndicatorElement != null) {
            getWidget().errorIndicatorElement.getStyle().setDisplay(
                    Display.NONE);

            // getWidget().setAriaInvalid(false);
        }

        // getWidget().setAriaRequired(isRequired());
        if (isReadOnly()) {
            getWidget().setEnabled(false);
        }

        if (getWidget().icon != null) {
            getWidget().getElement().removeChild(getWidget().icon.getElement());
            getWidget().icon = null;
        }
        Icon icon = getIcon();
        if (icon != null) {
            getWidget().icon = icon;
            DOM.insertChild(getWidget().getElement(), icon.getElement(), 1);
            icon.sinkEvents(VTooltip.TOOLTIP_EVENTS);
            icon.sinkEvents(Event.ONCLICK);
        }

        if (stateChangeEvent.isInitialStateChange()) {
            // Set the initial value without animation.
            getWidget().setAnimationEnabled(false);
        }
        getWidget().setValue(getState().checked);
        getWidget().setAnimationEnabled(getState().animated);

        getWidget().immediate = getState().immediate;
    }

    @Override
    public void onFocus(FocusEvent event) {
        // EventHelper.updateFocusHandler ensures that this is called only when
        // there is a listener on server side
        getRpcProxy(FocusAndBlurServerRpc.class).focus();
    }

    @Override
    public void onBlur(BlurEvent event) {
        // EventHelper.updateFocusHandler ensures that this is called only when
        // there is a listener on server side
        getRpcProxy(FocusAndBlurServerRpc.class).blur();
    }
}
