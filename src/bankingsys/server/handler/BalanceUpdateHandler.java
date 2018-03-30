package bankingsys.server.handler;

import bankingsys.message.ServiceRequest;
import bankingsys.message.ServiceResponse;
import bankingsys.server.model.BankAccount;

import java.util.HashMap;

import static bankingsys.message.ServiceResponse.ResponseType.FAILURE;
import static bankingsys.message.ServiceResponse.ResponseType.SUCCESS;

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
                return new ServiceResponse(SUCCESS, account.getAccountNumber(), null, account.getBalance());
            }
            return new ServiceResponse(FAILURE, null, "Balance is not enough", null);
        }
        return new ServiceResponse(FAILURE, null, "Account doesn't exist", null);
    }
}
