package bankingsys.server.handler;

import bankingsys.message.ServiceRequest;
import bankingsys.message.ServiceResponse;
import bankingsys.server.model.BankAccount;

import java.util.HashMap;

import static bankingsys.message.ServiceResponse.ResponseType.FAILURE;
import static bankingsys.message.ServiceResponse.ResponseType.SUCCESS;

/**
 * Handler for account creation
 */
public class AccountCreationHandler extends ServiceHandler {
    private int currentId = 0;

    public AccountCreationHandler(HashMap<Integer, BankAccount> accounts) {
        super(accounts);
    }

    @Override
    public ServiceResponse handleRequest(ServiceRequest request) {
        BankAccount newAccount = new BankAccount(currentId, request.getRequestName(),
                request.getRequestPassword(), request.getRequestCurrency(), request.getRequestAmount());
        accounts.put(newAccount.getAccountNumber(), newAccount);
        ++currentId;
        return new ServiceResponse(SUCCESS, newAccount.getAccountNumber(), null, newAccount.getBalance());
    }
}
