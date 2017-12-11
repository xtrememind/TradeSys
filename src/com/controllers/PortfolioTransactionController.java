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

public class PortfolioTransactionController {
    private static SessionFactory factory;
    @FXML
    private TextField IdText;
    @FXML
    private TextArea resultArea;
    @FXML
    private TextField typeText;
    @FXML
    private TextField quantityText;
    @FXML
    private TableView PortfolioTransactionTable;
    @FXML
    private TableColumn<PortfolioTransaction, Integer>  IdColumn;
    @FXML
    private TableColumn<PortfolioTransaction, String>  typeColumn;    
    @FXML
    private TableColumn<PortfolioTransaction, String>  quantityColumn;    
    @FXML
    private TableColumn<PortfolioTransaction, String>  portfolioColumn;
    @FXML
    private ComboBox<Investor> investorCombo;
    @FXML
    private ComboBox<Security> SecuritiesCombo;
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
        which field inside the PortfolioTransaction objects should be used for the particular column.
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
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());
        portfolioColumn.setCellValueFactory(cellData -> cellData.getValue().portfolioProperty());


        Session session = factory.openSession();
        try{
         		ObservableList<Investor> accountData = FXCollections.observableList(session.createQuery("FROM Investor").list());
         		ObservableList<Security> securityData = FXCollections.observableList(session.createQuery("FROM Security").list());
         		investorCombo.setItems(accountData);
         		newInvestorCombo.setItems(accountData);
         		SecuritiesCombo.setItems(securityData);
         }catch (HibernateException e) {
            e.printStackTrace(); 
         }finally {
            session.close(); 
         }

        	
    }


    //Search an PortfolioTransaction
    @FXML
    private void searchPortfolioTransaction (ActionEvent actionEvent) throws ClassNotFoundException, SQLException {
    	Session session = factory.openSession();
        try{
        		PortfolioTransaction portfolioTransaction = 
                        (PortfolioTransaction)session.get(PortfolioTransaction.class, Integer.parseInt(IdText.getText()));
           populateAndShowPortfolioTransaction(portfolioTransaction);
        }catch (HibernateException e) {
           e.printStackTrace();
           resultArea.setText("Error occurred while getting PortfolioTransaction information from DB.\n" + e);
        }finally {
           session.close(); 
        }

    }

    //Search all PortfolioTransactions
    @FXML
    private void searchPortfolioTransactions(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
     	Session session = factory.openSession();
        try{
         		ObservableList<PortfolioTransaction> portfolioTransactionData = FXCollections.observableList(session.createQuery("FROM PortfolioTransaction").list());
         		populatePortfolioTransactions(portfolioTransactionData);

         }catch (HibernateException e) {
            e.printStackTrace(); 
         }finally {
            session.close(); 
         }
    }

    //Populate PortfolioTransactions for TableView with MultiThreading (This is for example usage)
  /*  private void fillPortfolioTransactionTable(ActionEvent event) throws SQLException, ClassNotFoundException {
        Task<List<PortfolioTransaction>> task = new Task<List<PortfolioTransaction>>(){
            @Override
            public ObservableList<PortfolioTransaction> call() throws Exception{
                return PortfolioTransactionDAO.searchPortfolioTransactions();
            }
        };

        task.setOnFailed(e-> task.getException().printStackTrace());
        task.setOnSucceeded(e-> PortfolioTransactionTable.setItems((ObservableList<PortfolioTransaction>) task.getValue()));
        exec.execute(task);
    }*/

    //Populate PortfolioTransaction
   @FXML
    private void populatePortfolioTransaction (PortfolioTransaction PortfolioTransaction) throws ClassNotFoundException {
        //Declare and ObservableList for table view
        ObservableList<PortfolioTransaction> PortfolioTransactionData = FXCollections.observableArrayList();
        //Add PortfolioTransaction to the ObservableList
        PortfolioTransactionData.add(PortfolioTransaction);
        //Set items to the PortfolioTransactionTable
        PortfolioTransactionTable.setItems(PortfolioTransactionData);
    }

    //Set PortfolioTransaction information to Text Area
    @FXML
    private void setPortfolioTransactionInfoToTextArea ( PortfolioTransaction PortfolioTransaction) {
        resultArea.setText("Description: " + PortfolioTransaction.getType());
    }

    //Populate PortfolioTransaction for TableView and Display PortfolioTransaction on TextArea
    @FXML
    private void populateAndShowPortfolioTransaction(PortfolioTransaction PortfolioTransaction) throws ClassNotFoundException {
        if (PortfolioTransaction != null) {
            populatePortfolioTransaction(PortfolioTransaction);
            setPortfolioTransactionInfoToTextArea(PortfolioTransaction);
        } else {
            resultArea.setText("This PortfolioTransaction does not exist!\n");
        }
    }

    //Populate PortfolioTransactions for TableView
    @FXML
    private void populatePortfolioTransactions (ObservableList<PortfolioTransaction> PortfolioTransactionData) throws ClassNotFoundException {
        //Set items to the PortfolioTransactionTable
        PortfolioTransactionTable.setItems(PortfolioTransactionData);
    }

    //Update PortfolioTransaction's email with the email which is written on newEmailText field
    
 /*   private void updatePortfolioTransactionNames (ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        Session session = factory.openSession();
        Transaction tx = null;
        try{
           tx = session.beginTransaction();
           PortfolioTransaction portfolioTransaction = (PortfolioTransaction)session.get(PortfolioTransaction.class, Integer.parseInt(IdText.getText())); 
           portfolioTransaction.setAmount(BigDecimal.valueOf(Long.parseLong(newAmountText.getText())));
           portfolioTransaction.setDate(new Date());
           Investor investor = (Investor) session.get(Investor.class, newInvestorCombo.getValue().getId());
   		   PortfolioAccount portfolioAccount = investor.getPortfolioAccounts().toArray(new PortfolioAccount[1])[0];
           portfolioTransaction.setPortfolioAccount(portfolioAccount);
           session.update(portfolioTransaction); 
           tx.commit();
           resultArea.setText("PortfolioTransaction has been updated for, PortfolioTransaction id: " + IdText.getText() + "\n");
        }catch (HibernateException e) {
           if (tx!=null) tx.rollback();
           e.printStackTrace();
           resultArea.setText("Problem occurred while updating PortfolioTransaction: " + e);
        }finally {
           session.close(); 
        }

    }*/

    //Insert an PortfolioTransaction to the DB
    @FXML
    private void insertPortfolioTransaction (ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        Session session = factory.openSession();
        Transaction tx = null;
        try{
        		int quantity = Integer.parseInt(quantityText.getText());
        		Investor investor = (Investor) session.get(Investor.class, investorCombo.getValue().getId());
        		Security security = 
        				(Security)session.get(Security.class, SecuritiesCombo.getValue().getId());
        		Portfolio portfolio = investor.getPortfolios().toArray(new Portfolio[1])[0];
        		PortfolioBalance portfolioBalance = new PortfolioBalance();
        		for (PortfolioBalance portBal : portfolio.getPortfolioBalances())
        		{
        			if (portBal.getSecurity().getId() == security.getId())
        			{
        				portfolioBalance = portBal;
        			}
        		}
 	           tx = session.beginTransaction();
        		if ((portfolioBalance.getBalance() >= Math.abs(quantity) && quantity < 0) ||
        				(portfolioBalance.getBalance() != 0 && quantity > 0))
        		{
    	           PortfolioTransaction portfolioTransaction = new PortfolioTransaction();
    	           portfolioTransaction.setType(typeText.getText());
    	           portfolioTransaction.setDate(new Date());
    	           portfolioTransaction.setQuantity(quantity);
    	           portfolioTransaction.setPortfolio(portfolio);
    	           portfolioTransaction.setTotalQuantity(quantity + portfolioBalance.getBalance());
    	           portfolioTransaction.setSecurity(security);
    	           portfolioBalance.setBalance(quantity + portfolioBalance.getBalance());
    	           portfolioBalance.setSecurity(security);
    	           portfolioBalance.setPortfolio(portfolio);
    	           session.save(portfolioTransaction);
    	           session.update(portfolio);
    	           tx.commit();
    	           resultArea.setText("PortfolioTransaction inserted! \n");
        		}
        		else if (portfolioBalance.getBalance() == 0 && quantity > 0)
        		{
        			PortfolioTransaction portfolioTransaction = new PortfolioTransaction();
        			portfolioTransaction.setType(typeText.getText());
	           portfolioTransaction.setDate(new Date());
	           portfolioTransaction.setPortfolio(portfolio);
	           portfolioTransaction.setQuantity(quantity);
	           portfolioTransaction.setTotalQuantity(quantity + portfolioBalance.getBalance());
	           portfolioTransaction.setSecurity(security);
	           portfolioBalance.setBalance(quantity + portfolioBalance.getBalance());
	           portfolioBalance.setSecurity(security);
	           portfolioBalance.setPortfolio(portfolio);
	           session.save(portfolioTransaction);
	           session.save(portfolioBalance);
	           tx.commit();
	           resultArea.setText("Portfolio Transaction inserted! \n");
        		}
        		else 
        		{
        			resultArea.setText("Investor doesn't have sufficient balance to cover transaction");
        		}
        		
        }catch (HibernateException e) {
           if (tx!=null) tx.rollback();
           e.printStackTrace();
           resultArea.setText("Problem occurred while inserting PortfolioTransaction " + e);
        }finally {
           session.close(); 
        }
    }

    //Delete an PortfolioTransaction with a given PortfolioTransaction Id from DB
    @FXML
    private void deletePortfolioTransaction (ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
		Session session = factory.openSession();
		Transaction tx = null;
	    try{
	        tx = session.beginTransaction();
	    		PortfolioTransaction portfolioTransaction = 
	                    (PortfolioTransaction)session.get(PortfolioTransaction.class, Integer.parseInt(IdText.getText()));
	    		session.delete(portfolioTransaction);
	    		tx.commit();
	    		resultArea.setText("PortfolioTransaction deleted! PortfolioTransaction id: " + IdText.getText() + "\n");
	            
	    }catch (HibernateException e) {
	        if (tx!=null) tx.rollback();
	        resultArea.setText("Problem occurred while deleting PortfolioTransaction " + e);
	        e.printStackTrace(); 
	    }finally {
	       session.close(); 
	    }
    }
}
