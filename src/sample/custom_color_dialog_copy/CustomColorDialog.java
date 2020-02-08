/*
 * Copyright (c) 2012, 2016, Oracle and/or its affiliates. All rights reserved.
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

import com.sun.javafx.scene.control.skin.resources.ControlResources;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.*;


/**
 *
 * @author paru
 */
public class CustomColorDialog  {

    protected final Stage dialog = new Stage();
    protected ObjectProperty<Color> currentColorProperty = new SimpleObjectProperty<>(Color.WHITE);
    protected ObjectProperty<Color> customColorProperty = new SimpleObjectProperty<>(Color.TRANSPARENT);
    protected Scene customScene;
    protected Runnable onUse;
    static String getString(String key) {
        return ControlResources.getString("ColorPicker."+key);
    }
    protected Pane root;
    public CustomColorDialog(Window owner) {
        root = getRoot();
        root.getStyleClass().add("custom-color-dialog");
        if (owner != null) dialog.initOwner(owner);
        dialog.setTitle(getString("customColorDialogTitle"));
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UTILITY);
//        dialog.initStyle(StageStyle.DECORATED);
        dialog.setResizable(true );

        dialog.addEventHandler(KeyEvent.ANY, keyEventListener);

        customScene = new Scene(root);
        final Scene ownerScene = owner.getScene();
        if (ownerScene != null) {
            if (ownerScene.getUserAgentStylesheet() != null) {
                customScene.setUserAgentStylesheet(ownerScene.getUserAgentStylesheet());
            }
            customScene.getStylesheets().addAll(ownerScene.getStylesheets());
        }
        buildUI();

        dialog.setScene(customScene);
    }

    protected ColorRectPane colorRectPane;
    protected ControlsPane controlsPane;
    public Pane getRoot(){
        return new HBox(){
            @Override public void layoutChildren() {
                super.layoutChildren();
                if (dialog.getMinWidth() > 0 && dialog.getMinHeight() > 0) {
                    // don't recalculate min size once it's set
                    return;
                }

                // Math.max(0, ...) added for RT-34704 to ensure the dialog is at least 0 x 0
                double minWidth = Math.max(0, computeMinWidth(getHeight()) + (dialog.getWidth() - customScene.getWidth()));
                double minHeight = Math.max(0, computeMinHeight(getWidth()) + (dialog.getHeight() - customScene.getHeight()));
                dialog.setMinWidth(minWidth);
                dialog.setMinHeight(minHeight);
            }
        };
    }
    protected void buildUI() {


        colorRectPane = new ColorRectPane(this);
        controlsPane = new ControlsPane(this,colorRectPane,onUse);
        root.getChildren().setAll(colorRectPane, controlsPane);
        HBox.setHgrow(controlsPane,Priority.ALWAYS);
        HBox.setHgrow(colorRectPane,Priority.ALWAYS);
//        getChildren().setAll(colorRectPane);
    }

    private final EventHandler<KeyEvent> keyEventListener = e -> {
        switch (e.getCode()) {
            case ESCAPE :
                dialog.setScene(null);
                dialog.close();
        default:
            break;
        }
    };

    public void setCurrentColor(Color currentColor) {
        this.currentColorProperty.set(currentColor);
    }

    public Color getCurrentColor() {
        return currentColorProperty.get();
    }

    ObjectProperty<Color> customColorProperty() {
        return customColorProperty;
    }

    public void setCustomColor(Color color) {
        customColorProperty.set(color);
    }

    public Color getCustomColor() {
        return customColorProperty.get();
    }

    public void setOnUse(Runnable onUse) {
        this.onUse = onUse;
    }

    public void setOnHidden(EventHandler<WindowEvent> onHidden) {
         dialog.setOnHidden(onHidden);
     }

    public Stage getDialog() {
        return dialog;
    }

    public void show() {
        if (dialog.getOwner() != null) {
            // Workaround of RT-29871: Instead of just invoking fixPosition()
            // here need to use listener that fixes dialog position once both
            // width and height are determined
            dialog.widthProperty().addListener(positionAdjuster);
            dialog.heightProperty().addListener(positionAdjuster);
            positionAdjuster.invalidated(null);
        }
        if (dialog.getScene() == null) dialog.setScene(customScene);
        colorRectPane.updateValues();
        dialog.show();
    }

    public void hide() {
        if (dialog.getOwner() != null) {
            dialog.hide();
        }
    }

    private InvalidationListener positionAdjuster = new InvalidationListener() {

        @Override
        public void invalidated(Observable ignored) {
            if (Double.isNaN(dialog.getWidth()) || Double.isNaN(dialog.getHeight())) {
                return;
            }
            dialog.widthProperty().removeListener(positionAdjuster);
            dialog.heightProperty().removeListener(positionAdjuster);
            fixPosition();
        }

    };

    private void fixPosition() {
        Window w = dialog.getOwner();
        Screen s = com.sun.javafx.util.Utils.getScreen(w);
        Rectangle2D sb = s.getBounds();
        double xR = w.getX() + w.getWidth();
        double xL = w.getX() - dialog.getWidth();
        double x, y;
        if (sb.getMaxX() >= xR + dialog.getWidth()) {
            x = xR;
        } else if (sb.getMinX() <= xL) {
            x = xL;
        } else {
            x = Math.max(sb.getMinX(), sb.getMaxX() - dialog.getWidth());
        }
        y = Math.max(sb.getMinY(), Math.min(sb.getMaxY() - dialog.getHeight(), w.getY()));
        dialog.setX(x);
        dialog.setY(y);
    }
    /* ------------------------------------------------------------------------*/


    /* ------------------------------------------------------------------------*/


    public static double clamp(double value) {
        return value < 0 ? 0 : value > 1 ? 1 : value;
    }

    public static LinearGradient createHueGradient() {
        double offset;
        Stop[] stops = new Stop[255];
        for (int y = 0; y < 255; y++) {
            offset = (double)(1 - (1.0 / 255) * y);
            int h = (int)((y / 255.0) * 360);
            stops[y] = new Stop(offset, Color.hsb(h, 1.0, 1.0));
        }
        return new LinearGradient(0f, 1f, 0f, 0f, true, CycleMethod.NO_CYCLE, stops);
    }

    public static int doubleToInt(double value) {
        return (int) (value * 255 + 0.5); // Adding 0.5 for rounding only
    }
}
