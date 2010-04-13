package org.vaadin.teemu.switchui;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class SwitchComponentDemo extends Application implements
        Property.ValueChangeListener {

    private Window mainWindow;
    private CheckBox checkBox;
    private List<Switch> allSwitches = new ArrayList<Switch>(3);
    private Panel mainPanel;

    @Override
    public void init() {
        initWindowAndDescription();
        initDemoPanel();
    }

    private void initWindowAndDescription() {
        mainWindow = new Window("Switch Component Demo");
        setMainWindow(mainWindow);

        VerticalLayout centerLayout = new VerticalLayout();
        centerLayout.setMargin(true);

        mainPanel = new Panel();
        mainPanel.setCaption("Switch Component Demo");
        mainPanel.setWidth("750px");
        centerLayout.addComponent(mainPanel);
        centerLayout.setComponentAlignment(mainPanel, Alignment.TOP_CENTER);
        mainWindow.setContent(centerLayout);

        StringBuilder descriptionXhtml = new StringBuilder();
        descriptionXhtml
                .append("<p>Switch is a decorated checkbox inspired by the iPhone.</p>");
        descriptionXhtml
                .append("<p>Download and rate this component at <a href=\"http://vaadin.com/directory#addon/9\">Vaadin Directory</a>.</p>");
        descriptionXhtml.append("<p>The value can be changed by:</p>");
        descriptionXhtml.append("<ul>");
        descriptionXhtml.append("<li>Clicking the Switch</li>");
        descriptionXhtml.append("<li>Dragging the Switch with mouse</li>");
        descriptionXhtml
                .append("<li>Focusing the Switch with <i>tab</i> key and pressing <i>space</i></li>");
        descriptionXhtml
                .append("<li>Dragging the Switch with your finger (Mobile Safari)</li>");
        descriptionXhtml.append("</ul>");

        Label description = new Label(descriptionXhtml.toString(),
                Label.CONTENT_XHTML);
        mainPanel.addComponent(description);
    }

    private void initDemoPanel() {
        Panel demoPanel = new Panel("Demo");
        GridLayout demoLayout = new GridLayout(5, 2);
        demoLayout.setSpacing(true);
        demoLayout.setMargin(true);
        demoPanel.setContent(demoLayout);
        mainPanel.addComponent(demoPanel);

        checkBox = new CheckBox("Animated?", true);
        checkBox.addListener(this);
        checkBox.setImmediate(true);
        demoLayout.addComponent(checkBox, 0, 0, 4, 0);

        Switch plainSwitch = new Switch("Switch 1");
        plainSwitch.setImmediate(true);
        plainSwitch.addListener(this);
        allSwitches.add(plainSwitch);
        demoLayout.addComponent(plainSwitch);

        Switch plainSwitch2 = new Switch("Switch 2");
        plainSwitch2.setValue(true);
        plainSwitch2.setImmediate(true);
        plainSwitch2.addListener(this);
        allSwitches.add(plainSwitch2);
        demoLayout.addComponent(plainSwitch2);

        Switch disabledSwitch = new Switch("Disabled");
        disabledSwitch.setEnabled(false);
        disabledSwitch.addListener(this);
        allSwitches.add(disabledSwitch);
        demoLayout.addComponent(disabledSwitch);

        Switch readOnlySwitch = new Switch("Read-only", true);
        readOnlySwitch.setReadOnly(true);
        readOnlySwitch.addListener(this);
        allSwitches.add(readOnlySwitch);
        demoLayout.addComponent(readOnlySwitch);

        Switch validatorSwitch = new Switch("Validator", true);
        validatorSwitch
                .addValidator(new AbstractValidator("Only ON is valid!") {
                    public boolean isValid(Object value) {
                        return (Boolean) value;
                    }
                });
        validatorSwitch.setImmediate(true);
        validatorSwitch.addListener(this);
        allSwitches.add(validatorSwitch);
        demoLayout.addComponent(validatorSwitch);
    }

    public void valueChange(ValueChangeEvent event) {
        if (event.getProperty() == checkBox) {
            for (Switch s : allSwitches) {
                s.setAnimationEnabled(checkBox.booleanValue());
            }
        } else if (event.getProperty() instanceof Switch) {
            mainWindow.showNotification(((Switch) event.getProperty())
                    .getCaption()
                    + ": " + event.getProperty().getValue());
        }
    }

}
