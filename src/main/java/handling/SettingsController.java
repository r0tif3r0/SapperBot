package handling;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;

import java.io.IOException;

public class SettingsController {

    @FXML
    public RadioButton easyRB;
    @FXML
    private Button backButton;

    //переход в главное меню по кнопке "BACK"
    public void back() throws IOException {
        Stage stage = (Stage)backButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/startScene.fxml"));
        Parent root1 = loader.load();
        stage.setScene(new Scene(root1));
        stage.show();
    }
}
