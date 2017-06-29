Switch
======

Decorated checkbox for Vaadin. 

See [Switch page](https://vaadin.com/addon/switch) at Directory for more details, or [online demo](http://teemu.app.fi/switch).

Requirements
============

Vaadin 7.2.0+ is required for Switch 2.0.0.

Use Switch 1.0.1 for Vaadin 6.

Styles
============

This addon has various styles. A couple of image based styles and a style which is based on DOM elements.

## Dom Style

To activate the dom style you need to use the style name "dom":

~~~
Switch switch = new Switch();
Switch.addStyleName(Switch.DOM_STYLE);
~~~  

This style has support fort the valo theme. To use the switch-valo add on theme, you need to include the sass file of this addon into your styles.scss file:

~~~
/* import the switch addon valo theme */
@import "VAADIN/addons/switch/css/switch-valo.scss";
~~~

The `switch-valo.scss` provides a **mixin** which can be used to consistently create other sizes of the switch widget if needed.
