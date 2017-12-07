package com.models;

import javafx.beans.property.*;

public class User {
    //Declare Employees Table Columns
    private IntegerProperty id;
    private StringProperty name;
    private StringProperty userName;
    private StringProperty passWord;
    private Role role;

    //Constructor
    public User() {
        this.id = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty();
        this.userName = new SimpleStringProperty();
        this.passWord = new SimpleStringProperty();
        this.role = new Role(0,"Admin");

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
    public String getUserName () {
        return userName.get();
    }

    public void setUserName(String code){
        this.userName.set(code);
    }

    public StringProperty UserNameProperty() {
        return userName;
    }

    public String getPassWord () {
        return passWord.get();
    }

    public void setPassWord(String code){
        this.passWord.set(code);
    }

    public StringProperty PassWordProperty() {
        return passWord;
    }
    public Role getRole() {
		return role;
	}
	public StringProperty getRoleName() {
		StringProperty roleName = new SimpleStringProperty();
		roleName.set(role.getRoleName());
		return roleName;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	
    //last_name
    
}
