package com.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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

import com.models.CashAccount;
import com.models.Exchange;
import com.models.Investor;
import com.models.Portfolio;
import com.models.PortfolioBalance;
import com.models.Security;

public class PortfolioBalanceController {

    private static SessionFactory factory;
    @FXML
    private ComboBox<Investor> investorCombo;
    @FXML
    private Label cashBalanceText;
    @FXML
    private TableView portfolioBalanceTable;
    @FXML
    private TableColumn<PortfolioBalance, Integer>  IdColumn;
    @FXML
    private TableColumn<PortfolioBalance, String>  securityColumn;
    @FXML
    private TableColumn<PortfolioBalance, String> balanceColumn;
    @FXML
    //For MultiThreading
    private Executor exec;

    //Initializing the controller class.
    //This method is automatically called after the fxml file has been loaded.

    @FXML
    private void initialize () {
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
        securityColumn.setCellValueFactory(cellData -> cellData.getValue().securityCodeProperty());
        balanceColumn.setCellValueFactory(cellData -> cellData.getValue().balanceProperty());
        //portfolioColumn.setCellValueFactory(cellData -> cellData.getValue().portfolioProperty());
        
        
        Session session = factory.openSession();
        try{
         		ObservableList<Investor> accountData = FXCollections.observableList(session.createQuery("FROM Investor").list());
         		investorCombo.setItems(accountData);
         }catch (HibernateException e) {
            e.printStackTrace(); 
         }finally {
            session.close(); 
         }
    }


    //Search all Exchanges
    @FXML
    private void searchPortfolioBalance(ActionEvent actionEvent) throws ClassNotFoundException {
     	Session session = factory.openSession();
       try{
    	   		Investor investor = (Investor) session.get(Investor.class, investorCombo.getValue().getId());
    	   		Portfolio portfolio = investor.getPortfolios().toArray(new Portfolio[1])[0];
    	   		CashAccount cashAccount = investor.getCashAccounts().toArray(new CashAccount[1])[0];
    	   		cashBalanceText.setText(Double.toString(cashAccount.getBalance().doubleValue()));
        		ObservableList<PortfolioBalance> PortfolioBalanceData = FXCollections.observableArrayList(portfolio.getPortfolioBalances());
        		populatePortfolioBalance(PortfolioBalanceData);
        }catch (HibernateException e) {
           e.printStackTrace(); 
        }finally {
           session.close(); 
        }
    }


    //Populate portfolio Balance for TableView
    @FXML
    private void populatePortfolioBalance (ObservableList<PortfolioBalance> portfolioBalanceData) throws ClassNotFoundException {
        //Set items to the ExchangeTable
        portfolioBalanceTable.setItems(portfolioBalanceData);
    }
}
