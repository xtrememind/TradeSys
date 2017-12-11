package com.controllers;

import java.io.IOException;

import com.Main;
import com.models.Context;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class RootLayoutController {
	@FXML
	private MenuBar MainMenu;

    //Reference to the main application
    private Main main;

    //Is called by the main application to give a reference back to itself.
    public void setMain (Main main) {
        this.main = main;
    }
    public void toggleMainMenu(boolean disabled) {
    	MainMenu.setVisible(disabled);
    	System.out.println("Main Menu disable = " + disabled);
    }

    //Exit the program
    public void handleExit(ActionEvent actionEvent) {
        System.exit(0);
    }

    
    public void showPortfolioBalanceView(ActionEvent actionEvent) {
		main.showView("PortfolioBalance");
    }
    
    public void showSecurityView(ActionEvent actionEvent) {
		main.showView("Security");
    }
    public void showUserView(ActionEvent actionEvent) {
		main.showView("User");
    }
    
    public void showPortfolioTransactionView(ActionEvent actionEvent) {
		main.showView("PortfolioTransaction");
    }

    public void showExchangeView(ActionEvent actionEvent) {
    		main.showView("Exchange");
    }
    public void showOrderView(ActionEvent actionEvent) {
		main.showView("Order");
	}
    public void showOrderView() {
		main.showView("Order");
	}
    public void showInvestorView(ActionEvent actionEvent) {
		main.showView("Investor");
	}
    public void Logout(ActionEvent actionEvent) {
    		Context.getInstance().setUser(null);
    		MainMenu.setVisible(false);
		main.showView("Login");
	}
    public void showCashTransactionsView(ActionEvent actionEvent) {
		main.showView("CashTransaction");
	}
    //Help Menu button behavior
    public void handleHelp(ActionEvent actionEvent) {
        Alert alert = new Alert (Alert.AlertType.INFORMATION);
        alert.setTitle("Program Information");
        alert.setHeaderText("This is a sample JAVAFX application for SWTESTACADEMY!");
        alert.setContentText("You can search, delete, update, insert a new employee with this program.");
        alert.show();
    }
}
