package bankingsys.server.handler;

import bankingsys.message.ServiceRequest;
import bankingsys.message.ServiceResponse;
import bankingsys.server.RequestReceiver;
import bankingsys.server.model.BankAccount;

import java.util.HashMap;

import static bankingsys.Constant.BALANCE_CHECK;
import static bankingsys.message.ServiceResponse.ResponseStatus.FAILURE;
import static bankingsys.message.ServiceResponse.ResponseStatus.SUCCESS;

/**
 * Handler for checking account balance
 */
public class BalanceCheckHandler extends ServiceHandler {

    public BalanceCheckHandler(HashMap<Integer, BankAccount> accounts, RequestReceiver server) {
        super(accounts, server);
    }

    @Override
    public ServiceResponse handleRequest(ServiceRequest request, boolean simulation) {
        ServiceResponse response;
        if (authenticate(request)) {
            if (accounts.containsKey(request.getRequestAccount())) {
                BankAccount account = accounts.get(request.getRequestAccount());
                response = new ServiceResponse(BALANCE_CHECK, SUCCESS, account.getAccountNumber(),
                        "Account No." + Integer.toString(account.getAccountNumber()) + " belonging to " + account.getName() +
                        " has a balance of $" + Float.toString(account.getBalance()),
                        account.getBalance());
                return response;
            }
        }
        response = new ServiceResponse(BALANCE_CHECK, FAILURE, null, "Account doesn't exist", null);
        return response;
    }
}
