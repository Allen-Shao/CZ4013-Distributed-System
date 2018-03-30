package bankingsys.server.handler;

import bankingsys.message.ServiceRequest;
import bankingsys.message.ServiceResponse;
import bankingsys.server.model.BankAccount;

import java.util.HashMap;

/**
 * Created by koallen on 29/3/18.
 */
public class AccountCreationHandler extends ServiceHandler {
    public AccountCreationHandler(HashMap<Integer, BankAccount> accounts) {
        super(accounts);
    }

    @Override
    public ServiceResponse handleRequest(ServiceRequest request) {
        System.out.println("Called creation handler");
        return new ServiceResponse(200, 101, null, request.getRequestAmount());
    }
}
