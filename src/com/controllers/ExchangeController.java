package com.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.models.Exchange;
import com.models.ExchangeDAO;

public class ExchangeController {

    @FXML
    private TextField IdText;
    @FXML
    private TextArea resultArea;
    @FXML
    private TextField shortNameText;
    @FXML
    private TextField nameText;
    @FXML
    private TextField newNameText;
    @FXML
    private TextField newShortNameText;

    @FXML
    private TableView exchangeTable;
    @FXML
    private TableColumn<Exchange, Integer>  IdColumn;
    @FXML
    private TableColumn<Exchange, String>  nameColumn;
    @FXML
    private TableColumn<Exchange, String> shortNameColumn;
    @FXML
    //For MultiThreading
    private Executor exec;

    //Initializing the controller class.
    //This method is automatically called after the fxml file has been loaded.

    @FXML
    private void initialize () {
        /*
        The setCellValueFactory(...) that we set on the table columns are used to determine
        which field inside the Exchange objects should be used for the particular column.
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
        shortNameColumn.setCellValueFactory(cellData -> cellData.getValue().ShortNameProperty());
    }


    //Search an Exchange
    @FXML
    private void searchExchange (ActionEvent actionEvent) throws ClassNotFoundException, SQLException {
        try {
            //Get Exchange information
            Exchange exchange = ExchangeDAO.searchExchange(IdText.getText());
            //Populate Exchange on TableView and Display on TextArea
            populateAndShowExchange(exchange);
        } catch (SQLException e) {
            e.printStackTrace();
            resultArea.setText("Error occurred while getting Exchange information from DB.\n" + e);
            throw e;
        }
    }

    //Search all Exchanges
    @FXML
    private void searchExchanges(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        try {
            //Get all Exchanges information
            ObservableList<Exchange> exchangeData = ExchangeDAO.searchExchanges();
            //Populate Exchanges on TableView
            populateExchanges(exchangeData);
        } catch (SQLException e){
            System.out.println("Error occurred while getting Exchanges information from DB.\n" + e);
            throw e;
        }
    }

    //Populate Exchanges for TableView with MultiThreading (This is for example usage)
    private void fillExchangeTable(ActionEvent event) throws SQLException, ClassNotFoundException {
        Task<List<Exchange>> task = new Task<List<Exchange>>(){
            @Override
            public ObservableList<Exchange> call() throws Exception{
                return ExchangeDAO.searchExchanges();
            }
        };

        task.setOnFailed(e-> task.getException().printStackTrace());
        task.setOnSucceeded(e-> exchangeTable.setItems((ObservableList<Exchange>) task.getValue()));
        exec.execute(task);
    }

    //Populate Exchange
    @FXML
    private void populateExchange (Exchange exchange) throws ClassNotFoundException {
        //Declare and ObservableList for table view
        ObservableList<Exchange> exchangeData = FXCollections.observableArrayList();
        //Add Exchange to the ObservableList
        exchangeData.add(exchange);
        //Set items to the ExchangeTable
        exchangeTable.setItems(exchangeData);
    }

    //Set Exchange information to Text Area
    @FXML
    private void setExchangeInfoToTextArea ( Exchange exchange) {
        resultArea.setText("Name: " + exchange.getName() + "\n" +
                "Last Name: " + exchange.getShortName());
    }

    //Populate Exchange for TableView and Display Exchange on TextArea
    @FXML
    private void populateAndShowExchange(Exchange exchange) throws ClassNotFoundException {
        if (exchange != null) {
            populateExchange(exchange);
            setExchangeInfoToTextArea(exchange);
        } else {
            resultArea.setText("This Exchange does not exist!\n");
        }
    }

    //Populate Exchanges for TableView
    @FXML
    private void populateExchanges (ObservableList<Exchange> exchangeData) throws ClassNotFoundException {
        //Set items to the ExchangeTable
        exchangeTable.setItems(exchangeData);
    }

    //Update Exchange's email with the email which is written on newEmailText field
    @FXML
    private void updateExchangeNames (ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        try {
            ExchangeDAO.updateExchangeName(IdText.getText(),newNameText.getText());
            ExchangeDAO.updateExchangeShortName(IdText.getText(),newShortNameText.getText());
            resultArea.setText("Email has been updated for, Exchange id: " + IdText.getText() + "\n");
        } catch (SQLException e) {
            resultArea.setText("Problem occurred while updating email: " + e);
        }
    }

    //Insert an Exchange to the DB
    @FXML
    private void insertExchange (ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        try {
            ExchangeDAO.insertExchange(nameText.getText(),shortNameText.getText());
            resultArea.setText("Exchange inserted! \n");
        } catch (SQLException e) {
            resultArea.setText("Problem occurred while inserting Exchange " + e);
            throw e;
        }
    }

    //Delete an Exchange with a given Exchange Id from DB
    @FXML
    private void deleteExchange (ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        try {
            ExchangeDAO.deleteExchangeWithId(IdText.getText());
            resultArea.setText("Exchange deleted! Exchange id: " + IdText.getText() + "\n");
        } catch (SQLException e) {
            resultArea.setText("Problem occurred while deleting Exchange " + e);
            throw e;
        }
    }
}
