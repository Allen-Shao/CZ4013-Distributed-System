package bankingsys.server.handler;

import bankingsys.message.ServiceRequest;
import bankingsys.message.ServiceResponse;
import bankingsys.server.model.BankAccount;

import java.util.HashMap;

/**
 * Abstract class to be inherited by all concrete service handlers
 */
public abstract class ServiceHandler {
    protected HashMap<Integer, BankAccount> accounts;

    public ServiceHandler(HashMap<Integer, BankAccount> accounts) {
        this.accounts = accounts;
    }
    public abstract ServiceResponse handleRequest(ServiceRequest request);

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
