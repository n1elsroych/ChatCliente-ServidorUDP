package threads;

import events.ClientConnectedEvent;
import events.MessageReceivedEvent;
import events.ServerEventsListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

public class InputHandler extends Thread{
    DatagramSocket serverSocket;
    private ArrayList<ServerEventsListener> listeners;
    
    public InputHandler(DatagramSocket serverSocket){
        this.serverSocket = serverSocket;
        listeners = new ArrayList<>();
    }
    
    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        boolean isConnected = true;
        try{
            while (isConnected) {            
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                System.out.println("Esperando mensajes...");
                serverSocket.receive(request);
                String message = new String(request.getData());
                if (isNewClientConnected(message)){
                    triggerClientConnectedEvent(message, request);
                } else {
                    triggerMessageReceivedEvent(message);
                }
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private boolean isNewClientConnected(String message){
        return message.contains("<id>") | message.contains("<id-request>");
    }
    
    public void addEventsListener(ServerEventsListener listener){
        listeners.add(listener);
    }
    
    public void removeMiEventoListener(ServerEventsListener listener) {
        listeners.remove(listener);
    }
    
    public void triggerMessageReceivedEvent(String message) {
        MessageReceivedEvent evt = new MessageReceivedEvent(this, message);
        for (ServerEventsListener listener : listeners) {
            listener.onMessageReceived(evt);
        }
    }
    
    public void triggerClientConnectedEvent(String clientData, DatagramPacket clientDatagram){
        ClientConnectedEvent evt = new ClientConnectedEvent(this, clientData, clientDatagram);
        for (ServerEventsListener listener : listeners) {
            listener.onClientConnected(evt);
        }
    }
}
