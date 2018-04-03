package bankingsys.server.handler;

import bankingsys.message.ServiceRequest;
import bankingsys.message.ServiceResponse;
import bankingsys.server.RequestReceiver;
import bankingsys.server.model.BankAccount;

import java.util.HashMap;

/**
 * Abstract class to be inherited by all concrete service handlers
 */
public abstract class ServiceHandler {
    protected HashMap<Integer, BankAccount> accounts;
    protected RequestReceiver server;

    public ServiceHandler(HashMap<Integer, BankAccount> accounts, RequestReceiver server) {
        this.accounts = accounts;
        this.server = server;
    }
    public abstract ServiceResponse handleRequest(ServiceRequest request, boolean simulation);

    protected boolean authenticate(ServiceRequest request) {
        if (accounts.containsKey(request.getRequestAccount())) {
            BankAccount account = accounts.get(request.getRequestAccount());
            if (account.getPassword().equals(request.getRequestPassword()) &&
                    account.getName().equals(request.getRequestName()))
                return true;
        }
        return false;
    }
}
