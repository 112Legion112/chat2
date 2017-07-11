package ru.fearofcode.chat2;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Max on 7/10/2017.
 */
public class Server {
    private int port;
    private ServerSocket serverSocket;
    private DBWorker dbWorker;
    private final List<Connection> connections = new LinkedList<>();


    public Server() {
        File file = new File("E:\\Java\\chat2\\src\\main\\resources\\settings.properties");
        try (FileInputStream in = new FileInputStream(file)) {
            Properties properties = new Properties();
            properties.load(in);
            port = Integer.parseInt(properties.getProperty("port"));

            String host = properties.getProperty("host");
            String username = properties.getProperty("username");
            String password = properties.getProperty("password");

            dbWorker = new DBWorker(host, username, password);

        } catch (IOException e) {
            System.err.println("Couldn't set up server");
            e.printStackTrace();
        }

        try {
            serverSocket = new ServerSocket(port);

            while (true) {
                Socket socket = serverSocket.accept();
                Connection connection = new Connection(socket);
                connections.add(connection);
                connection.start();
            }

        } catch (Exception e) {
            System.out.println("Error in Server: " + e.getMessage());
            close();
        }


    }
    private void close() {
        dbWorker.close();
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("Couldn't close socket server");
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        new Server();
    }


    public class Connection extends Thread {
        private String name;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private boolean stop = false;


        public Connection(Socket socket) {
            this.socket = socket;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
                close();
            }
        }

        private void close() {
            try {
                stop = true;
                synchronized(connections) {
                    connections.remove(this);
                }
                in.close();
                out.close();
                socket.close();
                System.out.println("Connection has been delete");
            } catch (Exception e) {
                System.err.println("Couldn't closed connection");
            }
        }

        @Override
        public void run(){
            try {
                name = in.readLine();


                name += ": ";
                System.out.println(name);

                ResultSet resultSet = dbWorker.getMessages();
                while (resultSet.next()){
                    String message = resultSet.getString("message");
                    out.println(message);
                }

            } catch (SQLException e) {
                System.err.println("Couldn't execute query");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (!stop){
                try {
                    String message = name + in.readLine();
                    dbWorker.saveMessage(message);
                    for (Connection connection : connections) {
                        connection.out.println(message);
                    }
                } catch (IOException e) {
                    System.err.println("Couldn't accept message from client");
                    e.printStackTrace();
                    close();
                }
            }

        }
    }
}
