package sample.custom_color_dialog_copy;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

import static sample.custom_color_dialog_copy.CustomColorDialog.*;

public class ColorRectPane extends HBox {

    private Pane colorRect;
    private Pane colorBar;
    private Pane colorRectOverlayOne;
    private Pane colorRectOverlayTwo;
    private Region colorRectIndicator;
    private Region colorBarIndicator;
    CustomColorDialog customColorDialog;
    private boolean changeIsLocal = false;
    public DoubleProperty hue = new SimpleDoubleProperty(-1) {
        @Override
        protected void invalidated() {
            if (!changeIsLocal) {
                changeIsLocal = true;
                updateHSBColor();
                changeIsLocal = false;
            }
        }
    };
    public DoubleProperty sat = new SimpleDoubleProperty(-1) {
        @Override
        protected void invalidated() {
            if (!changeIsLocal) {
                changeIsLocal = true;
                updateHSBColor();
                changeIsLocal = false;
            }
        }
    };
    public DoubleProperty bright = new SimpleDoubleProperty(-1) {
        @Override
        protected void invalidated() {
            if (!changeIsLocal) {
                changeIsLocal = true;
                updateHSBColor();
                changeIsLocal = false;
            }
        }
    };
    public IntegerProperty red = new SimpleIntegerProperty(-1) {
        @Override
        protected void invalidated() {
            if (!changeIsLocal) {
                changeIsLocal = true;
                updateRGBColor();
                changeIsLocal = false;
            }
        }
    };

    public IntegerProperty green = new SimpleIntegerProperty(-1) {
        @Override
        protected void invalidated() {
            if (!changeIsLocal) {
                changeIsLocal = true;
                updateRGBColor();
                changeIsLocal = false;
            }
        }
    };

    public IntegerProperty blue = new SimpleIntegerProperty(-1) {
        @Override
        protected void invalidated() {
            if (!changeIsLocal) {
                changeIsLocal = true;
                updateRGBColor();
                changeIsLocal = false;
            }
        }
    };

    public DoubleProperty alpha = new SimpleDoubleProperty(100) {
        @Override
        protected void invalidated() {
            if (!changeIsLocal) {
                changeIsLocal = true;
                customColorDialog.setCustomColor(new Color(
                        customColorDialog.getCustomColor().getRed(),
                        customColorDialog.getCustomColor().getGreen(),
                        customColorDialog.getCustomColor().getBlue(),
                        clamp(alpha.get() / 100)));
                changeIsLocal = false;
            }
        }
    };

    private void updateRGBColor() {
        Color newColor = Color.rgb(red.get(), green.get(), blue.get(), clamp(alpha.get() / 100));
        hue.set(newColor.getHue());
        sat.set(newColor.getSaturation() * 100);
        bright.set(newColor.getBrightness() * 100);
        customColorDialog.setCustomColor(newColor);
    }

    private void updateHSBColor() {
        Color newColor = Color.hsb(hue.get(), clamp(sat.get() / 100),
                clamp(bright.get() / 100), clamp(alpha.get() / 100));
        red.set(doubleToInt(newColor.getRed()));
        green.set(doubleToInt(newColor.getGreen()));
        blue.set(doubleToInt(newColor.getBlue()));
        customColorDialog.setCustomColor(newColor);
    }

    private void colorChanged() {
        if (!changeIsLocal) {
            changeIsLocal = true;
            hue.set(customColorDialog.getCustomColor().getHue());
            sat.set(customColorDialog.getCustomColor().getSaturation() * 100);
            bright.set(customColorDialog.getCustomColor().getBrightness() * 100);
            red.set(doubleToInt(customColorDialog.getCustomColor().getRed()));
            green.set(doubleToInt(customColorDialog.getCustomColor().getGreen()));
            blue.set(doubleToInt(customColorDialog.getCustomColor().getBlue()));
            changeIsLocal = false;
        }
    }

    public ColorRectPane(CustomColorDialog customColorDialog) {

        this.customColorDialog = customColorDialog;
        getStyleClass().add("color-rect-pane");

        customColorDialog.customColorProperty().addListener((ov, t, t1) -> {
            colorChanged();
        });

        colorRectIndicator = new Region();
        colorRectIndicator.setId("color-rect-indicator");
        colorRectIndicator.setManaged(false);
        colorRectIndicator.setMouseTransparent(true);
        colorRectIndicator.setCache(true);

        final Pane colorRectOpacityContainer = new StackPane();

        colorRect = new StackPane(){
            // This is an implementation of square control that chooses its
            // size to fill the available height
            @Override
            public Orientation getContentBias() {
                return Orientation.VERTICAL;
            }

            @Override
            protected double computePrefWidth(double height) {
                return super.computePrefWidth(height);
            }

            @Override
            protected double computeMaxWidth(double height) {
                return super.computeMaxWidth(height);
            }
        };
        colorRect.getStyleClass().addAll("color-rect", "transparent-pattern");

        Pane colorRectHue = new Pane();
        colorRectHue.backgroundProperty().bind(new ObjectBinding<Background>() {

            {
                bind(hue);
            }

            @Override
            protected Background computeValue() {
                return new Background(new BackgroundFill(
                        Color.hsb(hue.getValue(), 1.0, 1.0),
                        CornerRadii.EMPTY, Insets.EMPTY));
            }
        });

        colorRectOverlayOne = new Pane();
        colorRectOverlayOne.getStyleClass().add("color-rect");
        colorRectOverlayOne.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.rgb(255, 255, 255, 1)),
                        new Stop(1, Color.rgb(255, 255, 255, 0))),
                CornerRadii.EMPTY, Insets.EMPTY)));

        EventHandler<MouseEvent> rectMouseHandler = event -> {
            final double x = event.getX();
            final double y = event.getY();
            sat.set(clamp(x / colorRect.getWidth()) * 100);
            bright.set(100 - (clamp(y / colorRect.getHeight()) * 100));
        };

        colorRectOverlayTwo = new Pane();
        colorRectOverlayTwo.getStyleClass().addAll("color-rect");
        colorRectOverlayTwo.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.rgb(0, 0, 0, 0)), new Stop(1, Color.rgb(0, 0, 0, 1))),
                CornerRadii.EMPTY, Insets.EMPTY)));
        colorRectOverlayTwo.setOnMouseDragged(rectMouseHandler);
        colorRectOverlayTwo.setOnMousePressed(rectMouseHandler);

        Pane colorRectBlackBorder = new Pane();
        colorRectBlackBorder.setMouseTransparent(true);
        colorRectBlackBorder.getStyleClass().addAll("color-rect", "color-rect-border");

        colorBar = new Pane();
        colorBar.getStyleClass().add("color-bar");
        colorBar.setBackground(new Background(new BackgroundFill(createHueGradient(),
                CornerRadii.EMPTY, Insets.EMPTY)));

        colorBarIndicator = new Region();
        colorBarIndicator.setId("color-bar-indicator");
        colorBarIndicator.setMouseTransparent(true);
        colorBarIndicator.setCache(true);

        colorRectIndicator.layoutXProperty().bind(sat.divide(100).multiply(colorRect.widthProperty()));
        colorRectIndicator.layoutYProperty().bind(Bindings.subtract(1, bright.divide(100)).multiply(colorRect.heightProperty()));
        colorBarIndicator.layoutYProperty().bind(hue.divide(360).multiply(colorBar.heightProperty()));
        colorRectOpacityContainer.opacityProperty().bind(alpha.divide(100));

        EventHandler<MouseEvent> barMouseHandler = event -> {
            final double y = event.getY();
            hue.set(clamp(y / colorRect.getHeight()) * 360);
        };

        colorBar.setOnMouseDragged(barMouseHandler);
        colorBar.setOnMousePressed(barMouseHandler);

        colorBar.getChildren().setAll(colorBarIndicator);
        colorRectOpacityContainer.getChildren().setAll(colorRectHue, colorRectOverlayOne, colorRectOverlayTwo);
        colorRect.getChildren().setAll(colorRectOpacityContainer, colorRectBlackBorder, colorRectIndicator);
        HBox.setHgrow(colorRect, Priority.ALWAYS);
        getChildren().addAll(colorRect, colorBar);
    }

    public void updateValues() {
        if (customColorDialog.getCurrentColor() == null) {
            customColorDialog.setCurrentColor(Color.TRANSPARENT);
        }
        changeIsLocal = true;
        //Initialize hue, sat, bright, color, red, green and blue
        hue.set(customColorDialog.getCurrentColor().getHue());
        sat.set(customColorDialog.getCurrentColor().getSaturation() * 100);
        bright.set(customColorDialog.getCurrentColor().getBrightness() * 100);
        alpha.set(customColorDialog.getCurrentColor().getOpacity() * 100);
        customColorDialog.setCustomColor(Color.hsb(hue.get(), clamp(sat.get() / 100), clamp(bright.get() / 100),
                clamp(alpha.get() / 100)));
        red.set(doubleToInt(customColorDialog.getCustomColor().getRed()));
        green.set(doubleToInt(customColorDialog.getCustomColor().getGreen()));
        blue.set(doubleToInt(customColorDialog.getCustomColor().getBlue()));
        changeIsLocal = false;
    }
/*
    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        // to maintain default size
        colorRectIndicator.autosize();
        // to maintain square size
        double size = Math.min(colorRect.getWidth(), colorRect.getHeight());
        colorRect.resize(size, size);
        colorBar.resize(colorBar.getWidth(), size);
    }*/
}