package com.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.models.Exchange;

public class ExchangeController {

    private static SessionFactory factory;
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

        IdColumn.setCellValueFactory(new PropertyValueFactory("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory("Name"));
        shortNameColumn.setCellValueFactory(new PropertyValueFactory("ShortName"));
    }


    //Search an Exchange
    @FXML
    private void searchExchange (ActionEvent actionEvent) throws ClassNotFoundException {
    	Session session = factory.openSession();
        try{
        		Exchange exchange = 
                        (Exchange)session.get(Exchange.class, Integer.parseInt(IdText.getText()));
           populateAndShowExchange(exchange);
        }catch (HibernateException e) {
           e.printStackTrace(); 
        }finally {
           session.close(); 
        }
    }

    //Search all Exchanges
    @FXML
    private void searchExchanges(ActionEvent actionEvent) throws ClassNotFoundException {
     	Session session = factory.openSession();
       try{
        		ObservableList<Exchange> exchangeData = FXCollections.observableList(session.createQuery("FROM Exchange").list());
        		populateExchanges(exchangeData);
        }catch (HibernateException e) {
           e.printStackTrace(); 
        }finally {
           session.close(); 
        }
    }

    //Populate Exchanges for TableView with MultiThreading (This is for example usage)
//    private void fillExchangeTable(ActionEvent event) throws SQLException, ClassNotFoundException {
//        Task<List<Exchange>> task = new Task<List<Exchange>>(){
//            @Override
//            public ObservableList<Exchange> call() throws Exception{
//                return ExchangeDAO.searchExchanges();
//            }
//        };
//
//        task.setOnFailed(e-> task.getException().printStackTrace());
//        task.setOnSucceeded(e-> exchangeTable.setItems((ObservableList<Exchange>) task.getValue()));
//        exec.execute(task);
//    }

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
    private void updateExchangeNames (ActionEvent actionEvent) throws ClassNotFoundException {
        Session session = factory.openSession();
        Transaction tx = null;
        try{
           tx = session.beginTransaction();
           Exchange exchange = 
                      (Exchange)session.get(Exchange.class, Integer.parseInt(IdText.getText())); 
           exchange.setName(newNameText.getText());
           exchange.setShortName(newShortNameText.getText());
  	 session.update(exchange); 
           tx.commit();
           resultArea.setText("Exchange has been updated for, Exchange id: " + IdText.getText() + "\n");
        }catch (HibernateException e) {
           if (tx!=null) tx.rollback();
           e.printStackTrace();
           resultArea.setText("Problem occurred while updating Exchange: " + e);
        }finally {
           session.close(); 
        }
    }

    //Insert an Exchange to the DB
    @FXML
    private void insertExchange (ActionEvent actionEvent) throws ClassNotFoundException {
    	
        Session session = factory.openSession();
        Transaction tx = null;
        try{
           tx = session.beginTransaction();
           Exchange exchange = new Exchange();
           exchange.setName(nameText.getText());
           exchange.setShortName(shortNameText.getText()); 
        		   session.save(exchange);
           tx.commit();
        }catch (HibernateException e) {
           if (tx!=null) tx.rollback();
           e.printStackTrace();
        }finally {
           session.close(); 
        }
    }

    //Delete an Exchange with a given Exchange Id from DB
    @FXML
    private void deleteExchange (ActionEvent actionEvent) throws ClassNotFoundException
    {
    		Session session = factory.openSession();
    		Transaction tx = null;
        try{
            tx = session.beginTransaction();
        		Exchange exchange = 
                        (Exchange)session.get(Exchange.class, Integer.parseInt(IdText.getText()));
        		session.delete(exchange);
        		tx.commit();
        		resultArea.setText("Exchange deleted! Exchange id: " + IdText.getText() + "\n");
                
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            resultArea.setText("Problem occurred while deleting Exchange " + e);
            e.printStackTrace(); 
        }finally {
           session.close(); 
        }
    }
}
