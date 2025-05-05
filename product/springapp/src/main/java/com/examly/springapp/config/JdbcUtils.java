package com.examly.springapp.config;

import java.sql.*;

public class JdbcUtils {

    public static Connection getConnection(){
        try{
            String url = "jdbc:mysql://localhost:3306/appdb";
            String user = "root";
            String pwd = "examly";

            Connection con = DriverManager.getConnection(url, user, pwd);
            return con;
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }
    
}
