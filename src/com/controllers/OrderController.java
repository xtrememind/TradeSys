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
import oracle.sql.DATE;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.models.Exchange;
import com.models.Investor;
import com.models.Order;
import com.models.Portfolio;
import com.models.Security;
import com.models.User;

public class OrderController {

    private static SessionFactory factory;
    @FXML
    private TextArea resultArea;
    @FXML
    private TextField userText;
    @FXML
    private TextField dateText;
    @FXML
    private TextField quantityText;
    @FXML
    private TextField priceText;
    @FXML
	private Label buyerBrokerFees;
    @FXML
	private Label buyerMarketFees;
    @FXML
	private Label buyerOrderAmount;
    @FXML
	private Label selerBrokerFees;
    @FXML
	private Label sellerMarketFees;
    @FXML
	private Label sellerOrderAmount;
    @FXML
    private ComboBox<Security> SecuritiesCombo;
    @FXML
    private ComboBox<Investor> BuyerPortfolioCombo;
    @FXML
    private ComboBox<Investor> SellerPortfolioCombo;
    

    @FXML
    //For MultiThreading
    private Executor exec;

    @FXML
    private void initialize () {
        try{
            factory = new Configuration().configure().buildSessionFactory();
         }catch (Throwable ex) { 
          System.err.println("Failed to create sessionFactory object." + ex);
          throw new ExceptionInInitializerError(ex); 
       }
        
     	Session session = factory.openSession();
        try{
         		ObservableList<Security> securityData = FXCollections.observableList(session.createQuery("FROM Security").list());
         		SecuritiesCombo.setItems(securityData);

         		ObservableList<Investor> buyerPortfolioData = FXCollections.observableList(session.createQuery("FROM Investor").list());
         		BuyerPortfolioCombo.setItems(buyerPortfolioData);

         		ObservableList<Investor> sellerPortfolioData = FXCollections.observableList(session.createQuery("FROM Investor").list());
         		SellerPortfolioCombo.setItems(sellerPortfolioData);
         }catch (HibernateException e) {
            e.printStackTrace(); 
         }finally {
            session.close(); 
         }
        
        //For multithreading: Create executor that uses daemon threads:
        exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread (runnable);
            t.setDaemon(true);
            return t;
        });
        
        //SecuritiesCombo.setCellFactory(cellData -> cellData.getValue().NameProperty());

    }

    //Insert an Exchange to the DB
    @FXML
    private void insertOrder (ActionEvent actionEvent) throws ClassNotFoundException {
    	
        Session session = factory.openSession();
        Transaction tx = null;
        try{
        		Security security = 
        				(Security)session.get(Security.class, SecuritiesCombo.getValue().getId());
        		Portfolio buyerPortfolio =
        				(Portfolio)session.get(Portfolio.class, BuyerPortfolioCombo.getValue().getId());
        		Portfolio sellerPortfolio = 
        				(Portfolio)session.get(Portfolio.class, SellerPortfolioCombo.getValue().getId());
        		User user = 
        				(User)session.get(User.class, Integer.parseInt(userText.getText()));
        		Order order = new Order();
        		order.setQuantity(Integer.parseInt(quantityText.getText()));
        		order.setPrice(BigDecimal.valueOf(Integer.parseInt(quantityText.getText())));
        		order.setPortfolioByBuyerPortfolioId(buyerPortfolio);
        		order.setPortfolioBySellerPortfolioId(sellerPortfolio);
        		order.setSecurity(security);
        		order.setDate(new Date());
        		order.setUser(user);
        		tx = session.beginTransaction();
        		session.save(order);
        		tx.commit();
        }catch (HibernateException e) {
           if (tx!=null) tx.rollback();
           e.printStackTrace();
        }finally {
           session.close(); 
        }
    }
}
