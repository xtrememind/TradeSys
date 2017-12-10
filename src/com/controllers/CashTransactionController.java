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

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.Date;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.models.*;

public class CashTransactionController {
    private static SessionFactory factory;
    @FXML
    private TextField IdText;
    @FXML
    private TextArea resultArea;
    @FXML
    private TextField descriptionText;
    @FXML
    private TextField amountText;
    @FXML
    private TableView CashTransactionTable;
    @FXML
    private TableColumn<CashTransaction, Integer>  IdColumn;
    @FXML
    private TableColumn<CashTransaction, String>  descriptionColumn;    
    @FXML
    private TableColumn<CashTransaction, String>  amountColumn;    
    @FXML
    private TableColumn<CashTransaction, String>  accountColumn;
    @FXML
    private ComboBox<Investor> investorCombo;
    @FXML
    private ComboBox<Investor> newInvestorCombo;

    //For MultiThreading
    private Executor exec;


    //Initializing the controller class.
    //This method is automatically called after the fxml file has been loaded.

    @FXML
    private void initialize () {
        /*
        The setCellValueFactory(...) that we set on the table columns are used to determine
        which field inside the CashTransaction objects should be used for the particular column.
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
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty());
        accountColumn.setCellValueFactory(cellData -> cellData.getValue().accountProperty());


        Session session = factory.openSession();
        try{
         		ObservableList<Investor> accountData = FXCollections.observableList(session.createQuery("FROM Investor").list());
         		investorCombo.setItems(accountData);
         		newInvestorCombo.setItems(accountData);
         }catch (HibernateException e) {
            e.printStackTrace(); 
         }finally {
            session.close(); 
         }

        	
    }


    //Search an CashTransaction
    @FXML
    private void searchCashTransaction (ActionEvent actionEvent) throws ClassNotFoundException, SQLException {
    	Session session = factory.openSession();
        try{
        		CashTransaction cashTransaction = 
                        (CashTransaction)session.get(CashTransaction.class, Integer.parseInt(IdText.getText()));
           populateAndShowCashTransaction(cashTransaction);
        }catch (HibernateException e) {
           e.printStackTrace();
           resultArea.setText("Error occurred while getting CashTransaction information from DB.\n" + e);
        }finally {
           session.close(); 
        }

    }

    //Search all CashTransactions
    @FXML
    private void searchCashTransactions(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
     	Session session = factory.openSession();
        try{
         		ObservableList<CashTransaction> cashTransactionData = FXCollections.observableList(session.createQuery("FROM CashTransaction").list());
         		populateCashTransactions(cashTransactionData);

         }catch (HibernateException e) {
            e.printStackTrace(); 
         }finally {
            session.close(); 
         }
    }

    //Populate CashTransactions for TableView with MultiThreading (This is for example usage)
  /*  private void fillCashTransactionTable(ActionEvent event) throws SQLException, ClassNotFoundException {
        Task<List<CashTransaction>> task = new Task<List<CashTransaction>>(){
            @Override
            public ObservableList<CashTransaction> call() throws Exception{
                return CashTransactionDAO.searchCashTransactions();
            }
        };

        task.setOnFailed(e-> task.getException().printStackTrace());
        task.setOnSucceeded(e-> CashTransactionTable.setItems((ObservableList<CashTransaction>) task.getValue()));
        exec.execute(task);
    }*/

    //Populate CashTransaction
   @FXML
    private void populateCashTransaction (CashTransaction CashTransaction) throws ClassNotFoundException {
        //Declare and ObservableList for table view
        ObservableList<CashTransaction> CashTransactionData = FXCollections.observableArrayList();
        //Add CashTransaction to the ObservableList
        CashTransactionData.add(CashTransaction);
        //Set items to the CashTransactionTable
        CashTransactionTable.setItems(CashTransactionData);
    }

    //Set CashTransaction information to Text Area
    @FXML
    private void setCashTransactionInfoToTextArea ( CashTransaction CashTransaction) {
        resultArea.setText("Description: " + CashTransaction.getDescription());
    }

    //Populate CashTransaction for TableView and Display CashTransaction on TextArea
    @FXML
    private void populateAndShowCashTransaction(CashTransaction CashTransaction) throws ClassNotFoundException {
        if (CashTransaction != null) {
            populateCashTransaction(CashTransaction);
            setCashTransactionInfoToTextArea(CashTransaction);
        } else {
            resultArea.setText("This CashTransaction does not exist!\n");
        }
    }

    //Populate CashTransactions for TableView
    @FXML
    private void populateCashTransactions (ObservableList<CashTransaction> CashTransactionData) throws ClassNotFoundException {
        //Set items to the CashTransactionTable
        CashTransactionTable.setItems(CashTransactionData);
    }

    //Update CashTransaction's email with the email which is written on newEmailText field
    
 /*   private void updateCashTransactionNames (ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        Session session = factory.openSession();
        Transaction tx = null;
        try{
           tx = session.beginTransaction();
           CashTransaction cashTransaction = (CashTransaction)session.get(CashTransaction.class, Integer.parseInt(IdText.getText())); 
           cashTransaction.setAmount(BigDecimal.valueOf(Long.parseLong(newAmountText.getText())));
           cashTransaction.setDate(new Date());
           Investor investor = (Investor) session.get(Investor.class, newInvestorCombo.getValue().getId());
   		   CashAccount cashAccount = investor.getCashAccounts().toArray(new CashAccount[1])[0];
           cashTransaction.setCashAccount(cashAccount);
           session.update(cashTransaction); 
           tx.commit();
           resultArea.setText("CashTransaction has been updated for, CashTransaction id: " + IdText.getText() + "\n");
        }catch (HibernateException e) {
           if (tx!=null) tx.rollback();
           e.printStackTrace();
           resultArea.setText("Problem occurred while updating CashTransaction: " + e);
        }finally {
           session.close(); 
        }

    }*/

    //Insert an CashTransaction to the DB
    @FXML
    private void insertCashTransaction (ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        Session session = factory.openSession();
        Transaction tx = null;
        try{
           tx = session.beginTransaction();
           CashTransaction cashTransaction = new CashTransaction();
           cashTransaction.setDescription(descriptionText.getText());
           cashTransaction.setDate(new Date());
           Investor investor = (Investor) session.get(Investor.class, investorCombo.getValue().getId());
   		   CashAccount cashAccount = investor.getCashAccounts().toArray(new CashAccount[1])[0];
   		   BigDecimal balance = cashAccount.getBalance();
   		   BigDecimal amount = BigDecimal.valueOf(Long.parseLong(amountText.getText()));
           if(balance.doubleValue()+amount.doubleValue()<0) {
        	   resultArea.setText("Investor doesn't have sufficient balance to cover transaction");
           }
           else {
	   		   balance.add(amount);
	   		   cashTransaction.setAmount(amount); 
	   		   cashTransaction.setBalance(balance);
	   		   cashAccount.setBalance(balance);
	           cashTransaction.setCashAccount(cashAccount);
	           session.save(cashTransaction);
	           session.update(cashAccount);
	           tx.commit();
	           resultArea.setText("CashTransaction inserted! \n");
           }
        }catch (HibernateException e) {
           if (tx!=null) tx.rollback();
           e.printStackTrace();
           resultArea.setText("Problem occurred while inserting CashTransaction " + e);
        }finally {
           session.close(); 
        }
    }

    //Delete an CashTransaction with a given CashTransaction Id from DB
    @FXML
    private void deleteCashTransaction (ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
		Session session = factory.openSession();
		Transaction tx = null;
	    try{
	        tx = session.beginTransaction();
	    		CashTransaction cashTransaction = 
	                    (CashTransaction)session.get(CashTransaction.class, Integer.parseInt(IdText.getText()));
	    		session.delete(cashTransaction);
	    		tx.commit();
	    		resultArea.setText("CashTransaction deleted! CashTransaction id: " + IdText.getText() + "\n");
	            
	    }catch (HibernateException e) {
	        if (tx!=null) tx.rollback();
	        resultArea.setText("Problem occurred while deleting CashTransaction " + e);
	        e.printStackTrace(); 
	    }finally {
	       session.close(); 
	    }
    }
}
