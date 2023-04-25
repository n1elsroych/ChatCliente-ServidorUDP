package threads;

import events.ClientDisconnectedEvent;
import events.ServerEventsListener;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Map;
import models.Client;

public class DisconnectionsHandler extends Thread{
    private Map<Integer, Client> clients;
    private ArrayList<ServerEventsListener> listeners;

    public DisconnectionsHandler(Map<Integer, Client> clients){
        this.clients = clients;
        listeners = new ArrayList<>();
    }

    @Override
    public void run() {
        while (true){
            synchronized (clients) {
                for (Client client : clients.values()){
                    InetAddress clientAddress = client.getAddress();
                    try {
                        if (!clientAddress.isReachable(2000)){
                            triggerClientDisconnectedEvent(client.getId());
                            //clients.remove(client.getId()); //si puedo hacer esto aqui necesito un evento ??
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void addEventsListener(ServerEventsListener listener){
        listeners.add(listener);
    }
    
    public void removeMiEventoListener(ServerEventsListener listener) {
        listeners.remove(listener);
    }
    
    public void triggerClientDisconnectedEvent(int id) {
        ClientDisconnectedEvent evt = new ClientDisconnectedEvent(this, id);
        for (ServerEventsListener listener : listeners) {
            listener.onClientDisconnected(evt);
        }
    }
}
