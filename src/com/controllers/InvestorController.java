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
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.models.Exchange;
import com.models.Investor;
import com.models.Portfolio;


public class InvestorController {

	private static SessionFactory factory;
	@FXML
    private TextField IdText;
    @FXML
    private TextArea resultArea;
    @FXML
    private TextField nameText;
    @FXML
    private TextField BrokerFeesText;
    @FXML
    private TextField MarketFeesText;
    @FXML
    private TextField newNameText;
    @FXML
    private TextField newBrokerFeesText;
    @FXML
    private TextField newMarketFeesText;

    @FXML
    private TableView investorTable;
    @FXML
    private TableColumn<Investor, Integer>  IdColumn;
    @FXML
    private TableColumn<Investor, String>  nameColumn;
    @FXML
    private TableColumn<Investor, Integer>  brokerFeesColumn;
    @FXML
    private TableColumn<Investor, Integer>  marketFeesColumn;
    @FXML
    //For MultiThreading
    private Executor exec;

    //Initializing the controller class.
    //This method is automatically called after the fxml file has been loaded.

    @FXML
    private void initialize () {
        /*
        The setCellValueFactory(...) that we set on the table columns are used to determine
        which field inside the Investor objects should be used for the particular column.
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
        brokerFeesColumn.setCellValueFactory(new PropertyValueFactory("BrokerFees"));
        marketFeesColumn.setCellValueFactory(new PropertyValueFactory("MarketFees"));


    }


    //Search an Investor
    @FXML
    private void searchInvestor (ActionEvent actionEvent) throws ClassNotFoundException, SQLException {    	Session session = factory.openSession();
    try{
		Investor investor = 
                (Investor)session.get(Investor.class, Integer.parseInt(IdText.getText()));
		populateAndShowInvestor(investor);
		}catch (HibernateException e) {
		   e.printStackTrace(); 
		}finally {
		   session.close(); 
		}
    }

    //Search all Investors
    @FXML
    private void searchInvestors(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
	    		Session session = factory.openSession();
	        try{
	        		ObservableList<Investor> investorData = FXCollections.observableList(session.createQuery("FROM Investor").list());
	        		populateInvestors(investorData);
	        }catch (HibernateException e) {
	           e.printStackTrace(); 
	        }finally {
	           session.close(); 
	        }
    }

    //Populate Investors for TableView with MultiThreading
    private void fillInvestorTable(ActionEvent event) throws SQLException, ClassNotFoundException {
//        Task<List<Investor>> task = new Task<List<Investor>>(){
//            @Override
//            public ObservableList<Investor> call() throws Exception{
//                return InvestorDAO.searchInvestors();
//            }
//        };
//
//        task.setOnFailed(e-> task.getException().printStackTrace());
//        task.setOnSucceeded(e-> investorTable.setItems((ObservableList<Investor>) task.getValue()));
//        exec.execute(task);
    }

    //Populate Investor
    @FXML
    private void populateInvestor (Investor investor) throws ClassNotFoundException {
        //Declare and ObservableList for table view
        ObservableList<Investor> investorData = FXCollections.observableArrayList();
        //Add Investor to the ObservableList
        investorData.add(investor);
        //Set items to the InvestorTable
        investorTable.setItems(investorData);
    }

    //Set Investor information to Text Area
    @FXML
    private void setInvestorInfoToTextArea ( Investor investor) {
        resultArea.setText("Name: " + investor.getName());
    }

    //Populate Investor for TableView and Display Investor on TextArea
    @FXML
    private void populateAndShowInvestor(Investor investor) throws ClassNotFoundException {
        if (investor != null) {
            populateInvestor(investor);
            setInvestorInfoToTextArea(investor);
        } else {
            resultArea.setText("This Investor does not exist!\n");
        }
    }

    //Populate Investors for TableView
    @FXML
    private void populateInvestors (ObservableList<Investor> investorData) throws ClassNotFoundException {
        //Set items to the InvestorTable
        investorTable.setItems(investorData);
    }

    //Update Investor's email with the email which is written on newEmailText field
    @FXML
    private void updateInvestorNames (ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        Session session = factory.openSession();
        Transaction tx = null;
        try{
           tx = session.beginTransaction();
           Investor investor = 
                      (Investor)session.get(Investor.class, Integer.parseInt(IdText.getText())); 
           investor.setName(newNameText.getText());
           Set<Portfolio> portfolios = investor.getPortfolios();
           for (Portfolio portforlio: portfolios)
           {
        	   		portforlio.setBrokerFees(Integer.parseInt(newBrokerFeesText.getText()));
        	   		portforlio.setMarketFees(Integer.parseInt(newMarketFeesText.getText())); 
           }
           investor.setPortfolios(portfolios);
           session.update(investor); 
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

    //Insert an Investor to the DB
    @FXML
    private void insertInvestor (ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        Session session = factory.openSession();
        Transaction tx = null;
        try{
           tx = session.beginTransaction();
           Investor investor = new Investor();
           investor.setName(nameText.getText());
           int investorID = (int)session.save(investor);
           Portfolio portfolio = new Portfolio();
           portfolio.setBrokerFees(Integer.parseInt(BrokerFeesText.getText()));
           portfolio.setMarketFees(Integer.parseInt(MarketFeesText.getText()));
           portfolio.setInvestor(investor);
           session.save(portfolio);
           tx.commit();
           resultArea.setText("Investor inserted! \n");
        }catch (HibernateException e) {
           if (tx!=null) tx.rollback();
           resultArea.setText("Problem occurred while inserting Investor " + e);
           e.printStackTrace();
        }finally {
           session.close(); 
        }
    }

    //Delete an Investor with a given Investor Id from DB
    @FXML
    private void deleteInvestor (ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
//        try {
//            InvestorDAO.deleteInvestorWithId(IdText.getText());
//            resultArea.setText("Investor deleted! Investor id: " + IdText.getText() + "\n");
//        } catch (SQLException e) {
//            resultArea.setText("Problem occurred while deleting Investor " + e);
//            throw e;
//        }
    }
}
