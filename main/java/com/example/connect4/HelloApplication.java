package com.example.connect4;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    //getting reference to the controller
    private HelloController controller;//connecting main with controller
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        GridPane rootGridPane=fxmlLoader.load();//loading from fxml file
        controller=fxmlLoader.getController();//connecting main with controller
        controller.createPlayground();//calling function from controller
        MenuBar menuBar=createMenu();
        menuBar.prefWidthProperty().bind(stage.widthProperty());
        Pane menuPane=(Pane) rootGridPane.getChildren().get(0);
        menuPane.getChildren().addAll(menuBar);//adding menubar to pane
        Scene scene = new Scene(rootGridPane);

        stage.setTitle("Connect four");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
    private MenuBar createMenu()
    {
      //file menu
        Menu fileMenu=new Menu("File");
        MenuItem newGame=new MenuItem("New Game");
        newGame.setOnAction(actionEvent -> controller.resetGame());
        MenuItem resetGame=new MenuItem("Reset Game");
        resetGame.setOnAction(actionEvent -> controller.resetGame());
        SeparatorMenuItem separatorMenuItem=new SeparatorMenuItem();
        MenuItem exitGame=new MenuItem("Exit Game");
        exitGame.setOnAction(actionEvent -> exitGame());
        fileMenu.getItems().addAll(newGame,resetGame,separatorMenuItem,exitGame);

        //help menu
        Menu helpMenu=new Menu("Help");
        MenuItem aboutGame=new MenuItem("About Connect4");
        aboutGame.setOnAction(actionEvent -> aboutConnect4());

        SeparatorMenuItem separator=new SeparatorMenuItem();
        MenuItem aboutMe=new MenuItem("About Me");
        aboutMe.setOnAction(actionEvent -> aboutMe());
        helpMenu.getItems().addAll(aboutGame,separator,aboutMe);
        MenuBar menuBar=new MenuBar();
        menuBar.getMenus().addAll(fileMenu,helpMenu);
        return menuBar;
    }

    private void aboutMe() {
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Developer");
        alert.setHeaderText("Namaste!");
        alert.setContentText("Hi,I am Ashina,a second year student studying at IGDTUW Delhi." +
                "I have been working on this project for the last 1.5 months and am glad to" +
                " finally present it to the users.Cheers.");
        alert.show();
    }

    private void aboutConnect4() {
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Connect Four");
        alert.setHeaderText("How To Play?");
        alert.setContentText("Connect Four (also known as Four Up, Plot Four, Find Four, Captain's Mistress, Four in a Row, Drop Four, and Gravitrips in the Soviet Union) is a two-player connection board game, in which the players choose a color and then take turns dropping colored discs into a seven-column, six-row vertically suspended grid. The pieces fall straight down, occupying the lowest available space within the column. The objective of the game is to be the first to form a horizontal, vertical, or diagonal line of four of one's own discs. Connect Four is a solved game. The first player can always win by playing the right moves.");
        alert.show();
    }

    private void exitGame() {
        Platform.exit();
        System.exit(0);
    }

    private void resetGame() {

    }

    public static void main(String[] args) {
        launch();
    }
}