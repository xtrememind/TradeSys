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

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.models.*;

public class UserController {
    private static SessionFactory factory;
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
    private ComboBox<RoleType> RoleCombo;
    @FXML
    private ComboBox<RoleType> newRoleCombo;
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

        try{
            factory = new Configuration().configure().buildSessionFactory();
         }catch (Throwable ex) { 
          System.err.println("Failed to create sessionFactory object." + ex);
          throw new ExceptionInInitializerError(ex); 
       }
        //For multithreading: Create executor that uses daemon threads:
        exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread (runnable);
            t.setDaemon(true);
            return t;
        });

        IdColumn.setCellValueFactory(cellData -> cellData.getValue().IdProperty().asObject());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().NameProperty());
        UserNameColumn.setCellValueFactory(cellData -> cellData.getValue().UserNameProperty());
        RoleColumn.setCellValueFactory(cellData -> cellData.getValue().roleNameProperty());

/*        ArrayList<Role> roles = new ArrayList<Role>();
        roles.add(new Role(1,"Admin"));
        roles.add(new Role(2,"Accountant"));
        roles.add(new Role(3,"Trader"));

        RoleCombo.setItems(FXCollections.observableList(roles));
        newRoleCombo.setItems(FXCollections.observableList(roles));*/
     	Session session = factory.openSession();
        try{
         		ObservableList<RoleType> roleData = FXCollections.observableList(session.createQuery("FROM RoleType").list());
         		RoleCombo.setItems(roleData);
         		newRoleCombo.setItems(roleData);
         }catch (HibernateException e) {
            e.printStackTrace(); 
         }finally {
            session.close(); 
         }

        	
    }


    //Search an User
    @FXML
    private void searchUser (ActionEvent actionEvent) throws ClassNotFoundException, SQLException {
    	Session session = factory.openSession();
        try{
        		User user = 
                        (User)session.get(User.class, Integer.parseInt(IdText.getText()));
           populateAndShowUser(user);
        }catch (HibernateException e) {
           e.printStackTrace();
           resultArea.setText("Error occurred while getting User information from DB.\n" + e);
        }finally {
           session.close(); 
        }

    }

    //Search all Users
    @FXML
    private void searchUsers(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
     	Session session = factory.openSession();
        try{
         		ObservableList<User> userData = FXCollections.observableList(session.createQuery("FROM User").list());
         		populateUsers(userData);

         }catch (HibernateException e) {
            e.printStackTrace(); 
         }finally {
            session.close(); 
         }
    }

    //Populate Users for TableView with MultiThreading (This is for example usage)
  /*  private void fillUserTable(ActionEvent event) throws SQLException, ClassNotFoundException {
        Task<List<User>> task = new Task<List<User>>(){
            @Override
            public ObservableList<User> call() throws Exception{
                return UserDAO.searchUsers();
            }
        };

        task.setOnFailed(e-> task.getException().printStackTrace());
        task.setOnSucceeded(e-> UserTable.setItems((ObservableList<User>) task.getValue()));
        exec.execute(task);
    }*/

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
        Session session = factory.openSession();
        Transaction tx = null;
        try{
           tx = session.beginTransaction();
           User user = (User)session.get(User.class, Integer.parseInt(IdText.getText())); 
           user.setName(newNameText.getText());
           user.setUserName(newUserNameText.getText());
           user.setPassWord(newPassWordText.getText());
           user.setRoleType(newRoleCombo.getValue());
           session.update(user); 
           tx.commit();
           resultArea.setText("User has been updated for, User id: " + IdText.getText() + "\n");
        }catch (HibernateException e) {
           if (tx!=null) tx.rollback();
           e.printStackTrace();
           resultArea.setText("Problem occurred while updating User: " + e);
        }finally {
           session.close(); 
        }

    }

    //Insert an User to the DB
    @FXML
    private void insertUser (ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        Session session = factory.openSession();
        Transaction tx = null;
        try{
           tx = session.beginTransaction();
           User user = new User();
           user.setName(nameText.getText());
           user.setUserName(UserNameText.getText());
           user.setPassWord(PassWordText.getText());
           user.setRoleType(RoleCombo.getValue());
           session.save(user);
           tx.commit();
           resultArea.setText("User inserted! \n");
        }catch (HibernateException e) {
           if (tx!=null) tx.rollback();
           e.printStackTrace();
           resultArea.setText("Problem occurred while inserting User " + e);
        }finally {
           session.close(); 
        }
    }

    //Delete an User with a given User Id from DB
    @FXML
    private void deleteUser (ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
		Session session = factory.openSession();
		Transaction tx = null;
	    try{
	        tx = session.beginTransaction();
	    		User user = 
	                    (User)session.get(User.class, Integer.parseInt(IdText.getText()));
	    		session.delete(user);
	    		tx.commit();
	    		resultArea.setText("User deleted! User id: " + IdText.getText() + "\n");
	            
	    }catch (HibernateException e) {
	        if (tx!=null) tx.rollback();
	        resultArea.setText("Problem occurred while deleting User " + e);
	        e.printStackTrace(); 
	    }finally {
	       session.close(); 
	    }
    }
}
