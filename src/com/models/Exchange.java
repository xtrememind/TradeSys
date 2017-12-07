package com.models;
// Generated Dec 6, 2017 5:39:32 PM by Hibernate Tools 5.2.6.Final

import java.util.HashSet;
import java.util.Set;

/**
 * Exchange generated by hbm2java
 */
public class Exchange implements java.io.Serializable {

	private Integer id;
	private String name;
	private String shortName;
	private Set<Security> securities = new HashSet<Security>(0);

import javafx.beans.property.*;

public class Exchange {
    //Declare Employees Table Columns
    private IntegerProperty id;
    private StringProperty name;
    private StringProperty short_name;
    //Constructor
    public Exchange() {
        this.id = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty();
        this.short_name = new SimpleStringProperty();
    }

    //employee_id
    public int getId() {
        return id.get();
    }

    public void setId(int Id){
        this.id.set(Id);
    }

    public IntegerProperty IdProperty(){
        return id;
    }

    //first_name
    public String getName () {
        return name.get();
    }

    public void setName(String Name){
        this.name.set(Name);
    }

    public StringProperty NameProperty() {
        return name;
    }

    //last_name
    public String getShortName () {
        return short_name.get();
    }

    public void setShortName(String ShortName){
        this.short_name.set(ShortName);
    }

    public StringProperty ShortNameProperty() {
        return short_name;
    }

	@Override
	public String toString() {
		return short_name.get();
	}
}
