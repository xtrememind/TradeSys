package com.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.models.*;

public class SecurityController {

    @FXML
    private TextField IdText;
    @FXML
    private TextArea resultArea;
    @FXML
    private TextField nameText;
    @FXML
    private TextField newNameText;
    @FXML
    private TextField newShortCodeText;    
    @FXML
    private TextField ShortCodeText;
    @FXML
    private TableView SecurityTable;
    @FXML
    private TableColumn<Security, Integer>  IdColumn;
    @FXML
    private TableColumn<Security, String>  nameColumn;    
    @FXML
    private TableColumn<Security, String>  codeColumn;    
    @FXML
    private TableColumn<Security, String>  exchangeColumn;
    @FXML
    private ComboBox<Exchange> ExchangeCombo;
    @FXML
    private ComboBox<Exchange> newExchangeCombo;
    @FXML
    //For MultiThreading
    private Executor exec;


    //Initializing the controller class.
    //This method is automatically called after the fxml file has been loaded.

    @FXML
    private void initialize () {
        /*
        The setCellValueFactory(...) that we set on the table columns are used to determine
        which field inside the Security objects should be used for the particular column.
        The arrow -> indicates that we're using a Java 8 feature called Lambdas.
        (Another option would be to use a PropertyValueFactory, but this is not type-safe

        We're only using StringProperty values for our table columns in this example.
        When you want to use IntegerProperty or DoubleProperty, the setCellValueFactory(...)
        must have an additional asObject():
        */

        //For multithreading: Create executor that uses daemon threads:
        exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread (runnable);
            t.setDaemon(true);
            return t;
        });

        IdColumn.setCellValueFactory(cellData -> cellData.getValue().IdProperty().asObject());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().NameProperty());
        exchangeColumn.setCellValueFactory(cellData -> cellData.getValue().getExchangeName());
        codeColumn.setCellValueFactory(cellData -> cellData.getValue().CodeProperty());
        
        try {
        	ObservableList<Exchange> exchangeData = ExchangeDAO.searchExchanges();
        	ExchangeCombo.setItems(exchangeData);
        	newExchangeCombo.setItems(exchangeData);

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        	
    }


    //Search an Security
    @FXML
    private void searchSecurity (ActionEvent actionEvent) throws ClassNotFoundException, SQLException {
        try {
            //Get Security information
            Security Security = SecurityDAO.searchSecurity(IdText.getText());
            //Populate Security on TableView and Display on TextArea
            populateAndShowSecurity(Security);
        } catch (SQLException e) {
            e.printStackTrace();
            resultArea.setText("Error occurred while getting Security information from DB.\n" + e);
            throw e;
        }
    }

    //Search all Securitys
    @FXML
    private void searchSecuritys(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        try {
            //Get all Securitys information
            ObservableList<Security> SecurityData = SecurityDAO.searchSecuritys();
            //Populate Securitys on TableView
            populateSecuritys(SecurityData);
        } catch (SQLException e){
            System.out.println("Error occurred while getting Securitys information from DB.\n" + e);
            throw e;
        }
    }

    //Populate Securitys for TableView with MultiThreading (This is for example usage)
    private void fillSecurityTable(ActionEvent event) throws SQLException, ClassNotFoundException {
        Task<List<Security>> task = new Task<List<Security>>(){
            @Override
            public ObservableList<Security> call() throws Exception{
                return SecurityDAO.searchSecuritys();
            }
        };

        task.setOnFailed(e-> task.getException().printStackTrace());
        task.setOnSucceeded(e-> SecurityTable.setItems((ObservableList<Security>) task.getValue()));
        exec.execute(task);
    }

    //Populate Security
    @FXML
    private void populateSecurity (Security Security) throws ClassNotFoundException {
        //Declare and ObservableList for table view
        ObservableList<Security> SecurityData = FXCollections.observableArrayList();
        //Add Security to the ObservableList
        SecurityData.add(Security);
        //Set items to the SecurityTable
        SecurityTable.setItems(SecurityData);
    }

    //Set Security information to Text Area
    @FXML
    private void setSecurityInfoToTextArea ( Security Security) {
        resultArea.setText("Name: " + Security.getName());
    }

    //Populate Security for TableView and Display Security on TextArea
    @FXML
    private void populateAndShowSecurity(Security Security) throws ClassNotFoundException {
        if (Security != null) {
            populateSecurity(Security);
            setSecurityInfoToTextArea(Security);
        } else {
            resultArea.setText("This Security does not exist!\n");
        }
    }

    //Populate Securitys for TableView
    @FXML
    private void populateSecuritys (ObservableList<Security> SecurityData) throws ClassNotFoundException {
        //Set items to the SecurityTable
        SecurityTable.setItems(SecurityData);
    }

    //Update Security's email with the email which is written on newEmailText field
    @FXML
    private void updateSecurityNames (ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        try {
            SecurityDAO.updateSecurityName(IdText.getText(),newNameText.getText(), newShortCodeText.getText(), newExchangeCombo.getValue().getId());
            resultArea.setText("Email has been updated for, Security id: " + IdText.getText() + "\n");
        } catch (SQLException e) {
            resultArea.setText("Problem occurred while updating email: " + e);
        }
    }

    //Insert an Security to the DB
    @FXML
    private void insertSecurity (ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        try {
            SecurityDAO.insertSecurity(nameText.getText(),ShortCodeText.getText(),ExchangeCombo.getValue().getId());
            resultArea.setText("Security inserted! \n");
        } catch (SQLException e) {
            resultArea.setText("Problem occurred while inserting Security " + e);
            throw e;
        }
    }

    //Delete an Security with a given Security Id from DB
    @FXML
    private void deleteSecurity (ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        try {
            SecurityDAO.deleteSecurityWithId(IdText.getText());
            resultArea.setText("Security deleted! Security id: " + IdText.getText() + "\n");
        } catch (SQLException e) {
            resultArea.setText("Problem occurred while deleting Security " + e);
            throw e;
        }
    }
}
