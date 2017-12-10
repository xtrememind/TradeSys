package com.models;

import com.controllers.RootLayoutController;

public class Context {
    private final static Context instance = new Context();
    private static RootLayoutController controller;

    public static Context getInstance() {
        return instance;
    }

    private User user = new User();

    public void setUser(User user) {
         this.user = user;
    }
    public User currentUser() {
        return user;
    }
    public void setController(RootLayoutController controller) {
    	this.controller=controller;
    }
    public RootLayoutController getController() {
    	return controller;
    }
}