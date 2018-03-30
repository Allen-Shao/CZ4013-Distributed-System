package bankingsys.server.handler;

import bankingsys.message.ServiceRequest;
import bankingsys.message.ServiceResponse;
import bankingsys.server.RequestReceiver;
import bankingsys.server.model.BankAccount;

import java.util.HashMap;

import static bankingsys.message.ServiceResponse.ResponseStatus.SUCCESS;

/**
 * Handler for account creation
 */
public class AccountCreationHandler extends ServiceHandler {
    private int currentId = 0;

    public AccountCreationHandler(HashMap<Integer, BankAccount> accounts, RequestReceiver server) {
        super(accounts, server);
    }


    @Override
    public void handleRequest(ServiceRequest request) {
        ServiceResponse response;
        BankAccount newAccount = new BankAccount(currentId, request.getRequestName(),
                request.getRequestPassword(), request.getRequestCurrency(), request.getRequestAmount());
        accounts.put(newAccount.getAccountNumber(), newAccount);
        ++currentId;
        response = new ServiceResponse('b', SUCCESS, newAccount.getAccountNumber(),
                "Account No." + Integer.toString(newAccount.getAccountNumber()) + " belonging to " + newAccount.getName() +
                        " has been created with initial balance $" + Float.toString(newAccount.getBalance()),
                newAccount.getBalance());
        server.sendResponse(response, request.getRequestAddress(), request.getRequestPort());
        server.sendCallbacks(response);
    }
}
