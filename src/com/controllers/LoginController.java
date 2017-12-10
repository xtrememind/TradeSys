package com.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.Main;
import com.models.*;

public class LoginController {
    private static SessionFactory factory;

    @FXML
    private TextField LoginNameText;
    @FXML
    private TextField PassWordText;
    @FXML 
    private Label invalidlbl;

    //For MultiThreading
    private Executor exec;
    
    private Main main;
    
    public void setMain (Main main) {
        this.main = main;
    }

    //Initializing the controller class.
    //This method is automatically called after the fxml file has been loaded.

    @FXML
    private void initialize () {
        /*
        The setCellValueFactory(...) that we set on the table columns are used to determine
        which field inside the Login objects should be used for the particular column.
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

       	
    }


    //Delete an Login with a given Login Id from DB
    @FXML
    private void LoginUser (ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
    	Session session = factory.openSession();
    	CriteriaBuilder builder = session.getCriteriaBuilder();
        try{
        		//Criteria criteria = session.createCriteria(Exchange.class);
        		CriteriaQuery<User> criteria = builder.createQuery(User.class);
        		Root<User> root = criteria.from(User.class);
        		criteria.select(root);
        		criteria.where(builder.equal( root.get("userName"),LoginNameText.getText()));
        		User user = (User) session.createQuery(criteria).uniqueResult();
        		if(user == null)
        			invalidlbl.setText("Invalid username or password");
        		else if (!user.getPassWord().equals(PassWordText.getText()))
        			invalidlbl.setText("Invalid username or password");
        		else {
        			invalidlbl.setText("Login successful");
        			Context.getInstance().setUser(user);

                    RootLayoutController controller = Context.getInstance().getController();
                    controller.toggleMainMenu(false);

        		}
        }catch (HibernateException e) {
           e.printStackTrace(); 
        }finally {
           session.close(); 
        }
    }
}
