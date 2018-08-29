package com.infare;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class FlightInfoDB {
    public FlightInfoDB() throws SQLException {
    }

    Connection conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test",
            "sa", "");
    Statement statement = conn.createStatement();

    public void createFareDataTable() throws SQLException {
    statement.execute("drop TABLE faredata if EXISTS");
    statement.execute("CREATE TABLE FAREDATA (ID VARCHAR (9) PRIMARY KEY, DATE DATE , DEPARTURE varchar(255) ,ARRIVAL varchar(255)  ,DEP_TIME TIME , ARR_TIME TIME ,PRICE DECIMAL ,TAXES DECIMAL )");
}

    public void fillFareData(String id, String date, String depAirport, String arrAirport, String depTime, String arrTime, String price, String taxes) throws SQLException {
        String query1 = String.format("INSERT into FAREDATA " +
                       // "(ID,DATE,DEPARTURE,ARRIVAL,DEP_TIME,ARR_TIME,PRICE,TAXES) " +
                        "VALUES ('%s',PARSEDATETIME('%s','yyyy-MMM-dd'),'%s','%s',PARSEDATETIME ('%s','hh:mm'),PARSEDATETIME ('%s','hh:mm'),'%s','%s')",
                        id, date, depAirport, arrAirport, depTime, arrTime, Double.valueOf(price), Double.valueOf(taxes));
        statement.execute(query1);
    }
}

