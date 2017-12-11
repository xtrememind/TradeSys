package com.models;
// Generated Dec 10, 2017 7:29:30 PM by Hibernate Tools 5.2.6.Final

import java.util.HashSet;
import java.util.Set;

/**
 * Security generated by hbm2java
 */
public class Security implements java.io.Serializable {

	private Integer id;
	private Exchange exchange;
	private String name;
	private String code;
	private Set<PortfolioTransaction> portfolioTransactions = new HashSet<PortfolioTransaction>(0);
	private Set<PortfolioBalance> portfolioBalances = new HashSet<PortfolioBalance>(0);
	private Set<Order> orders = new HashSet<Order>(0);
	private Set<SecurityPrice> securityPrices = new HashSet<SecurityPrice>(0);

	public Security() {
	}

	public Security(Exchange exchange, String name, String code) {
		this.exchange = exchange;
		this.name = name;
		this.code = code;
	}

	public Security(Exchange exchange, String name, String code, Set<PortfolioTransaction> portfolioTransactions,
			Set<PortfolioBalance> portfolioBalances, Set<Order> orders, Set<SecurityPrice> securityPrices) {
		this.exchange = exchange;
		this.name = name;
		this.code = code;
		this.portfolioTransactions = portfolioTransactions;
		this.portfolioBalances = portfolioBalances;
		this.orders = orders;
		this.securityPrices = securityPrices;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Exchange getExchange() {
		return this.exchange;
	}

	public void setExchange(Exchange exchange) {
		this.exchange = exchange;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Set<PortfolioTransaction> getPortfolioTransactions() {
		return this.portfolioTransactions;
	}

	public void setPortfolioTransactions(Set<PortfolioTransaction> portfolioTransactions) {
		this.portfolioTransactions = portfolioTransactions;
	}

	public Set<PortfolioBalance> getPortfolioBalances() {
		return this.portfolioBalances;
	}

	public void setPortfolioBalances(Set<PortfolioBalance> portfolioBalances) {
		this.portfolioBalances = portfolioBalances;
	}

	public Set<Order> getOrders() {
		return this.orders;
	}

	public void setOrders(Set<Order> orders) {
		this.orders = orders;
	}

	public Set<SecurityPrice> getSecurityPrices() {
		return this.securityPrices;
	}

	public void setSecurityPrices(Set<SecurityPrice> securityPrices) {
		this.securityPrices = securityPrices;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
