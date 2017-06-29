package org.vaadin.teemu.switchui.demo;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.teemu.switchui.Switch;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Widgetset("org.vaadin.teemu.switchui.demo.SwitchDemoWidgetset")
@Theme("reindeer")
public class SwitchComponentDemo extends UI implements
        Property.ValueChangeListener {

    private CheckBox checkBox;
    private List<Switch> allSwitches = new ArrayList<Switch>(3);
    private Panel main = new Panel();

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setMargin(true);
        setContent(wrapper);

        main.setWidth("750px");
        wrapper.addComponent(main);
        wrapper.setComponentAlignment(main, Alignment.TOP_CENTER);

        VerticalLayout content = new VerticalLayout();
        content.setMargin(true);
        main.setContent(content);
        content.addComponent(createDescription());
        content.addComponent(createDemoPanel(null));
        content.addComponent(createDemoPanel("compact"));
        content.addComponent(createDemoPanel("holodeck"));
        content.addComponent(createDemoPanel(Switch.DOM_STYLE));
    }

    private Label createDescription() {
        StringBuilder descriptionXhtml = new StringBuilder();
        descriptionXhtml.append("<h1>Switch Component Demo</h1>");
        descriptionXhtml
                .append("<p>Switch is a decorated checkbox similar to ones on mobile platforms.");
        descriptionXhtml
                .append("<br />Download and rate this component at <a href=\"https://vaadin.com/addon/switch\">Vaadin Directory</a>.</p>");
        descriptionXhtml.append("<p>The value can be changed by:</p>");
        descriptionXhtml.append("<ul>");
        descriptionXhtml.append("<li>Clicking the Switch</li>");
        descriptionXhtml.append("<li>Dragging the Switch with mouse</li>");
        descriptionXhtml
                .append("<li>Focusing the Switch with <i>tab</i> key and pressing <i>space</i></li>");
        descriptionXhtml
                .append("<li>Dragging or tapping the Switch with your finger (Mobile Safari, Android)</li>");
        descriptionXhtml.append("</ul>");

        return new Label(descriptionXhtml.toString(), ContentMode.HTML);
    }

    private Panel createDemoPanel(String switchStyle) {
        Panel demoPanel = new Panel("Demonstration ("
                + (switchStyle == null ? "default" : '"' + switchStyle + '"')
                + " style)");
        GridLayout demoLayout = new GridLayout(6, 2);
        demoLayout.setSpacing(true);
        demoLayout.setMargin(true);
        demoPanel.setContent(demoLayout);

        if (checkBox == null) {
            checkBox = new CheckBox("Animated?", true);
            checkBox.addValueChangeListener(this);
            checkBox.setImmediate(true);
            demoLayout.addComponent(checkBox, 0, 0, 5, 0);
        }

        Switch plainSwitch = createSwitch("Switch 1", switchStyle, true);
        demoLayout.addComponent(plainSwitch);

        Switch plainSwitch2 = createSwitch("Switch 2", switchStyle, false);
        demoLayout.addComponent(plainSwitch2);

        Switch disabledSwitch = createSwitch("Disabled", switchStyle, true);
        disabledSwitch.setEnabled(false);
        demoLayout.addComponent(disabledSwitch);

        Switch readOnlySwitch = createSwitch("Read-only", switchStyle, true);
        readOnlySwitch.setReadOnly(true);
        demoLayout.addComponent(readOnlySwitch);

        Switch readOnlySwitch2 = createSwitch("Read-only", switchStyle, false);
        readOnlySwitch2.setReadOnly(true);
        demoLayout.addComponent(readOnlySwitch2);

        Switch validatorSwitch = createSwitch("Validator", switchStyle, true);
        validatorSwitch.addValidator(new AbstractValidator<Boolean>(
                "Only ON is valid!") {
            @Override
            protected boolean isValidValue(Boolean value) {
                return value;
            }

            @Override
            public Class<Boolean> getType() {
                return Boolean.class;
            }
        });
        demoLayout.addComponent(validatorSwitch);
        return demoPanel;
    }

    private Switch createSwitch(String caption, String style,
            boolean initialState) {
        Switch switchComponent = new Switch(caption, initialState);
        switchComponent.addValueChangeListener(this);
        switchComponent.setImmediate(true);
        if (style != null) {
            switchComponent.setStyleName(style);
        }
        allSwitches.add(switchComponent);
        return switchComponent;
    }

    @Override
    public void valueChange(ValueChangeEvent event) {
        if (event.getProperty() == checkBox) {
            for (Switch s : allSwitches) {
                s.setAnimationEnabled(checkBox.getValue());
            }
        } else if (event.getProperty() instanceof Switch) {
            Notification.show(((Switch) event.getProperty()).getCaption()
                    + ": " + event.getProperty().getValue());
        }
    }

}
