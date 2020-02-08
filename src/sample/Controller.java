package sample;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import sample.custom_color_dialog_copy.CustomColorDialog;

public class Controller {
    public void showIssue(ActionEvent actionEvent) {
        ColorDialog dialog = new ColorDialog(((Node)actionEvent.getSource()).getScene().getWindow());
        dialog.show();
    }

    public void showDefault(ActionEvent actionEvent) {
        CustomColorDialog dialog = new CustomColorDialog(((Node)actionEvent.getSource()).getScene().getWindow());
        dialog.show();
    }
}
