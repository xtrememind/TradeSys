package com.models;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.util.DBUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ExchangeDAO {

    //*******************************
    //SELECT an Exchange
    //*******************************
    public static Exchange searchExchange (String Id) throws SQLException, ClassNotFoundException {
        //Declare a SELECT statement
		String selectStmt = "SELECT * FROM Exchange WHERE ID="+Id;

        //Execute SELECT statement
        try {
            //Get ResultSet from dbExecuteQuery method
            ResultSet rsExchange = DBUtil.dbExecuteQuery(selectStmt);

            //Send ResultSet to the getExchangeFromResultSet method and get Exchange object
            Exchange exchange = getExchangeFromResultSet(rsExchange);

            //Return Exchange object
            return exchange;
        } catch (SQLException e) {
            System.out.println("While searching an Exchange with " + Id + " id, an error occurred: " + e);
            //Return exception
            throw e;
        }
    }

    //Use ResultSet from DB as parameter and set Exchange Object's attributes and return Exchange object.
    private static Exchange getExchangeFromResultSet(ResultSet rs) throws SQLException
    {
        Exchange exchange = null;
        if (rs.next()) {
        		exchange = new Exchange();
        		exchange.setId(rs.getInt("ID"));
        		exchange.setName(rs.getString("NAME"));
        		exchange.setShortName(rs.getString("SHORTNAME"));        }
        return exchange;
    }

    //*******************************
    //SELECT Exchanges
    //*******************************
    public static ObservableList<Exchange> searchExchanges () throws SQLException, ClassNotFoundException {
        //Declare a SELECT statement
        String selectStmt = "SELECT * FROM Exchange";

        //Execute SELECT statement
        try {
            //Get ResultSet from dbExecuteQuery method
            ResultSet rsExchange = DBUtil.dbExecuteQuery(selectStmt);

            //Send ResultSet to the getExchangeList method and get Exchange object
            ObservableList<Exchange> exchangeList = getExchangeList(rsExchange);

            //Return Exchange object
            return exchangeList;
        } catch (SQLException e) {
            System.out.println("SQL select operation has been failed: " + e);
            //Return exception
            throw e;
        }
    }

    //Select * from Exchanges operation
    private static ObservableList<Exchange> getExchangeList(ResultSet rs) throws SQLException, ClassNotFoundException {
        //Declare a observable List which comprises of Exchange objects
        ObservableList<Exchange> exchangeList = FXCollections.observableArrayList();

        while (rs.next()) {
            Exchange exchange = new Exchange();
            exchange.setId(rs.getInt("ID"));
            exchange.setName(rs.getString("NAME"));
            exchange.setShortName(rs.getString("SHORTNAME"));

            //Add Exchange to the ObservableList
            exchangeList.add(exchange);
        }
        //return ExchangeList (ObservableList of Exchanges)
        return exchangeList;
    }

    //*************************************
    //UPDATE an Exchange's Name address
    //*************************************
    public static void updateExchangeName (String Id, String Name) throws SQLException, ClassNotFoundException {
        //Declare a UPDATE statement
        String updateStmt =
        		"   UPDATE Exchange\n" +
        				"      SET NAME = '" + Name + "'\n" +
        				"    WHERE Exchange_ID = " + Id + ";";

        //Execute UPDATE operation
        try {
            DBUtil.dbExecuteUpdate(updateStmt);
        } catch (SQLException e) {
            System.out.print("Error occurred while UPDATE Operation: " + e);
            throw e;
        }
    }

    //*************************************
    //UPDATE an Exchange's Short Name address
    //*************************************
    public static void updateExchangeShortName (String Id, String ShortName) throws SQLException, ClassNotFoundException {
        //Declare a UPDATE statement
        String updateStmt =
        		"   UPDATE Exchange\n" +
        				"      SET SHORTNAME = '" + ShortName + "'\n" +
        				"    WHERE Exchange_ID = " + Id + ";";

        //Execute UPDATE operation
        try {
            DBUtil.dbExecuteUpdate(updateStmt);
        } catch (SQLException e) {
            System.out.print("Error occurred while UPDATE Operation: " + e);
            throw e;
        }
    }
    
    //*************************************
    //DELETE an Exchange
    //*************************************
    public static void deleteExchangeWithId (String Id) throws SQLException, ClassNotFoundException {
        //Declare a DELETE statement
        String updateStmt =
                        "   DELETE FROM Exchange\n" +
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
    //INSERT an Exchange
    //*************************************
    public static void insertExchange (String name, String shortName) throws SQLException, ClassNotFoundException {
        //Declare a DELETE statement
        String updateStmt =
        		"INSERT INTO Exchange\n" +
        				"(NAME, SHORTNAME)\n" +
        				"VALUES\n" +
        				"('"+name+"', '"+shortName+"');";

        //Execute DELETE operation
        try {
            DBUtil.dbExecuteUpdate(updateStmt);
        } catch (SQLException e) {
            System.out.print("Error occurred while DELETE Operation: " + e);
            throw e;
        }
    }
}
