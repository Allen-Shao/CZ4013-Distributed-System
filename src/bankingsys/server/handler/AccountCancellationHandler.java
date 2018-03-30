package bankingsys.server.handler;

import bankingsys.message.ServiceRequest;
import bankingsys.message.ServiceResponse;
import bankingsys.server.model.BankAccount;

import java.util.HashMap;

/**
 * Created by koallen on 29/3/18.
 */
public class AccountCancellationHandler extends ServiceHandler {
    public AccountCancellationHandler(HashMap<Integer, BankAccount> accounts) {
        super(accounts);
    }

    @Override
    public ServiceResponse handleRequest(ServiceRequest request) {
        return null;
    }
}
