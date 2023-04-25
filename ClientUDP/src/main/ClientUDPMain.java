package main;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import threads.Client;

public class ClientUDPMain {

    public static void main(String[] args) {
        try {
            Client client = new Client("localhost", 5000);
            client.connect();
        } catch (SocketException ex) {
            Logger.getLogger(ClientUDPMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(ClientUDPMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
