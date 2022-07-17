package userInterface;

public class Connection {
    private String ip;
    private int port;
    
    public Connection(String ip, Long port) {
        this.ip = ip;
        this.port = Integer.parseInt(Long.toString(port));
    }
    
    public Connection(String ip, String port) {
        this.ip = ip;
        this.port = Integer.parseInt(port);
    }    
    
    public Connection(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }
    public int getPort() {
        return port;
    }
}

