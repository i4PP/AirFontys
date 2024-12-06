package me.abdul;

import java.io.*;
import java.net.*;

public class TcpServer {
    private static final int PORT = 5000;

    public static void main(String[] args) {

        try(ServerSocket serverSocket = new ServerSocket(PORT)) {
                Socket socket = serverSocket.accept();
                String clientAddress = socket.getInetAddress().getHostAddress();
                System.out.println(clientAddress +"Client connected");







        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}