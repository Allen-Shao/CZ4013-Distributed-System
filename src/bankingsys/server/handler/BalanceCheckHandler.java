package bankingsys.server.handler;

import bankingsys.message.ServiceRequest;
import bankingsys.message.ServiceResponse;
import bankingsys.server.model.BankAccount;

import java.util.HashMap;

import static bankingsys.message.ServiceResponse.ResponseStatus.FAILURE;
import static bankingsys.message.ServiceResponse.ResponseStatus.SUCCESS;

/**
 * Handler for checking account balance
 */
public class BalanceCheckHandler extends ServiceHandler {
    public BalanceCheckHandler(HashMap<Integer, BankAccount> accounts) {
        super(accounts);
    }

    @Override
    public ServiceResponse handleRequest(ServiceRequest request) {
        if (authenticate(request)) {
            if (accounts.containsKey(request.getRequestAccount())) {
                BankAccount account = accounts.get(request.getRequestAccount());
                return new ServiceResponse(SUCCESS, account.getAccountNumber(),
                        "Account No." + Integer.toString(account.getAccountNumber()) + " belonging to " + account.getName() +
                        " has a balance of $" + Float.toString(account.getBalance()),
                        account.getBalance());
            }
        }
        return new ServiceResponse(FAILURE, null, "Account doesn't exist", null);
    }
}
