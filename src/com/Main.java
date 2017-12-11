package com;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

import com.controllers.LoginController;
import com.controllers.RootLayoutController;
import com.models.Context;
import com.models.User;

//Main class which extends from Application Class
public class Main extends Application {

    //This is our PrimaryStage (It contains everything)
    private Stage primaryStage;
    public static User currentUser;
    //This is the BorderPane of RootLayout
    private BorderPane rootLayout;

    @Override
    public void start(Stage primaryStage) {
        //1) Declare a primary stage (Everything will be on this stage)
        this.primaryStage = primaryStage;

        //Optional: Set a title for primary stage
        this.primaryStage.setTitle("TradeSys");

        //2) Initialize RootLayout
        initRootLayout();
        
        showView("Login");
    }

    //Initializes the root layout.
    public void initRootLayout() {
        try {
            //First, load root layout from RootLayout.fxml
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("views/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            //Second, show the scene containing the root layout.
            Scene scene = new Scene(rootLayout); //We are sending rootLayout to the Scene.
            primaryStage.setScene(scene); //Set the scene in primary stage.

            //Give the controller access to the main.
            RootLayoutController controller = loader.getController();
            controller.setMain(this);
            
            controller.toggleMainMenu(false);
            Context.getInstance().setController(controller);
            
            //Third, show the primary stage
            primaryStage.show(); //Display the primary stage
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showView(String view) {
        try {
            FXMLLoader loader = new FXMLLoader();

            loader.setLocation(Main.class.getResource("views/" + view + "View.fxml"));

            AnchorPane oerationsView = (AnchorPane) loader.load();

            // Set Operations view into the center of root layout.
            rootLayout.setCenter(oerationsView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}