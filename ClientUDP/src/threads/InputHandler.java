package threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class InputHandler extends Thread{
    DatagramSocket socket;
    
    public InputHandler(DatagramSocket socket){
        this.socket= socket;
    }
    
    @Override
    public void run() {
        boolean isConnected = true;
        try {
            while (isConnected) {
                byte[] buffer = new byte[1024];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);
                String message = new String(request.getData());
                System.out.println(message);
            }
        } catch(IOException ex) {
            ex.printStackTrace();
            socket.close();
        }
        System.out.println("El bucle se termino en InputHandler");
    }
}
