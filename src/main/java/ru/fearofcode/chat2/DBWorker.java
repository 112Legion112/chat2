package ru.fearofcode.chat2;

import java.sql.*;

/**
 * Created by Max on 7/11/2017.
 */
public class DBWorker {
    private String HOST;
    private String USERNAME;
    private String PASSWORD;
    private Connection connection;
    private Statement statement;
    private PreparedStatement preparedStatement;

    private final static String ADD_MESSAGE = "INSERT INTO messages SET message=?;";
    private final static String GET_MESSAGES = "SELECT message FROM messages ORDER by id DESC LIMIT 10;";

    public DBWorker(String host, String username, String password) {
        HOST = host;
        USERNAME = username;
        PASSWORD = password;

        try {
            connection  = DriverManager.getConnection(HOST, USERNAME, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Couldn't connect with database");
            e.printStackTrace();
        }
    }

    public ResultSet getMessages() throws SQLException {
            statement = connection.createStatement();
            return (statement.executeQuery(GET_MESSAGES));
    }

    public void saveMessage(String message){
        try {
            preparedStatement = connection.prepareStatement(ADD_MESSAGE);
            preparedStatement.setString(1,message);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Couldn't add massage to database");
            e.printStackTrace();
        }

    }



    public void close(){
        try {
            connection.close();
        } catch (SQLException e) {
            System.err.println("Couldn't close connection with database");
            e.printStackTrace();
        }
    }


}
