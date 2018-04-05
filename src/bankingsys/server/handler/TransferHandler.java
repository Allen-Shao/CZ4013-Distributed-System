package bankingsys.server.handler;

import bankingsys.message.ServiceRequest;
import bankingsys.message.ServiceResponse;
import bankingsys.server.RequestReceiver;
import bankingsys.server.model.BankAccount;

import java.util.HashMap;

import static bankingsys.Constant.TRANSFER;
import static bankingsys.message.ServiceResponse.ResponseStatus.FAILURE;
import static bankingsys.message.ServiceResponse.ResponseStatus.SUCCESS;

/**
 * Handler for money transfer
 */
public class TransferHandler extends ServiceHandler {

    public TransferHandler(HashMap<Integer, BankAccount> accounts, RequestReceiver server) {
        super(accounts, server);
    }

    @Override
    public ServiceResponse handleRequest(ServiceRequest request, boolean simulation) {
        ServiceResponse response;
        if (authenticate(request)) {
            if (accounts.containsKey(request.getRequestAccount()) &&
                    accounts.containsKey(request.getRequestTargetAccount())) {
                BankAccount sourceAccount = accounts.get(request.getRequestAccount());
                BankAccount targetAccount = accounts.get(request.getRequestTargetAccount());
                if (sourceAccount.getCurrencyType() == targetAccount.getCurrencyType()) {
                    if (sourceAccount.getBalance() - request.getRequestAmount() >= 0.0f) {
                        sourceAccount.setBalance(sourceAccount.getBalance() - request.getRequestAmount());
                        targetAccount.setBalance(targetAccount.getBalance() + request.getRequestAmount());
                        response = new ServiceResponse(TRANSFER, SUCCESS, sourceAccount.getAccountNumber(),
                                "Transferred $" + Float.toString(request.getRequestAmount()) +
                                        " from account no." + Integer.toString(sourceAccount.getAccountNumber()) +
                                        " to account no." + Integer.toString(targetAccount.getAccountNumber()),
                                sourceAccount.getBalance());
                    } else {
                        response = new ServiceResponse(TRANSFER, FAILURE, null, "No enough balance.", null);
                    }
                    return response;
                } else {
                    response = new ServiceResponse(TRANSFER, FAILURE, null, "Target account currency type does not match.", null);
                    return response;
                }
            }
        }
        response = new ServiceResponse(TRANSFER, FAILURE, null, "Account does not exist.", null);
        return response;
    }
}
