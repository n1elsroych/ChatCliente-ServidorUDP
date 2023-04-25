package models;

import events.ClientConnectedEvent;
import events.ClientDisconnectedEvent;
import events.MessageReceivedEvent;
import events.ServerEventsListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import threads.InputHandler;

public class Server implements ServerEventsListener{
    private DatagramSocket serverSocket;
    private Map<Integer, Client> clients;
    private int clientId;
    
    public Server(int port) throws SocketException{
        serverSocket = new DatagramSocket(port);
        clients = new HashMap<>();
        clientId = 0;
        System.out.println("Se ha iniciado el servidor en el puerto: "+port);
    }
    
    public void start(){
        InputHandler inputHandler = new InputHandler(serverSocket);
        inputHandler.addEventsListener(this);
        inputHandler.start();
    }
    
    private void sendBroadcast(int senderId, String message) throws IOException{
        byte[] buffer = new byte[1024];
        synchronized (clients) {  
            String senderNickname = clients.get(senderId).getNickname();
            message = "["+senderNickname +"]: "+ message;
            buffer = message.getBytes();
            for (Client client : clients.values()){
                int port = client.getPort();
                InetAddress address = client.getAddress();
                DatagramPacket msgDatagram = new DatagramPacket(buffer, buffer.length, address, port);
                serverSocket.send(msgDatagram);
                
                //catch remover de la lista el cliente
            }
        }
    }
    
    private String getData(String type, String dataMessage){
        int i = dataMessage.indexOf(type) + type.length();
        int f = dataMessage.indexOf(";", i);
        return dataMessage.substring(i, f);
    }

    private boolean isIdRequest(String dataMessage){
        return dataMessage.contains("<id-request>");
    }
    
    @Override
    public void onClientConnected(ClientConnectedEvent evt) {
        String clientData = evt.getClienData();
        DatagramPacket clienDatagram = evt.getClientDatagram();
        InetAddress clientAddress = clienDatagram.getAddress();
        int clientPort = clienDatagram.getPort();
        if (isIdRequest(clientData)){
                System.out.println("Un nuevo usuario ha solicitado un id");
            clientId++;
            String idMsg = "<id>"+clientId+";";
            byte[] buffer = new byte[1024];
            buffer = idMsg.getBytes();
            DatagramPacket responseId = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
            try {
                serverSocket.send(responseId);
                    System.out.println("ID enviado al usuario solicitante");
            } catch (IOException ex) {
                ex.printStackTrace();
                clientId--;
            }
        } else {
            int id = Integer.parseInt(getData("<id>", clientData));
            String nickname = getData("<nickname>", clientData);
            Client client = new Client(clientPort, clientAddress);
            client.setId(id);
            client.setNickname(nickname);
            synchronized (clients) {
                clients.put(id, client);
                    System.out.println("El servidor ha registrado correctamente el nickname del usuario");
            }
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent evt) {
        String data = evt.getMessage();
        int senderId = Integer.parseInt(getData("<origin>", data));
        String message = getData("<message>", data);
        try {
            sendBroadcast(senderId, message);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onClientDisconnected(ClientDisconnectedEvent evt) {
        synchronized (clients) {
            clients.remove(evt.getId());
                System.out.println("Un usuario se ha desconectado");
        }
    }
}
