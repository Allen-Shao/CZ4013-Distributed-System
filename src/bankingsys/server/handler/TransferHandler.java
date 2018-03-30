package bankingsys.server.handler;

import bankingsys.message.ServiceRequest;
import bankingsys.message.ServiceResponse;
import bankingsys.server.model.BankAccount;

import java.util.HashMap;

import static bankingsys.message.ServiceResponse.ResponseType.FAILURE;
import static bankingsys.message.ServiceResponse.ResponseType.SUCCESS;

/**
 * Created by koallen on 29/3/18.
 */
public class TransferHandler extends ServiceHandler {
    public TransferHandler(HashMap<Integer, BankAccount> accounts) {
        super(accounts);
    }

    @Override
    public ServiceResponse handleRequest(ServiceRequest request) {
        if (accounts.containsKey(request.getRequestAccount()) &&
                accounts.containsKey(request.getRequestTargetAccount())) {
            BankAccount sourceAccount = accounts.get(request.getRequestAccount());
            BankAccount targetAccount = accounts.get(request.getRequestTargetAccount());
            if (sourceAccount.getCurrencyType() == targetAccount.getCurrencyType()) {
                sourceAccount.setBalance(sourceAccount.getBalance() - request.getRequestAmount());
                targetAccount.setBalance(targetAccount.getBalance() + request.getRequestAccount());
                return new ServiceResponse(SUCCESS, sourceAccount.getAccountNumber(),
                        "Transfered $" + Float.toString(request.getRequestAmount()) +
                                " from account no." + Integer.toString(sourceAccount.getAccountNumber()) +
                                " to account no." + Integer.toString(targetAccount.getAccountNumber()),
                        sourceAccount.getBalance());
            } else {
                return new ServiceResponse(FAILURE, null, "Target account currency type does not match.", null);
            }
        }
        return new ServiceResponse(FAILURE, null, "Account does not exist.", null);
    }
}
