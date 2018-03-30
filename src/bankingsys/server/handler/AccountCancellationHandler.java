package bankingsys.server.handler;

import bankingsys.message.ServiceRequest;
import bankingsys.message.ServiceResponse;
import bankingsys.server.RequestReceiver;
import bankingsys.server.model.BankAccount;

import java.util.HashMap;

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
    public void handleRequest(ServiceRequest request) {
        ServiceResponse response;
        if (accounts.containsKey(request.getRequestAccount())) {
            accounts.remove(request.getRequestAccount());
            response = new ServiceResponse('a', SUCCESS, 0, "Account closed", 0.0f);
            server.sendResponse(response, request.getRequestAddress(), request.getRequestPort());
            server.sendCallbacks(response);
            return;
        }
        response = new ServiceResponse('a', FAILURE, null, "Account doesn't exist", null);
        server.sendResponse(response, request.getRequestAddress(), request.getRequestPort());
    }
}
