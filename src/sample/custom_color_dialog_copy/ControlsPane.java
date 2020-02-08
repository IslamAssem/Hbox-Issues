package sample.custom_color_dialog_copy;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import static sample.custom_color_dialog_copy.CustomColorDialog.getString;

public class ControlsPane extends VBox {

    private final CustomColorDialog customColorDialog;
    private Label currentColorLabel;
        private Label newColorLabel;
        private Region currentColorRect;
        private Region newColorRect;
        private Region currentTransparent; // for opacity
        private GridPane currentAndNewColor;
        private Region currentNewColorBorder;
        private ToggleButton hsbButton;
        private ToggleButton rgbButton;
        private ToggleButton webButton;
        private HBox hBox;

    // JDK-8161449
    private String saveBtnText;
    private boolean showUseBtn = true;
    private boolean showOpacitySlider = true;

    private Runnable onSave;
    private Runnable onUse;
    private Runnable onCancel;
        private Label labels[] = new Label[4];
        private Slider sliders[] = new Slider[4];
        private IntegerField fields[] = new IntegerField[4];
        private Label units[] = new Label[4];
        protected HBox buttonBox;
        private Region whiteBox;

        protected WebColorField webField = null;
        private GridPane settingsPane = new GridPane();
    private ColorRectPane colorRectPane;

    public ControlsPane(CustomColorDialog customColorDialog, ColorRectPane colorRectPane, Runnable onUse) {
            this.customColorDialog = customColorDialog;
            this.colorRectPane = colorRectPane;
            this.onUse = onUse;
            getStyleClass().add("controls-pane");

            currentNewColorBorder = new Region();
            currentNewColorBorder.setId("current-new-color-border");

            currentTransparent = new Region();
            currentTransparent.getStyleClass().addAll("transparent-pattern");

            currentColorRect = new Region();
            currentColorRect.getStyleClass().add("color-rect");
            currentColorRect.setId("current-color");
            currentColorRect.backgroundProperty().bind(new ObjectBinding<Background>() {
                {
                    bind(customColorDialog.currentColorProperty);
                }
                @Override protected Background computeValue() {
                    return new Background(new BackgroundFill(customColorDialog.currentColorProperty.get(), CornerRadii.EMPTY, Insets.EMPTY));
                }
            });

            newColorRect = new Region();
            newColorRect.getStyleClass().add("color-rect");
            newColorRect.setId("new-color");
            newColorRect.backgroundProperty().bind(new ObjectBinding<Background>() {
                {
                    bind(customColorDialog.customColorProperty);
                }
                @Override protected Background computeValue() {
                    return new Background(new BackgroundFill(customColorDialog.customColorProperty.get(), CornerRadii.EMPTY, Insets.EMPTY));
                }
            });

            currentColorLabel = new Label(getString("currentColor"));
            newColorLabel = new Label(getString("newColor"));

            whiteBox = new Region();
            whiteBox.getStyleClass().add("customcolor-controls-background");

            hsbButton = new ToggleButton(getString("colorType.hsb"));
            hsbButton.getStyleClass().add("left-pill");
            rgbButton = new ToggleButton(getString("colorType.rgb"));
            rgbButton.getStyleClass().add("center-pill");
            webButton = new ToggleButton(getString("colorType.web"));
            webButton.getStyleClass().add("right-pill");
            final ToggleGroup group = new ToggleGroup();

            setAlignment(Pos.CENTER_RIGHT);
            hBox = new HBox();
            hBox.setAlignment(Pos.CENTER);
            hBox.getChildren().addAll(hsbButton, rgbButton, webButton);

            Region spacer1 = new Region();
            spacer1.setId("spacer1");
            Region spacer2 = new Region();
            spacer2.setId("spacer2");
            Region leftSpacer = new Region();
            leftSpacer.setId("spacer-side");
            Region rightSpacer = new Region();
            rightSpacer.setId("spacer-side");
            Region bottomSpacer = new Region();
            bottomSpacer.setId("spacer-bottom");

            currentAndNewColor = new GridPane();
            currentAndNewColor.getColumnConstraints().addAll(new ColumnConstraints(), new ColumnConstraints());
            currentAndNewColor.getColumnConstraints().get(0).setHgrow(Priority.ALWAYS);
            currentAndNewColor.getColumnConstraints().get(1).setHgrow(Priority.ALWAYS);
            currentAndNewColor.getRowConstraints().addAll(new RowConstraints(), new RowConstraints(), new RowConstraints());
            currentAndNewColor.getRowConstraints().get(2).setVgrow(Priority.ALWAYS);
            VBox.setVgrow(currentAndNewColor, Priority.ALWAYS);

            currentAndNewColor.getStyleClass().add("current-new-color-grid");
            currentAndNewColor.add(currentColorLabel, 0, 0);
            currentAndNewColor.add(newColorLabel, 1, 0);
            currentAndNewColor.add(spacer1, 0, 1, 2, 1);
            currentAndNewColor.add(currentTransparent, 0, 2, 2, 1);
            currentAndNewColor.add(currentColorRect, 0, 2);
            currentAndNewColor.add(newColorRect, 1, 2);
            currentAndNewColor.add(currentNewColorBorder, 0, 2, 2, 1);
            currentAndNewColor.add(spacer2, 0, 3, 2, 1);

            settingsPane = new GridPane();
            settingsPane.setId("settings-pane");
            settingsPane.getColumnConstraints().addAll(new ColumnConstraints(),
                    new ColumnConstraints(), new ColumnConstraints(),
                    new ColumnConstraints(), new ColumnConstraints(),
                    new ColumnConstraints());
            settingsPane.getColumnConstraints().get(0).setHgrow(Priority.NEVER);
            settingsPane.getColumnConstraints().get(2).setHgrow(Priority.ALWAYS);
            settingsPane.getColumnConstraints().get(3).setHgrow(Priority.NEVER);
            settingsPane.getColumnConstraints().get(4).setHgrow(Priority.NEVER);
            settingsPane.getColumnConstraints().get(5).setHgrow(Priority.NEVER);
            settingsPane.add(whiteBox, 0, 0, 6, 5);
            settingsPane.add(hBox, 0, 0, 6, 1);
            settingsPane.add(leftSpacer, 0, 0);
            settingsPane.add(rightSpacer, 5, 0);
            settingsPane.add(bottomSpacer, 0, 4);

            webField = new WebColorField();
            webField.getStyleClass().add("web-field");
            webField.setSkin(new WebColorFieldSkin(webField));
            webField.valueProperty().addListener(new ChangeListener<Color>() {
                @Override
                public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
                    if (newValue!=null)
                    customColorDialog.customColorProperty.set(newValue);
                }
            });
//            webField.valueProperty().bindBidirectional(customColorDialog.customColorProperty);
            webField.visibleProperty().bind(group.selectedToggleProperty().isEqualTo(webButton));
            settingsPane.add(webField, 2, 1);

            // Color settings Grid Pane
            for (int i = 0; i < 4; i++) {
                labels[i] = new Label();
                labels[i].getStyleClass().add("settings-label");

                sliders[i] = new Slider();

                fields[i] = new IntegerField();
                fields[i].getStyleClass().add("color-input-field");
                fields[i].setSkin(new IntegerFieldSkin(fields[i]));

                units[i] = new Label(i == 0 ? "\u00B0" : "%");
                units[i].getStyleClass().add("settings-unit");

                if (i > 0 && i < 3) {
                    // first row and opacity labels are always visible
                    // second and third row labels are not visible in Web page
                    labels[i].visibleProperty().bind(group.selectedToggleProperty().isNotEqualTo(webButton));
                }
                if (i < 3) {
                    // sliders and fields shouldn't be visible in Web page
                    sliders[i].visibleProperty().bind(group.selectedToggleProperty().isNotEqualTo(webButton));
                    fields[i].visibleProperty().bind(group.selectedToggleProperty().isNotEqualTo(webButton));
                    units[i].visibleProperty().bind(group.selectedToggleProperty().isEqualTo(hsbButton));
                }
                int row = 1 + i;
                if (i == 3) {
                    // opacity row is shifted one gridPane row down
                    row++;
                }

                // JDK-8161449 - hide the opacity slider
                if (i == 3 && !showOpacitySlider) {
                    continue;
                }

                settingsPane.add(labels[i], 1, row);
                settingsPane.add(sliders[i], 2, row);
                settingsPane.add(fields[i], 3, row);
                settingsPane.add(units[i], 4, row);
            }

            set(3, getString("opacity_colon"), 100, colorRectPane.alpha);

            hsbButton.setToggleGroup(group);
            rgbButton.setToggleGroup(group);
            webButton.setToggleGroup(group);
            group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null) {
                    group.selectToggle(oldValue);
                } else {
                    if (newValue == hsbButton) {
                        showHSBSettings();
                    } else if (newValue == rgbButton) {
                        showRGBSettings();
                    } else {
                        showWebSettings();
                    }
                }
            });
            group.selectToggle(hsbButton);

            buttonBox = new HBox();
            buttonBox.setId("buttons-hbox");
            buttonBox.setAlignment(Pos.CENTER_RIGHT);
            Button saveButton = new Button(saveBtnText != null && !saveBtnText.isEmpty() ? saveBtnText : getString("Save"));
            saveButton.setDefaultButton(true);
            saveButton.setOnAction(t -> {
                if (onSave != null) {
                    onSave.run();
                }
                customColorDialog.hide();
            });

            Button useButton = new Button(getString("Use"));
            useButton.setOnAction(t -> {
                if (onUse != null) {
                    onUse.run();
                }
                customColorDialog.hide();
            });

            Button cancelButton = new Button(getString("Cancel"));
            cancelButton.setCancelButton(true);
            cancelButton.setOnAction(e -> {
                customColorDialog.customColorProperty.set(customColorDialog.getCurrentColor());
                if (onCancel != null) {
                    onCancel.run();
                }
                customColorDialog.hide();
            });

            if (showUseBtn) {
                buttonBox.getChildren().addAll(saveButton, useButton, cancelButton);
            } else {
                buttonBox.getChildren().addAll(saveButton, cancelButton);
            }

            getChildren().addAll(currentAndNewColor, settingsPane, buttonBox);
        }

    private void showHSBSettings() {
            set(0, getString("hue_colon"), 360, colorRectPane.hue);
            set(1, getString("saturation_colon"), 100, colorRectPane.sat);
            set(2, getString("brightness_colon"), 100, colorRectPane.bright);
        }

        private void showRGBSettings() {
            set(0, getString("red_colon"), 255, colorRectPane.red);
            set(1, getString("green_colon"), 255, colorRectPane.green);
            set(2, getString("blue_colon"), 255, colorRectPane.blue);
        }

        private void showWebSettings() {
            labels[0].setText(getString("web_colon"));
        }

        private Property<Number>[] bindedProperties = new Property[4];

        private void set(int row, String caption, int maxValue, Property<Number> prop) {
            labels[row].setText(caption);
            if (bindedProperties[row] != null) {
                sliders[row].valueProperty().unbindBidirectional(bindedProperties[row]);
                fields[row].valueProperty().unbindBidirectional(bindedProperties[row]);
            }
            sliders[row].setMax(maxValue);
            sliders[row].valueProperty().bindBidirectional(prop);
            labels[row].setLabelFor(sliders[row]);
            fields[row].setMaxValue(maxValue);
            fields[row].valueProperty().bindBidirectional(prop);
            bindedProperties[row] = prop;
        }
    }