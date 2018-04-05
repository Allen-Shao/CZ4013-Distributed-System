package bankingsys.server.handler;

import bankingsys.message.ServiceRequest;
import bankingsys.message.ServiceResponse;
import bankingsys.server.RequestReceiver;
import bankingsys.server.model.BankAccount;

import java.util.HashMap;

import static bankingsys.Constant.*;
import static bankingsys.message.ServiceResponse.ResponseStatus.FAILURE;
import static bankingsys.message.ServiceResponse.ResponseStatus.SUCCESS;

/**
 * Handler for account cancellation
 */
public class AccountCancellationHandler extends ServiceHandler {

    public AccountCancellationHandler(HashMap<Integer, BankAccount> accounts, RequestReceiver server) {
        super(accounts, server);
    }

    @Override
    public ServiceResponse handleRequest(ServiceRequest request, boolean simulation) {
        ServiceResponse response;
        if (authenticate(request)) {
            if (accounts.containsKey(request.getRequestAccount())) {
                accounts.remove(request.getRequestAccount());
                response = new ServiceResponse(ACCOUNT_CREATE, SUCCESS, null, "Account closed", null);
                return response;
            }
        }
        response = new ServiceResponse(ACCOUNT_CREATE, FAILURE, null, "Account doesn't exist", null);
        return response;
    }
}
