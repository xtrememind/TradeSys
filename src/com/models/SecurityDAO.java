package com.models;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.util.DBUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SecurityDAO {

    //*******************************
    //SELECT an Security
    //*******************************
    public static Security searchSecurity (String Id) throws SQLException, ClassNotFoundException {
        //Declare a SELECT statement
		String selectStmt = "SELECT s.ID as ID, s.Code as CODE, s.Name as NAME, s.ExchangeID as ExchangeID, ShortName FROM Security s left join Exchange e on s.ExchangeID=e.Id where s.id="+Id;

        //Execute SELECT statement
        try {
            //Get ResultSet from dbExecuteQuery method
            ResultSet rsSecurity = DBUtil.dbExecuteQuery(selectStmt);

            //Send ResultSet to the getSecurityFromResultSet method and get Security object
            Security Security = getSecurityFromResultSet(rsSecurity);

            //Return Security object
            return Security;
        } catch (SQLException e) {
            System.out.println("While searching an Security with " + Id + " id, an error occurred: " + e);
            //Return exception
            throw e;
        }
    }

    //Use ResultSet from DB as parameter and set Security Object's attributes and return Security object.
    private static Security getSecurityFromResultSet(ResultSet rs) throws SQLException
    {
        Security Security = null;
        if (rs.next()) {
        		Security = new Security();
        		Security.setId(rs.getInt("ID"));
        		Security.setName(rs.getString("NAME"));        		
        		Security.setCode(rs.getString("CODE"));
                Exchange exchange = new Exchange();
                exchange.setId(rs.getInt("EXCHANGEID"));
                exchange.setShortName(rs.getString("ShortName"));
                Security.setExchange(exchange);		
        }
        return Security;
    }

    //*******************************
    //SELECT Securitys
    //*******************************
    public static ObservableList<Security> searchSecuritys () throws SQLException, ClassNotFoundException {
        //Declare a SELECT statement
        String selectStmt = "SELECT s.ID as ID, s.Name as NAME, s.Code as CODE, s.ExchangeID as ExchangeID, ShortName FROM Security s left join Exchange e on s.ExchangeID=e.Id;";
        
        //Execute SELECT statement
        try {
            //Get ResultSet from dbExecuteQuery method
            ResultSet rsSecurity = DBUtil.dbExecuteQuery(selectStmt);

            //Send ResultSet to the getSecurityList method and get Security object
            ObservableList<Security> SecurityList = getSecurityList(rsSecurity);

            //Return Security object
            return SecurityList;
        } catch (SQLException e) {
            System.out.println("SQL select operation has been failed: " + e);
            //Return exception
            throw e;
        }
    }

    //Select * from Securitys operation
    private static ObservableList<Security> getSecurityList(ResultSet rs) throws SQLException, ClassNotFoundException {
        //Declare a observable List which comprises of Security objects
        ObservableList<Security> SecurityList = FXCollections.observableArrayList();

        while (rs.next()) {
            Security Security = new Security();
            Security.setId(rs.getInt("ID"));
            Security.setName(rs.getString("NAME"));
            Security.setCode(rs.getString("CODE"));
            Exchange exchange = new Exchange();
            exchange.setId(rs.getInt("EXCHANGEID"));
            exchange.setShortName(rs.getString("ShortName"));
            Security.setExchange(exchange);

            //Add Security to the ObservableList
            SecurityList.add(Security);
        }
        //return SecurityList (ObservableList of Securitys)
        return SecurityList;
    }

    //*************************************
    //UPDATE an Security's Name address
    //*************************************
    public static void updateSecurityName (String Id, String Name, String code, int ExchangeID) throws SQLException, ClassNotFoundException {
        //Declare a UPDATE statement
        String updateStmt =
        		"   UPDATE Security\n" +
        				"      SET NAME = '" + Name + "',\n" +
        				"      CODE = '" + code + "',\n" +
        				"      ExchangeID = " + ExchangeID + "\n" +        				
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
    //DELETE an Security
    //*************************************
    public static void deleteSecurityWithId (String Id) throws SQLException, ClassNotFoundException {
        //Declare a DELETE statement
        String updateStmt =
                        "   DELETE FROM Security\n" +
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
    //INSERT an Security
    //*************************************
    public static void insertSecurity (String name, String code, int exchangeID) throws SQLException, ClassNotFoundException {
        //Declare a DELETE statement
        String updateStmt =
        		"INSERT INTO Security\n" +
        				"(CODE, NAME, EXCHANGEID)\n" +
        				"VALUES\n" +
        				"('"+code+"'"+",'"+name+"',"+exchangeID+");";
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
