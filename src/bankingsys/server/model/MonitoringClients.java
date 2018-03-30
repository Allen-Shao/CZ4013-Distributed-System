package bankingsys.server.model;

import java.util.HashSet;

/**
 * Created by koallen on 30/3/18.
 */
public class MonitoringClients {
    private HashSet<Client> clients = null;

    public MonitoringClients() {
        clients = new HashSet<>();
    }

    public HashSet<Client> getClients() {
        return clients;
    }

    public synchronized void removeFromClients(Client clientToRemove) {
        clients.remove(clientToRemove);
    }

    public synchronized void addClient(Client client) {
        clients.add(client);
    }

    public synchronized boolean isClientInSet(Client client) {
        return clients.contains(client);
    }
}
