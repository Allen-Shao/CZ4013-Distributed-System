package bankingsys.server.handler;

import bankingsys.message.ServiceRequest;
import bankingsys.message.ServiceResponse;
import bankingsys.server.RequestReceiver;
import bankingsys.server.model.BankAccount;

import java.util.HashMap;

import static bankingsys.Constant.ACCOUNT_CANCEL;
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
    public void handleRequest(ServiceRequest request, boolean simulation) {
        ServiceResponse response;
        BankAccount newAccount = new BankAccount(currentId, request.getRequestName(),
                request.getRequestPassword(), request.getRequestCurrency(), request.getRequestAmount());
        accounts.put(newAccount.getAccountNumber(), newAccount);
        ++currentId;
        response = new ServiceResponse(ACCOUNT_CANCEL, SUCCESS, newAccount.getAccountNumber(),
                "Account No." + Integer.toString(newAccount.getAccountNumber()) + " belonging to " + newAccount.getName() +
                        " has been created with initial balance $" + Float.toString(newAccount.getBalance()),
                newAccount.getBalance());
        server.sendResponse(response, request.getRequestAddress(), request.getRequestPort(), simulation);
        server.sendCallbacks(response);
    }
}
