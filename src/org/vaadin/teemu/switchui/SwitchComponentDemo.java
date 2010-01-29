package org.vaadin.teemu.switchui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class SwitchComponentDemo extends Application implements
        Property.ValueChangeListener {

    private final int NUMBER_OF_SLAVES = 10;
    private Window mainWindow;
    private CheckBox checkBox;
    private List<Switch> allSwitches = new ArrayList<Switch>(3);
    private List<Switch> slaves = new ArrayList<Switch>(NUMBER_OF_SLAVES);
    private Switch masterSwitch;

    @Override
    public void init() {
        initSlaves();

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

        masterSwitch = new Switch("Master Switch", false);
        masterSwitch.setImmediate(true);
        masterSwitch.addListener(this);
        allSwitches.add(masterSwitch);
        mainWindow.addComponent(masterSwitch);

        HorizontalLayout slavesLayout = new HorizontalLayout();
        for (Switch slave : slaves) {
            slavesLayout.addComponent(slave);
        }
        slavesLayout.setSpacing(true);
        mainWindow.addComponent(slavesLayout);

        HorizontalLayout statusLayout = new HorizontalLayout();
        statusLayout.setSpacing(true);
        Switch switchComponent3 = new Switch("Disabled Switch");
        switchComponent3.setEnabled(false);
        allSwitches.add(switchComponent3);
        statusLayout.addComponent(switchComponent3);

        Switch switchComponent4 = new Switch("Read-only Switch", true);
        switchComponent4.setReadOnly(true);
        allSwitches.add(switchComponent4);
        statusLayout.addComponent(switchComponent4);

        Switch switchComponent5 = new Switch("Switch with Validator", true);
        switchComponent5
                .addValidator(new AbstractValidator("Only ON is valid!") {
                    @Override
                    public boolean isValid(Object value) {
                        return (Boolean) value;
                    }
                });
        switchComponent5.setImmediate(true);
        allSwitches.add(switchComponent5);
        statusLayout.addComponent(switchComponent5);

        mainWindow.addComponent(statusLayout);

        setMainWindow(mainWindow);
    }

    private void initSlaves() {
        Random r = new Random();
        for (int i = 0; i < NUMBER_OF_SLAVES; i++) {
            Switch slave = new Switch("Slave Switch " + (i + 1));
            slave.setValue(r.nextBoolean());
            slaves.add(slave);
            allSwitches.add(slave);
        }
    }

    public void valueChange(ValueChangeEvent event) {
        mainWindow.showNotification("valueChange, "
                + event.getProperty().getClass().getSimpleName() + ", "
                + event.getProperty().getValue());

        if (event.getProperty() == masterSwitch) {
            for (Switch slave : slaves) {
                slave.setValue(masterSwitch.getValue());
            }
        }

        if (event.getProperty() == checkBox) {
            for (Switch s : allSwitches) {
                s.setAnimationEnabled(checkBox.booleanValue());
            }
        }
    }

}
