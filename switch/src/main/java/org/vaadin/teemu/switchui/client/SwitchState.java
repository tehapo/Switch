package org.vaadin.teemu.switchui.client;

import com.vaadin.shared.ui.checkbox.CheckBoxState;

@SuppressWarnings("serial")
public class SwitchState extends CheckBoxState {
    {
        primaryStyleName = "v-switch";
    }

    public boolean animated = true;
}
