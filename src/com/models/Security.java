package com.models;

import javafx.beans.property.*;

public class Security {
    //Declare Employees Table Columns
    private IntegerProperty id;
    private StringProperty name;
    private StringProperty code;
    private Exchange exchange;
    //Constructor
    public Security() {
        this.id = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty();
        this.code = new SimpleStringProperty();
        this.exchange = new Exchange();
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
    public String getCode () {
        return code.get();
    }

    public void setCode(String code){
        this.code.set(code);
    }

    public StringProperty CodeProperty() {
        return code;
    }

	public Exchange getExchange() {
		return exchange;
	}
	public StringProperty getExchangeName() {
		StringProperty exchangeName = new SimpleStringProperty();
		exchangeName.set(exchange.getShortName());
		return exchangeName;
	}

	public void setExchange(Exchange exchange) {
		this.exchange = exchange;
	}

    //last_name
    
}
