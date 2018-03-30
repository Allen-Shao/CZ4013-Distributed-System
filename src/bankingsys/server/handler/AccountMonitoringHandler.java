package bankingsys.server.handler;

import bankingsys.message.ServiceRequest;
import bankingsys.message.ServiceResponse;
import bankingsys.server.model.BankAccount;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.HashSet;

import static bankingsys.message.ServiceResponse.ResponseType.FAILURE;
import static bankingsys.message.ServiceResponse.ResponseType.SUCCESS;

/**
 * Handler for registering monitoring clients
 */
public class AccountMonitoringHandler extends ServiceHandler {
    private HashSet<InetAddress> clients;

    public AccountMonitoringHandler(HashMap<Integer, BankAccount> accounts, HashSet<InetAddress> clients) {
        super(accounts);
        this.clients = clients;
    }

    @Override
    public ServiceResponse handleRequest(ServiceRequest request) {
        System.out.println("AccountMonitoringHandler called");
        if (!clients.contains(request.getRequestAddress())) {
            clients.add(request.getRequestAddress());
            return new ServiceResponse(SUCCESS, null, "Monitoring callback registered", null);
        }
        return new ServiceResponse(FAILURE, null, "Monitoring callback already registered", null);
    }
}
