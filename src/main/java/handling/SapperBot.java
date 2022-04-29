package handling;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class SapperBot extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    //запуск программы, установка стартовой сцены
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        URL xmlUrl = getClass().getResource("/fxml/startScene.fxml");
        loader.setLocation(xmlUrl);
        Parent root = loader.load();

        stage.setScene(new Scene(root));


        //установка иконки приложения и названия
        InputStream iconStream = getClass().getResourceAsStream("/images/icon.png");
        assert iconStream != null;
        Image image = new Image(iconStream);
        stage.getIcons().add(image);
        stage.setResizable(false);
        stage.setTitle("SapperBOT");

        stage.show();
    }
}