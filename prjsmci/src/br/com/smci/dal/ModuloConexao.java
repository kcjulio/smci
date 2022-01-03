/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.smci.dal;

import java.sql.*;

/**
 * Class that establish connection with the database
 * @author julio
 * @version 1.0
 * @since SMCI 1.0
 */
public class ModuloConexao {
    
    /**
     * Method that connects to the database
     * @return Connection instance of the database
     * @since SMCI 1.0
     */
    public static Connection conector() {
        java.sql.Connection conexao = null;
        //A linha abaixo chama o driver
        String driver = "com.mysql.jdbc.Driver";
        //Armazenando informações referentes ao banco
        String url = "jdbc:mysql://localhost:3306/dbsdai?autoReconnect=true&useSSL=false";
        String user = "root";
        String password = "admin";
        //Estabelecendo a conexão com o banco
        try {
            Class.forName(driver);
            conexao = DriverManager.getConnection(url, user, password);
            return conexao;
        } catch (Exception e) {
            return null;
        }
    }
    
}
