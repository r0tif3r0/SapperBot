package handling;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import sapper.*;

import java.io.IOException;

public class StartController {


    @FXML
    private Label bott;
    @FXML
    private Label sap;
    @FXML
    private Label win;
    @FXML
    private Label lose;
    @FXML
    private BorderPane border;
    @FXML
    private GridPane grid;
    @FXML
    private Button backButton;
    @FXML
    private Button exitButton;
    @FXML
    private Button helpButton;
    @FXML
    private Button startButton;
    @FXML
    private Button settingsButton;

    Game game;
    Bot bot;

    //выход из приложения по кнопке "EXIT"
    @FXML
    private void exit() {
        Platform.exit();
        System.exit(0);
    }

    //переход по кнопке "HELP"
    @FXML
    private void help() throws IOException {
        Stage stage = (Stage) helpButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/helpScene.fxml"));
        Parent root1 = loader.load();
        stage.setScene(new Scene(root1));
        stage.show();
    }

    //запуск работы бота по кнопке "START"
    @FXML
    private void start() {
        helpButton.setVisible(false);
        startButton.setVisible(false);
        settingsButton.setVisible(false);
        sap.setVisible(false);
        bott.setVisible(false);
        exitButton.setVisible(false);
        backButton.setVisible(true);
        buildEasyGrid();
    }

    //переход по кнопке "SETTINGS"
    @FXML
    private void settings() throws IOException {
        Stage stage = (Stage)settingsButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/settingsScene.fxml"));
        Parent root1 = loader.load();
        stage.setScene(new Scene(root1));
        stage.show();
    }

    //возврат в главное меню по кнопке "BACK"
    @FXML
    private void back() throws IOException {
        Stage stage = (Stage)backButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/startScene.fxml"));
        Parent root1 = loader.load();
        stage.setScene(new Scene(root1));
        stage.show();
    }

    //начало игры, установка и передача параметров, построение начального поля
    public void buildEasyGrid(){
        grid = new GridPane();
        border.setCenter(grid);
        grid.setAlignment(Pos.TOP_CENTER);
        game = new Game(9,9, 10, border);
        game.start();

        Image image = new Image("/images/closed.png");
        for (Coord coord : Ranges.getAllCoords())
        {
            ImageView imgV = new ImageView(image);
            grid.add(imgV, coord.x, coord.y);
        }

        //запуск бота
        bot = new Bot(border, grid, game);
        bot.startBot();
        service.restart();
    }

    //Проверка состояния игры и вывод информации об этом
    private void checkWin() {
        System.out.println(game.getState());
        switch (game.getState()) {
            case BOMBED -> {
                lose.setVisible(true);
                service.cancel();
            }
            case WINNER -> {
                win.setVisible(true);
                service.cancel();
            }
            case PLAYED -> service.restart();
        }
    }

    //сервис, позволяющий проверять состояние игры в режиме реального времени
    Service<Void> service = new Service<Void>() {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {
                    Platform.runLater(StartController.this::checkWin);
                    return null;
                }
            };
        }
    };

}