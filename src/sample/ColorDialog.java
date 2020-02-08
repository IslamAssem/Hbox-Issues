package sample;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import sample.custom_color_dialog_copy.ColorRectPane;
import sample.custom_color_dialog_copy.ControlsPane;
import sample.custom_color_dialog_copy.CustomColorDialog;

public class ColorDialog extends CustomColorDialog {
    public ColorDialog(Window owner) {
        super(owner);
    }

    @Override
    public Pane getRoot() {
        return super.getRoot();
    }

    protected void buildUI() {


        colorRectPane = new ColorRectPane(this);
        controlsPane = new ControlsPane(this,colorRectPane,onUse);
        HBox box = new HBox();
        box.getChildren().setAll(colorRectPane, controlsPane);
        root.getChildren().setAll(box);
        HBox.setHgrow(controlsPane, Priority.ALWAYS);
        HBox.setHgrow(colorRectPane,Priority.ALWAYS);
        VBox.setVgrow(box,Priority.ALWAYS);
//        getChildren().setAll(colorRectPane);
    }
}
