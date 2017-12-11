package com.models;
// Generated Dec 10, 2017 7:29:30 PM by Hibernate Tools 5.2.6.Final

import java.util.Date;

/**
 * PortfolioTransaction generated by hbm2java
 */
public class PortfolioTransaction implements java.io.Serializable {

	private Integer id;
	private Portfolio portfolio;
	private Security security;
	private Date date;
	private String type;
	private int quantity;
	private int totalQuantity;

	public PortfolioTransaction() {
	}

	public PortfolioTransaction(Portfolio portfolio, Security security, Date date, String type, int quantity,
			int totalQuantity) {
		this.portfolio = portfolio;
		this.security = security;
		this.date = date;
		this.type = type;
		this.quantity = quantity;
		this.totalQuantity = totalQuantity;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Portfolio getPortfolio() {
		return this.portfolio;
	}

	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}

	public Security getSecurity() {
		return this.security;
	}

	public void setSecurity(Security security) {
		this.security = security;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getQuantity() {
		return this.quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getTotalQuantity() {
		return this.totalQuantity;
	}

	public void setTotalQuantity(int totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

}
