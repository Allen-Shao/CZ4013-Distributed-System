package bankingsys.server.handler;

import bankingsys.message.ServiceRequest;
import bankingsys.message.ServiceResponse;
import bankingsys.server.RequestReceiver;
import bankingsys.server.model.BankAccount;

import java.util.HashMap;

import static bankingsys.Constant.BALANCE_UPDATE;
import static bankingsys.message.ServiceResponse.ResponseStatus.FAILURE;
import static bankingsys.message.ServiceResponse.ResponseStatus.SUCCESS;

/**
 * Handler for updating account balance (deposit / withdraw)
 */
public class BalanceUpdateHandler extends ServiceHandler {

    public BalanceUpdateHandler(HashMap<Integer, BankAccount> accounts, RequestReceiver server) {
        super(accounts, server);
    }

    @Override
    public ServiceResponse handleRequest(ServiceRequest request, boolean simulation) {
        ServiceResponse response;
        if (accounts.containsKey(request.getRequestAccount())) {
            BankAccount account = accounts.get(request.getRequestAccount());
            float currentBalance = account.getBalance();
            if (currentBalance + request.getRequestAmount() >= 0) {
                account.setBalance(account.getBalance() + request.getRequestAmount());
                response = new ServiceResponse(BALANCE_UPDATE, SUCCESS, account.getAccountNumber(),
                        "Account No." + Integer.toString(account.getAccountNumber()) + " belonging to " + account.getName() +
                                " has a new balance of $" + Float.toString(account.getBalance()),
                        account.getBalance());
                return response;
            }
            response = new ServiceResponse(BALANCE_UPDATE, FAILURE, null, "Balance is not enough", null);
            return response;
        }
        response = new ServiceResponse(BALANCE_UPDATE, FAILURE, null, "Account doesn't exist", null);
        return response;
    }
}
