package bankingsys.server.handler;

import bankingsys.message.ServiceRequest;
import bankingsys.message.ServiceResponse;
import bankingsys.server.RequestReceiver;
import bankingsys.server.model.BankAccount;

import java.util.HashMap;

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
    public void handleRequest(ServiceRequest request) {
        ServiceResponse response;
        if (accounts.containsKey(request.getRequestAccount())) {
            BankAccount account = accounts.get(request.getRequestAccount());
            float currentBalance = account.getBalance();
            if (currentBalance + request.getRequestAmount() >= 0) {
                account.setBalance(account.getBalance() + request.getRequestAmount());
                response = new ServiceResponse('e', SUCCESS, account.getAccountNumber(),
                        "Account No." + Integer.toString(account.getAccountNumber()) + " belonging to " + account.getName() +
                                " has a new balance of $" + Float.toString(account.getBalance()),
                        account.getBalance());
                server.sendResponse(response, request.getRequestAddress(), request.getRequestPort());
                server.sendCallbacks(response);
                return;
            }
            response = new ServiceResponse('e', FAILURE, null, "Balance is not enough", null);
            server.sendResponse(response, request.getRequestAddress(), request.getRequestPort());
            return;
        }
        response = new ServiceResponse('e', FAILURE, null, "Account doesn't exist", null);
        server.sendResponse(response, request.getRequestAddress(), request.getRequestPort());
    }
}
