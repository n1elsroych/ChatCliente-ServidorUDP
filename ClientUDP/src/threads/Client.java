package threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    private DatagramSocket socket;
    private InetAddress serverAddress;
    private int serverPort;
    //private byte[] buffer = new byte[1024];
    
    private int id;
    
    public Client(String address, int serverPort) throws SocketException, UnknownHostException{
        socket = new DatagramSocket();
        serverAddress = InetAddress.getByName(address);
        this.serverPort = serverPort;
    }
    
    public void connect(){
        firstConnection();
        
        InputHandler inputHandler = new InputHandler(socket);
        inputHandler.start();
        
        Scanner scanner = new Scanner(System.in);
        byte[] buffer = new byte[1024];
        boolean isConnected = true;
        try {
            while (isConnected){
                String message = scanner.nextLine();
                message = "<origin>"+id+";<message>"+message+";";
                buffer = message.getBytes();
                DatagramPacket request = new DatagramPacket(buffer, buffer.length, serverAddress, serverPort);
                socket.send(request);
            }
        } catch(IOException ex) {
            ex.printStackTrace();
            socket.close();
            isConnected = false;
        }
        System.out.println("El bucle se termino en Client");
    }
    
    private void firstConnection(){
        byte[] buffer = new byte[1024];
        String idRequestP = "<id-request>";
        buffer = idRequestP.getBytes();
        DatagramPacket idRequest = new DatagramPacket(buffer, buffer.length, serverAddress, serverPort);
        try {
                System.out.println("Enviando solicitud de id...");
            socket.send(idRequest);
            buffer = new byte[1024];
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                System.out.println("Esperando mi identificador...");
            socket.receive(request);
            String dataMessage = new String(request.getData());
            id = Integer.parseInt(getData("<id>", dataMessage));
                System.out.println("A este usuario se le ha asignado el id = "+id);
            Scanner scanner = new Scanner(System.in);
                System.out.println("Escriba su nickname de usuario:");
            String nickname = scanner.nextLine();
            String responseMsg = "<id>"+id+";<nickname>"+nickname+";";
            buffer = new byte[1024];
            buffer = responseMsg.getBytes();
            DatagramPacket response = new DatagramPacket(buffer, buffer.length, serverAddress, serverPort);
            socket.send(response);
                System.out.println("Enviando mi nickname al servidor");
        } catch (IOException ex) {
            ex.printStackTrace();
            socket.close();
        }
    }
    
    private String getData(String type, String dataMessage){
        int i = dataMessage.indexOf(type) + type.length();
        int f = dataMessage.indexOf(";", i);
        return dataMessage.substring(i, f);
    }
}
