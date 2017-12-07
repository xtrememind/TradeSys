package com.models;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.util.DBUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    //*******************************
    //SELECT an User
    //*******************************
    public static User searchUser (String Id) throws SQLException, ClassNotFoundException {
        //Declare a SELECT statement
		String selectStmt = "select u.ID as ID, Name, UserName, Password, RoleTypeID, RoleName from User u, RoleType r where u.RoleTypeID= r.id and u.ID = "+Id;

        //Execute SELECT statement
        try {
            //Get ResultSet from dbExecuteQuery method
            ResultSet rsUser = DBUtil.dbExecuteQuery(selectStmt);

            //Send ResultSet to the getUserFromResultSet method and get User object
            User User = getUserFromResultSet(rsUser);

            //Return User object
            return User;
        } catch (SQLException e) {
            System.out.println("While searching an User with " + Id + " id, an error occurred: " + e);
            //Return exception
            throw e;
        }
    }

    //Use ResultSet from DB as parameter and set User Object's attributes and return User object.
    private static User getUserFromResultSet(ResultSet rs) throws SQLException
    {
        User User = null;
        if (rs.next()) {
        		User = new User();
        		User.setId(rs.getInt("ID"));
        		User.setName(rs.getString("Name"));        		
        		User.setUserName(rs.getString("UserName"));
                User.setPassWord(rs.getString("Password"));
                User.setRole(new Role(rs.getInt("RoleTypeID"),rs.getString("RoleName")));
        }
        return User;
    }

    //*******************************
    //SELECT Users
    //*******************************
    public static ObservableList<User> searchUsers () throws SQLException, ClassNotFoundException {
        //Declare a SELECT statement
        String selectStmt = "select u.ID as ID, Name, UserName, Password, RoleTypeID, RoleName from User u, RoleType r where u.RoleTypeID= r.id";
        
        //Execute SELECT statement
        try {
            //Get ResultSet from dbExecuteQuery method
            ResultSet rsUser = DBUtil.dbExecuteQuery(selectStmt);

            //Send ResultSet to the getUserList method and get User object
            ObservableList<User> UserList = getUserList(rsUser);

            //Return User object
            return UserList;
        } catch (SQLException e) {
            System.out.println("SQL select operation has been failed: " + e);
            //Return exception
            throw e;
        }
    }

    //Select * from Users operation
    private static ObservableList<User> getUserList(ResultSet rs) throws SQLException, ClassNotFoundException {
        //Declare a observable List which comprises of User objects
        ObservableList<User> UserList = FXCollections.observableArrayList();

        while (rs.next()) {
            User User = new User();
    		User.setId(rs.getInt("ID"));
    		User.setName(rs.getString("Name"));        		
    		User.setUserName(rs.getString("UserName"));
            User.setPassWord(rs.getString("Password"));
            User.setRole(new Role(rs.getInt("RoleTypeID"),rs.getString("RoleName")));

            //Add User to the ObservableList
            UserList.add(User);
        }
        //return UserList (ObservableList of Users)
        return UserList;
    }

    //*************************************
    //UPDATE an User's Name address
    //*************************************
    public static void updateUserName (String Id, String Name, String userName, String passWord, int role) throws SQLException, ClassNotFoundException {
        //Declare a UPDATE statement
        String updateStmt =
        		"   UPDATE User\n" +
        				"      SET NAME = '" + Name + "',\n" +
        				"      UserName = '" + userName + "',\n" +
        				"      PassWord = '" + passWord + "',\n" +     				
        				"      RoleTypeID = " + role + "\n" +        				
        				"    WHERE ID = " + Id + ";";
        System.out.println(updateStmt);
        //Execute UPDATE operation
        try {
            DBUtil.dbExecuteUpdate(updateStmt);
        } catch (SQLException e) {
            System.out.print("Error occurred while UPDATE Operation: " + e);
            throw e;
        }
    }

    
    //*************************************
    //DELETE an User
    //*************************************
    public static void deleteUserWithId (String Id) throws SQLException, ClassNotFoundException {
        //Declare a DELETE statement
        String updateStmt =
                        "   DELETE FROM User\n" +
                        "         WHERE id ="+ Id +";";

        //Execute UPDATE operation
        try {
            DBUtil.dbExecuteUpdate(updateStmt);
        } catch (SQLException e) {
            System.out.print("Error occurred while DELETE Operation: " + e);
            throw e;
        }
    }

    //*************************************
    //INSERT an User
    //*************************************
    public static void insertUser (String name, String userName, String passWord, int role) throws SQLException, ClassNotFoundException {
        //Declare a DELETE statement
        String updateStmt =
        		"INSERT INTO User\n" +
        				"(NAME, UserName, PassWord, RoleTypeID)\n" +
        				"VALUES\n" +
        				"('"+name+"','"+userName+"','"+passWord+"',"+role+");";
        System.out.println(updateStmt);
        //Execute DELETE operation
        try {
            DBUtil.dbExecuteUpdate(updateStmt);
        } catch (SQLException e) {
            System.out.print("Error occurred while DELETE Operation: " + e);
            throw e;
        }
    }
}
