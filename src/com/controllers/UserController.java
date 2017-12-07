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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.models.*;

public class UserController {

    @FXML
    private TextField IdText;
    @FXML
    private TextArea resultArea;
    @FXML
    private TextField nameText;
    @FXML
    private TextField newNameText;
    @FXML
    private TextField newUserNameText;    
    @FXML
    private TextField UserNameText;
    @FXML
    private TextField PassWordText;
    @FXML
    private TextField newPassWordText;
    @FXML
    private TableView UserTable;
    @FXML
    private TableColumn<User, Integer>  IdColumn;
    @FXML
    private TableColumn<User, String>  nameColumn;    
    @FXML
    private TableColumn<User, String>  UserNameColumn;    
    @FXML
    private TableColumn<User, String>  RoleColumn;
    @FXML
    private ComboBox<Role> RoleCombo;
    @FXML
    private ComboBox<Role> newRoleCombo;
    @FXML
    //For MultiThreading
    private Executor exec;


    //Initializing the controller class.
    //This method is automatically called after the fxml file has been loaded.

    @FXML
    private void initialize () {
        /*
        The setCellValueFactory(...) that we set on the table columns are used to determine
        which field inside the User objects should be used for the particular column.
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
        UserNameColumn.setCellValueFactory(cellData -> cellData.getValue().UserNameProperty());
        RoleColumn.setCellValueFactory(cellData -> cellData.getValue().getRoleName());

        ArrayList<Role> roles = new ArrayList<Role>();
        roles.add(new Role(1,"Admin"));
        roles.add(new Role(2,"Accountant"));
        roles.add(new Role(3,"Trader"));

        RoleCombo.setItems(FXCollections.observableList(roles));
        newRoleCombo.setItems(FXCollections.observableList(roles));
        	
    }


    //Search an User
    @FXML
    private void searchUser (ActionEvent actionEvent) throws ClassNotFoundException, SQLException {
        try {
            //Get User information
            User User = UserDAO.searchUser(IdText.getText());
            //Populate User on TableView and Display on TextArea
            populateAndShowUser(User);
        } catch (SQLException e) {
            e.printStackTrace();
            resultArea.setText("Error occurred while getting User information from DB.\n" + e);
            throw e;
        }
    }

    //Search all Users
    @FXML
    private void searchUsers(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        try {
            //Get all Users information
            ObservableList<User> UserData = UserDAO.searchUsers();
            //Populate Users on TableView
            populateUsers(UserData);
        } catch (SQLException e){
            System.out.println("Error occurred while getting Users information from DB.\n" + e);
            throw e;
        }
    }

    //Populate Users for TableView with MultiThreading (This is for example usage)
    private void fillUserTable(ActionEvent event) throws SQLException, ClassNotFoundException {
        Task<List<User>> task = new Task<List<User>>(){
            @Override
            public ObservableList<User> call() throws Exception{
                return UserDAO.searchUsers();
            }
        };

        task.setOnFailed(e-> task.getException().printStackTrace());
        task.setOnSucceeded(e-> UserTable.setItems((ObservableList<User>) task.getValue()));
        exec.execute(task);
    }

    //Populate User
    @FXML
    private void populateUser (User User) throws ClassNotFoundException {
        //Declare and ObservableList for table view
        ObservableList<User> UserData = FXCollections.observableArrayList();
        //Add User to the ObservableList
        UserData.add(User);
        //Set items to the UserTable
        UserTable.setItems(UserData);
    }

    //Set User information to Text Area
    @FXML
    private void setUserInfoToTextArea ( User User) {
        resultArea.setText("Name: " + User.getName());
    }

    //Populate User for TableView and Display User on TextArea
    @FXML
    private void populateAndShowUser(User User) throws ClassNotFoundException {
        if (User != null) {
            populateUser(User);
            setUserInfoToTextArea(User);
        } else {
            resultArea.setText("This User does not exist!\n");
        }
    }

    //Populate Users for TableView
    @FXML
    private void populateUsers (ObservableList<User> UserData) throws ClassNotFoundException {
        //Set items to the UserTable
        UserTable.setItems(UserData);
    }

    //Update User's email with the email which is written on newEmailText field
    @FXML
    private void updateUserNames (ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        try {
            UserDAO.updateUserName(IdText.getText(),newNameText.getText(), newUserNameText.getText(),newPassWordText.getText(), newRoleCombo.getValue().getId());
            resultArea.setText("Email has been updated for, User id: " + IdText.getText() + "\n");
        } catch (SQLException e) {
            resultArea.setText("Problem occurred while updating email: " + e);
        }
    }

    //Insert an User to the DB
    @FXML
    private void insertUser (ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        try {
            UserDAO.insertUser(nameText.getText(),UserNameText.getText(),PassWordText.getText(),RoleCombo.getValue().getId());
            resultArea.setText("User inserted! \n");
        } catch (SQLException e) {
            resultArea.setText("Problem occurred while inserting User " + e);
            throw e;
        }
    }

    //Delete an User with a given User Id from DB
    @FXML
    private void deleteUser (ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        try {
            UserDAO.deleteUserWithId(IdText.getText());
            resultArea.setText("User deleted! User id: " + IdText.getText() + "\n");
        } catch (SQLException e) {
            resultArea.setText("Problem occurred while deleting User " + e);
            throw e;
        }
    }
}
