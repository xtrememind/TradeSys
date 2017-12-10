package com.models;
// Generated Dec 10, 2017 7:29:30 PM by Hibernate Tools 5.2.6.Final

import java.util.HashSet;
import java.util.Set;

/**
 * Investor generated by hbm2java
 */
public class Investor implements java.io.Serializable {

	private Integer id;
	private String name;
	private Set<Portfolio> portfolios = new HashSet<Portfolio>(0);
	private Set<CashAccount> cashAccounts = new HashSet<CashAccount>(0);

	public String toString() {
		return name;
	}
	public Investor() {
	}

	public Investor(String name, Set<Portfolio> portfolios, Set<CashAccount> cashAccounts) {
		this.name = name;
		this.portfolios = portfolios;
		this.cashAccounts = cashAccounts;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Portfolio> getPortfolios() {
		return this.portfolios;
	}

	public void setPortfolios(Set<Portfolio> portfolios) {
		this.portfolios = portfolios;
	}

	public Set<CashAccount> getCashAccounts() {
		return this.cashAccounts;
	}

	public void setCashAccounts(Set<CashAccount> cashAccounts) {
		this.cashAccounts = cashAccounts;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
