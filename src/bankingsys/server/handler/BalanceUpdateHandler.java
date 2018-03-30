package bankingsys.server.handler;

import bankingsys.message.ServiceRequest;
import bankingsys.message.ServiceResponse;
import bankingsys.server.model.BankAccount;

import java.util.HashMap;

import static bankingsys.message.ServiceResponse.ResponseStatus.FAILURE;
import static bankingsys.message.ServiceResponse.ResponseStatus.SUCCESS;

/**
 * Handler for updating account balance (deposit / withdraw)
 */
public class BalanceUpdateHandler extends ServiceHandler {
    public BalanceUpdateHandler(HashMap<Integer, BankAccount> accounts) {
        super(accounts);
    }

    @Override
    public ServiceResponse handleRequest(ServiceRequest request) {
        if (accounts.containsKey(request.getRequestAccount())) {
            BankAccount account = accounts.get(request.getRequestAccount());
            float currentBalance = account.getBalance();
            if (currentBalance + request.getRequestAmount() >= 0) {
                account.setBalance(account.getBalance() + request.getRequestAmount());
                return new ServiceResponse('e', SUCCESS, account.getAccountNumber(),
                        "Account No." + Integer.toString(account.getAccountNumber()) + " belonging to " + account.getName() +
                                " has a new balance of $" + Float.toString(account.getBalance()),
                        account.getBalance());
            }
            return new ServiceResponse('e', FAILURE, null, "Balance is not enough", null);
        }
        return new ServiceResponse('e', FAILURE, null, "Account doesn't exist", null);
    }
}
