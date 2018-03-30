package bankingsys.server.model;

import java.net.InetAddress;

/**
 * Created by koallen on 30/3/18.
 */
public class Client implements Comparable {
    private InetAddress clientAddress;
    private int clientPort;

    public Client(InetAddress clientAddress, int clientPort) {
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
    }

    public InetAddress getClientAddress() {
        return clientAddress;
    }

    public int getClientPort() {
        return clientPort;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Client) {
            Client other = (Client) o;
            if (other.getClientAddress() == clientAddress &&
                    other.getClientPort() == clientPort) {
                return 0;
            }
        }
        return 1;
    }
}
