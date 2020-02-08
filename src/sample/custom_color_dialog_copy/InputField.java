/*
 * Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package sample.custom_color_dialog_copy;

import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Control;

/**
 *
 */
abstract class InputField extends Control {
    /**
     * The default value for {@link #prefColumnCount}.
     */
    public static final int DEFAULT_PREF_COLUMN_COUNT = 12;

    /**
     * Indicates whether this InputField can be edited by the user. If true, the
     * "readonly" pseudo class will be false, but if false, the "readonly"
     * pseudo class will be true.
     */
    private BooleanProperty editable = new SimpleBooleanProperty(this, "editable", true);
    public final boolean isEditable() { return editable.getValue(); }
    public final void setEditable(boolean value) { editable.setValue(value); }
    public final BooleanProperty editableProperty() { return editable; }

    /**
     * The {@code InputField}'s prompt text to display, or
     * <tt>null</tt> if no prompt text is displayed.
     */
    private StringProperty promptText = new StringPropertyBase("") {
        @Override protected void invalidated() {
            // Strip out newlines
            String txt = get();
            if (txt != null && txt.contains("\n")) {
                txt = txt.replace("\n", "");
                set(txt);
            }
        }

        @Override public Object getBean() { return InputField.this; }
        @Override public String getName() { return "promptText"; }
    };
    public final StringProperty promptTextProperty() { return promptText; }
    public final String getPromptText() { return promptText.get(); }
    public final void setPromptText(String value) { promptText.set(value); }


    /**
     * The preferred number of text columns. This is used for
     * calculating the {@code InputField}'s preferred width.
     */
    private IntegerProperty prefColumnCount = new IntegerPropertyBase(DEFAULT_PREF_COLUMN_COUNT) {
        private int oldValue = get();

        @Override
        protected void invalidated() {
            int value = get();

            if (value < 0) {
                if (isBound()) {
                    unbind();
                }
                set(oldValue);
                throw new IllegalArgumentException("value cannot be negative.");
            }

            oldValue = value;
        }

        @Override public Object getBean() { return InputField.this; }
        @Override public String getName() { return "prefColumnCount"; }
    };
    public final IntegerProperty prefColumnCountProperty() { return prefColumnCount; }
    public final int getPrefColumnCount() { return prefColumnCount.getValue(); }
    public final void setPrefColumnCount(int value) { prefColumnCount.setValue(value); }

    /**
     * The action handler associated with this InputField, or
     * <tt>null</tt> if no action handler is assigned.
     *
     * The action handler is normally called when the user types the ENTER key.
     */
    private ObjectProperty<EventHandler<ActionEvent>> onAction = new ObjectPropertyBase<EventHandler<ActionEvent>>() {
        @Override protected void invalidated() {
            setEventHandler(ActionEvent.ACTION, get());
        }

        @Override public Object getBean() { return InputField.this; }
        @Override public String getName() { return "onAction"; }
    };
    public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty() { return onAction; }
    public final EventHandler<ActionEvent> getOnAction() { return onActionProperty().get(); }
    public final void setOnAction(EventHandler<ActionEvent> value) { onActionProperty().set(value); }

    /**
     * Creates a new InputField. The style class is set to "money-field".
     */
    public InputField() {
        getStyleClass().setAll("input-field");
    }

    //    @Override protected String getUserAgentStylesheet() {
//        return getClass().getResource("InputField.css").toExternalForm();
//    }
}