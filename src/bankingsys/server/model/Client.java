package bankingsys.server.model;

import java.net.InetAddress;

/**
 * Class that represents a client
 */
public class Client {
    private InetAddress clientAddress;
    private int clientPort;

    /**
     * Create a client
     * @param clientAddress Address of client
     * @param clientPort Port of client
     */
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
    public boolean equals(Object o) {
        if (o instanceof Client) {
            Client other = (Client) o;
            if (other.getClientAddress().equals(clientAddress) &&
                    other.getClientPort() == clientPort) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(clientAddress);
        buffer.append(clientPort);
        return buffer.toString().hashCode();
    }

    @Override
    public String toString() {
        return clientAddress.toString() + ":" + clientPort;
    }
}
