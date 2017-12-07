package com.models;
// Generated Dec 6, 2017 5:39:32 PM by Hibernate Tools 5.2.6.Final

import java.util.HashSet;
import java.util.Set;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Exchange generated by hbm2java
 */
public class Exchange implements java.io.Serializable {

	@Override
	public String toString() {
		return name;
	}

	private Integer id;
	private String name;
	private String shortName;
	private Set<Security> securities = new HashSet<Security>(0);

	public Exchange() {
	}

	public Exchange(String name, String shortName, Set<Security> securities) {
		this.name = name;
		this.shortName = shortName;
		this.securities = securities;
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


	public String getShortName() {
		return this.shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public Set<Security> getSecurities() {
		return this.securities;
	}

	public void setSecurities(Set<Security> securities) {
		this.securities = securities;
	}

}
