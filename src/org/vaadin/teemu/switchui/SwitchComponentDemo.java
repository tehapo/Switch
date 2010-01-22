package org.vaadin.teemu.switchui;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class SwitchComponentDemo extends Application implements
        Property.ValueChangeListener {

    private static final long serialVersionUID = 4713399207156787146L;
    private Window mainWindow;
    private CheckBox checkBox;
    private List<Switch> switches = new ArrayList<Switch>(3);

    @Override
    public void init() {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSpacing(true);
        mainLayout.setMargin(true);

        mainWindow = new Window("SwitchComponentDemo");
        mainWindow.setContent(mainLayout);
        Label label = new Label(
                "This is a demo application of the Switch component.");
        mainWindow.addComponent(label);

        checkBox = new CheckBox("Animated?", true);
        checkBox.addListener(this);
        checkBox.setImmediate(true);
        mainWindow.addComponent(checkBox);

        Switch switchComponent = new Switch("Initally false", false);
        switchComponent.setImmediate(true);
        switchComponent.addListener(this);
        switches.add(switchComponent);
        mainWindow.addComponent(switchComponent);
        Switch switchComponent2 = new Switch(
                "Initially true, initally focused", true);
        switchComponent2.setImmediate(true);
        switchComponent2.addListener(this);
        switchComponent2.focus();
        switches.add(switchComponent2);
        mainWindow.addComponent(switchComponent2);

        Switch switchComponent3 = new Switch("Disabled Switch");
        switchComponent3.setEnabled(false);
        switches.add(switchComponent3);
        mainWindow.addComponent(switchComponent3);

        Switch switchComponent4 = new Switch("Read-only Switch", true);
        switchComponent4.setReadOnly(true);
        switches.add(switchComponent4);
        mainWindow.addComponent(switchComponent4);

        setMainWindow(mainWindow);
    }

    public void valueChange(ValueChangeEvent event) {
        mainWindow.showNotification("valueChange, "
                + event.getProperty().getClass().getSimpleName() + ", "
                + event.getProperty().getValue());
        if (event.getProperty() == checkBox) {
            for (Switch s : switches) {
                s.setAnimationEnabled(checkBox.booleanValue());
            }
        }
    }

}
