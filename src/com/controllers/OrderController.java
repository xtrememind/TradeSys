package com.controllers;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.models.CashAccount;
import com.models.CashTransaction;
import com.models.Context;
import com.models.Investor;
import com.models.Order;
import com.models.Portfolio;
import com.models.PortfolioBalance;
import com.models.PortfolioTransaction;
import com.models.Security;
import com.models.User;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class OrderController {

    private static SessionFactory factory;
    @FXML
    private TextArea resultArea;
    @FXML
    private TextField dateText;
    @FXML
    private TextField quantityText;
    @FXML
    private TextField priceText;
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
        		Investor buyer = (Investor) session.get(Investor.class, BuyerPortfolioCombo.getValue().getId());
        		Investor seller = (Investor) session.get(Investor.class, SellerPortfolioCombo.getValue().getId());
        		Portfolio buyerPortfolio = buyer.getPortfolios().toArray(new Portfolio[1])[0];
        		CashAccount buyerCashAccount = buyer.getCashAccounts().toArray(new CashAccount[1])[0];
        		Portfolio sellerPortfolio = seller.getPortfolios().toArray(new Portfolio[1])[0];
        		CashAccount sellerCashAccount = seller.getCashAccounts().toArray(new CashAccount[1])[0];
        	

        		User user = Context.getInstance().currentUser();
        		int quantity = Integer.parseInt(quantityText.getText());
        		BigDecimal price = BigDecimal.valueOf(Integer.parseInt(priceText.getText()));        		
        		BigDecimal buyerBrokerFees = price.multiply(new BigDecimal(quantity)
        				.multiply(new BigDecimal(0.01)).multiply(new BigDecimal(buyerPortfolio.getBrokerFees())));
        		BigDecimal sellerBrokerFees = price.multiply(new BigDecimal(quantity)
        				.multiply(new BigDecimal(0.01)).multiply(new BigDecimal(sellerPortfolio.getBrokerFees())));
        		BigDecimal buyerMarketFees = price.multiply(new BigDecimal(quantity)
        				.multiply(new BigDecimal(0.01)).multiply(new BigDecimal(buyerPortfolio.getMarketFees()))); 
        		BigDecimal sellerMarketFees = price.multiply(new BigDecimal(quantity)
        				.multiply(new BigDecimal(0.01)).multiply(new BigDecimal(sellerPortfolio.getMarketFees())));
        		BigDecimal buyerOrderAmount = price.multiply(new BigDecimal(quantity))
        				.add(buyerBrokerFees).add(buyerMarketFees);  
        		BigDecimal sellerOrderAmount =  price.multiply(new BigDecimal(quantity))
        				.subtract(sellerBrokerFees).subtract(sellerMarketFees);

        		PortfolioBalance buyerPortfolioBalance = new PortfolioBalance();
        		for (PortfolioBalance portBal : buyerPortfolio.getPortfolioBalances())
        		{
        			if (portBal.getSecurity().getId() == security.getId())
        			{
        				buyerPortfolioBalance = portBal;
        			}
        		}
        		 
        		PortfolioBalance sellerPortfolioBalance = new PortfolioBalance();
        		for (PortfolioBalance portBal : sellerPortfolio.getPortfolioBalances())
        		{
        			if (portBal.getSecurity().getId() == security.getId())
        			{
        				sellerPortfolioBalance = portBal;
        			}
        		}
        		
        		if (buyerCashAccount.getBalance().compareTo(buyerOrderAmount) == 1
        				& sellerPortfolioBalance.getBalance() >= quantity)
        		{
        			sellerPortfolioBalance.setBalance( sellerPortfolioBalance.getBalance()- quantity);
        			if (buyerPortfolioBalance.getId() == null)
        			{
        				buyerPortfolioBalance.setBalance(quantity);
        				buyerPortfolioBalance.setSecurity(security);
        				buyerPortfolioBalance.setPortfolio(buyerPortfolio);
        			}
        			else
        			{
        				buyerPortfolioBalance.setBalance(quantity + buyerPortfolioBalance.getBalance());
        			}
        			
	        		Order order = new Order();
	        		order.setQuantity(quantity);
	        		order.setPrice(price);
	        		order.setPortfolioByBuyerPortfolioId(buyerPortfolio);
	        		order.setPortfolioBySellerPortfolioId(sellerPortfolio);
	        		order.setSecurity(security);
	        		order.setDate(new Date());
	        		order.setUser(user);
	        		order.setBuyerBrokerFees(buyerBrokerFees);
	        		order.setBuyerMarketFees(buyerMarketFees);
	        		order.setSellerBrokerFees(sellerBrokerFees);
	        		order.setSellerMarketFees(sellerMarketFees);
	        		order.setBuyerOrderAmount(buyerOrderAmount);
	        		order.setSellerOrderAmount(sellerOrderAmount);
	        		tx = session.beginTransaction();
	        		
	        		CashTransaction buyerCashTransaction = new CashTransaction();
	        		buyerCashTransaction.setAmount(buyerOrderAmount.multiply(new BigDecimal(-1)));
	        		buyerCashTransaction.setDate(new Date());
	        		buyerCashTransaction.setDescription("Buy " + security.getCode());
	        		buyerCashTransaction.setCashAccount(buyerCashAccount);
	        		buyerCashTransaction.setBalance(buyerCashAccount.getBalance().subtract(buyerOrderAmount));
	        		buyerCashAccount.setBalance(buyerCashAccount.getBalance().subtract(buyerOrderAmount));
	        		
	        		PortfolioTransaction buyerPortfolioTransaction = new PortfolioTransaction();
	        		buyerPortfolioTransaction.setDate(new Date());
	        		buyerPortfolioTransaction.setSecurity(security);
	        		buyerPortfolioTransaction.setQuantity(quantity);
	        		buyerPortfolioTransaction.setPortfolio(buyerPortfolio);
	        		buyerPortfolioTransaction.setType("buy shares");
	        		buyerPortfolioTransaction.setTotalQuantity(buyerPortfolioBalance.getBalance());
	        			        		
	        		CashTransaction sellerCashTransaction = new CashTransaction();
	        		sellerCashTransaction.setAmount(sellerOrderAmount);
	        		sellerCashTransaction.setDate(new Date());
	        		sellerCashTransaction.setDescription("Sell " + security.getCode());
	        		sellerCashTransaction.setCashAccount(sellerCashAccount);
	        		sellerCashTransaction.setBalance(sellerCashAccount.getBalance().add(sellerOrderAmount));
	        		sellerCashAccount.setBalance(sellerCashAccount.getBalance().add(sellerOrderAmount));

	        		PortfolioTransaction sellerPortfolioTransaction = new PortfolioTransaction();
	        		sellerPortfolioTransaction.setDate(new Date());
	        		sellerPortfolioTransaction.setSecurity(security);
	        		sellerPortfolioTransaction.setQuantity(quantity);
	        		sellerPortfolioTransaction.setPortfolio(sellerPortfolio);
	        		sellerPortfolioTransaction.setType("sell shares");
	        		sellerPortfolioTransaction.setTotalQuantity(sellerPortfolioBalance.getBalance());
	        		
	        		session.save(order);
	        		session.save(buyerCashAccount);
	        		session.save(buyerCashTransaction);
	        		session.save(buyerPortfolioBalance);
	        		session.save(buyerPortfolioTransaction);
	        		session.save(sellerCashAccount);
	        		session.save(sellerCashTransaction);
	        		session.save(sellerPortfolioBalance);
	        		session.save(sellerPortfolioTransaction);
	        		
	        		tx.commit();
                resultArea.setText("Order successfully created\n" +
                		"buyer total fees = " +  buyerBrokerFees.add(buyerMarketFees).doubleValue() + "\n" + 
                		"buyer order amount = "+ buyerOrderAmount.doubleValue() + "\n" + 
                		"seller total fees = " + sellerBrokerFees.add(sellerMarketFees).doubleValue() + "\n" + 
                		"seller order amount = "+ sellerOrderAmount.doubleValue() + "\n" 
                		);
        		}
        		else
        		{
        			resultArea.setText("Buyer doesn't have suffecient balance\n");
        		}
        }catch (HibernateException e) {
           if (tx!=null) tx.rollback();
           e.printStackTrace();
           resultArea.setText("Problem occurred while placing Order: " + e);
        }finally {
           session.close(); 
        }
    }
}
