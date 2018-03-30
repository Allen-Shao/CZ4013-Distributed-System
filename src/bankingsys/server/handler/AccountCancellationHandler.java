package bankingsys.server.handler;

import bankingsys.message.ServiceRequest;
import bankingsys.message.ServiceResponse;
import bankingsys.server.model.BankAccount;

import java.util.HashMap;

import static bankingsys.message.ServiceResponse.ResponseStatus.FAILURE;
import static bankingsys.message.ServiceResponse.ResponseStatus.SUCCESS;

/**
 * Handler for account cancellation
 */
public class AccountCancellationHandler extends ServiceHandler {
    public AccountCancellationHandler(HashMap<Integer, BankAccount> accounts) {
        super(accounts);
    }

    @Override
    public ServiceResponse handleRequest(ServiceRequest request) {
        if (accounts.containsKey(request.getRequestAccount())) {
            accounts.remove(request.getRequestAccount());
            return new ServiceResponse(SUCCESS, null, "Account closed", null);
        }
        return new ServiceResponse(FAILURE, null, "Account doesn't exist", null);
    }
}
