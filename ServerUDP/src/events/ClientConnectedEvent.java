package events;

import java.net.DatagramPacket;
import java.util.EventObject;

public class ClientConnectedEvent extends EventObject{
    String clienData;
    DatagramPacket clientDatagram;
    
    public ClientConnectedEvent(Object source, String clientData, DatagramPacket clientDatagram){
        super(source);
        this.clienData = clientData;
        this.clientDatagram = clientDatagram;
    }

    public String getClienData() {
        return clienData;
    }

    public DatagramPacket getClientDatagram() {
        return clientDatagram;
    }
}
