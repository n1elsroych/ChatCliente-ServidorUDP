package models;

import java.net.InetAddress;

public class Client {
    private int port;
    private InetAddress address;
    
    private int id;
    private String nickname;

    public Client(int port, InetAddress address) {
        this.port = port;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getPort() {
        return port;
    }

    public InetAddress getAddress() {
        return address;
    }
}
