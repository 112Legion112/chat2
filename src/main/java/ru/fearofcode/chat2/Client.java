package ru.fearofcode.chat2;

import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;

/**
 * Created by Max on 7/11/2017.
 */
public class Client {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Repeater repeater;



    public Client() {
        Scanner scanner = new Scanner(System.in);

        File file = new File("E:\\Java\\chat2\\src\\main\\resources\\settings.properties");
        try (FileInputStream fileInputStream = new FileInputStream(file)) {

            Properties properties = new Properties();
            properties.load(fileInputStream);
            int port = Integer.parseInt(properties.getProperty("port"));

            String ip = properties.getProperty("serverIP");
            socket = new Socket(ip, port);
            in = new BufferedReader( new InputStreamReader( socket.getInputStream()));
            out = new PrintWriter( socket.getOutputStream(),true);

        } catch (IOException e) {
            System.err.println("Couldn't set up client");
            close();
            e.printStackTrace();
        }


        System.out.println("Please enter your nik name");
        String nik = scanner.nextLine();
        out.println(nik);

        repeater = new Repeater();
        repeater.start();

        String message;
        while(true){
            message = scanner.nextLine();
            out.println(message);
        }

    }
    private void close() {
        try {
            repeater.close();
            out.close();
            in.close();
        } catch (IOException e) {
            System.err.println("Couldn't close connection with server");
            e.printStackTrace();
        }
    }

    private class Repeater extends Thread {

        private boolean stop = false;
        @Override
        public void run(){
            String message;
            while(!stop){
                try {
                    message = in.readLine();

                    System.out.println(message);
                } catch (IOException e) {
                    System.err.println("Couldn't get message from server");
                    e.printStackTrace();
                }

            }
        }
        public void close() {
            stop = true;
        }

    }

    public static void main(String[] args) {
        new Client();
    }
}
