package bankingsys.server.model;

import java.util.HashSet;

/**
 * A wrapper for a set of clients with synchronization on set operations
 */
public class MonitoringClients {
    private HashSet<Client> clients = null;

    public MonitoringClients() {
        clients = new HashSet<>();
    }

    public HashSet<Client> getClients() {
        return clients;
    }

    /**
     * Remove a client from the set (synchronized)
     * @param clientToRemove Client to remove
     */
    public synchronized void removeFromClients(Client clientToRemove) {
        clients.remove(clientToRemove);
    }

    /**
     * Add a client to the set (synchronized)
     * @param client Client to add
     */
    public synchronized void addClient(Client client) {
        clients.add(client);
    }

    /**
     * Check whether a client is in the set (synchronized)
     * @param client Client to check
     * @return Whether client is in set
     */
    public synchronized boolean isClientInSet(Client client) {
        return clients.contains(client);
    }
}
