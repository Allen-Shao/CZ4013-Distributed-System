package bankingsys.server.handler;

import bankingsys.message.ServiceRequest;
import bankingsys.message.ServiceResponse;
import bankingsys.server.model.BankAccount;
import bankingsys.server.model.Client;
import bankingsys.server.model.MonitoringClients;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import static bankingsys.message.ServiceResponse.ResponseStatus.FAILURE;
import static bankingsys.message.ServiceResponse.ResponseStatus.SUCCESS;

/**
 * Handler for registering monitoring clients
 */
public class AccountMonitoringHandler extends ServiceHandler {
    private MonitoringClients clients;

    public AccountMonitoringHandler(HashMap<Integer, BankAccount> accounts, MonitoringClients clients) {
        super(accounts);
        this.clients = clients;
    }

    @Override
    public ServiceResponse handleRequest(ServiceRequest request) {
        System.out.println("AccountMonitoringHandler called");
        Client client = new Client(request.getRequestAddress(), request.getRequestPort());
        if (!clients.isClientInSet(client)) {
            clients.addClient(client);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    clients.removeFromClients(client);
                    // TODO: send message back to client
                }
            }, request.getRequestDelay() * 1000);
            return new ServiceResponse(SUCCESS, 0, "Monitoring callback registered", 0.0f);
        }
        return new ServiceResponse(FAILURE, null, "Monitoring callback already registered", null);
    }
}
