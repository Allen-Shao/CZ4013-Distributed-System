package bankingsys.server.handler;

import bankingsys.message.ServiceRequest;
import bankingsys.message.ServiceResponse;
import bankingsys.server.RequestReceiver;
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

    public AccountMonitoringHandler(HashMap<Integer, BankAccount> accounts, RequestReceiver server, MonitoringClients clients) {
        super(accounts, server);
        this.clients = clients;
    }

    @Override
    public void handleRequest(ServiceRequest request) {
        ServiceResponse response;
        System.out.println("AccountMonitoringHandler called");
        Client client = new Client(request.getRequestAddress(), request.getRequestPort());
        if (!clients.isClientInSet(client)) {
            clients.addClient(client);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    clients.removeFromClients(client);
                    //ServiceResponse terminateResponse = new ServiceResponse('k',
                    //        SUCCESS, null, "Monitoring terminated", null);
                    //server.sendResponse(terminateResponse, request.getRequestAddress(), request.getRequestPort());
                    System.out.println("Client removed");
                }
            }, request.getRequestDelay() * 1000);
            response = new ServiceResponse('c', SUCCESS, null, "Monitoring callback registered", null);
            server.sendResponse(response, request.getRequestAddress(), request.getRequestPort());
            return;
        }
        response = new ServiceResponse('c', FAILURE, null, "Monitoring callback already registered", null);
        server.sendResponse(response, request.getRequestAddress(), request.getRequestPort());
    }
}
