package main;

import java.net.SocketException;
import models.Server;

public class ServerUDPMain {

    public static void main(String[] args) {
        try {
            Server server = new Server(5000);
            server.start();
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
    }
    
}
