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
    public void handleRequest(ServiceRequest request) {
        ServiceResponse response;
        if (accounts.containsKey(request.getRequestAccount()) &&
                accounts.containsKey(request.getRequestTargetAccount())) {
            BankAccount sourceAccount = accounts.get(request.getRequestAccount());
            BankAccount targetAccount = accounts.get(request.getRequestTargetAccount());
            if (sourceAccount.getCurrencyType() == targetAccount.getCurrencyType()) {
                if (sourceAccount.getBalance() >= request.getRequestAccount()) {
                    sourceAccount.setBalance(sourceAccount.getBalance() - request.getRequestAmount());
                    targetAccount.setBalance(targetAccount.getBalance() + request.getRequestAmount());
                    response = new ServiceResponse(TRANSFER, SUCCESS, sourceAccount.getAccountNumber(),
                            "Transferred $" + Float.toString(request.getRequestAmount()) +
                                    " from account no." + Integer.toString(sourceAccount.getAccountNumber()) +
                                    " to account no." + Integer.toString(targetAccount.getAccountNumber()),
                            sourceAccount.getBalance());
                    server.sendResponse(response, request.getRequestAddress(), request.getRequestPort());
                    server.sendCallbacks(response);
                } else {response = new ServiceResponse(TRANSFER, FAILURE, null, "No enough balance.", null);
                    server.sendResponse(response, request.getRequestAddress(), request.getRequestPort());
                }

            } else {
                response = new ServiceResponse(TRANSFER, FAILURE, null, "Target account currency type does not match.", null);
                server.sendResponse(response, request.getRequestAddress(), request.getRequestPort());
            }
            return;
        }
        response = new ServiceResponse(TRANSFER, FAILURE, null, "Account does not exist.", null);
        server.sendResponse(response, request.getRequestAddress(), request.getRequestPort());
    }
}
