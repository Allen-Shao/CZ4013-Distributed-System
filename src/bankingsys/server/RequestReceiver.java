package bankingsys.server;
import bankingsys.io.Deserializer;
import bankingsys.io.Serializer;
import bankingsys.message.ServiceRequest;
import bankingsys.message.ServiceResponse;
import bankingsys.server.handler.*;
import bankingsys.server.model.BankAccount;
import bankingsys.server.model.Client;
import bankingsys.server.model.MonitoringClients;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.*;

import static bankingsys.Constant.BUFFER_SIZE;
import static bankingsys.Constant.SERVER_PORT;
import static bankingsys.message.ServiceResponse.ResponseStatus.SUCCESS;

/**
 * Main class that implements the server
 */
public class RequestReceiver {

    private HashMap <Integer, BankAccount> accountDatabase = new HashMap<>();
    private int databaseSize = accountDatabase.size();
    private MonitoringClients clients = new MonitoringClients();
    private HashMap<Client, HashMap<Integer, ServiceResponse>> clientsLog = new HashMap<>();
    private DatagramSocket socket = null;
    private Deserializer deserializer = null;
    private Serializer serializer = null;
    private byte[] receiveBuffer = new byte[BUFFER_SIZE];

    private static final Logger log = Logger.getLogger(RequestReceiver.class.getName());
    private static Options options = new Options();

    public static void main(String[] args) {
        //Arguments Handle
        Boolean atMostOnce = false;

        options.addOption("h", "help", false, "Show help.");
        options.addOption("m", "mode", true, "Set mode to 'at-least-once' or 'at-most-once'.");
        CommandLineParser parser = new DefaultParser();

        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption("h"))
                help();

            if (cmd.hasOption("mode")) {
                log.log(Level.INFO, "Using cli argument -mode=" + cmd.getOptionValue("mode"));
                // Whatever you want to do with the setting goes here
                if (cmd.getOptionValue("mode").equals("at-most-once"))
                    atMostOnce = true;
            } else {
                log.log(Level.SEVERE, "Missing mode option");
                help();
            }

        } catch (ParseException e) {
            log.log(Level.SEVERE, "Failed to parse command line properties", e);
            help();
        }

        System.out.println(atMostOnce);

        new RequestReceiver().run(atMostOnce);
    }

    public void run(boolean atMostOnce) {
        HashMap<Character, ServiceHandler> handlerMap = new HashMap<>();
        handlerMap.put('a', new AccountCancellationHandler(accountDatabase));
        handlerMap.put('b', new AccountCreationHandler(accountDatabase));
        handlerMap.put('c', new AccountMonitoringHandler(accountDatabase, clients));
        handlerMap.put('d', new BalanceCheckHandler(accountDatabase));
        handlerMap.put('e', new BalanceUpdateHandler(accountDatabase));
        handlerMap.put('f', new TransferHandler(accountDatabase));


        try {
            socket = new DatagramSocket(SERVER_PORT);
            System.out.println("Start listening");
            while (true) {
                // receive request and parse message
                DatagramPacket requestPacket =
                        new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(requestPacket);
                serializer = new Serializer();

                deserializer = new Deserializer(receiveBuffer);
                ServiceRequest serviceRequest = new ServiceRequest();
                serviceRequest.read(deserializer);
                Character op = serviceRequest.getRequestType();
                ServiceResponse response;
                // Check client in log. If not exist, register and log a new client
                Client tempClient = new Client(requestPacket.getAddress(),requestPacket.getPort());
                Boolean registered = checkClient(tempClient);

                if (atMostOnce && registered && checkRequestHistory(clientsLog.get(tempClient), serviceRequest.getRequestID())) {
                    response = clientsLog.get(tempClient).get(serviceRequest.getRequestID());
                } else {
                    serviceRequest.setRequestAddress(requestPacket.getAddress());
                    serviceRequest.setRequestPort(requestPacket.getPort());
                    // handle the request
                    response = handlerMap.get(op).handleRequest(serviceRequest);
                }

                sendResponse(response, requestPacket.getAddress(), requestPacket.getPort());

                // send callbacks
                if (op != 'c')
                    sendCallbacks(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

    private void sendResponse(ServiceResponse response, InetAddress address, int port) {
        // send response
        response.write(serializer);
        DatagramPacket responsePacket =
                new DatagramPacket(serializer.getBuffer(), serializer.getBufferLength(),
                        address, port);
        try {
            socket.send(responsePacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Boolean checkRequestHistory(HashMap<Integer, ServiceResponse> history, Integer id) {
        if (history.containsKey(id)) {
            System.out.println("Request already handled.");
            return true;
        } else {
            System.out.println("New request.");
            return false;
        }
    }

    private Boolean checkClient(Client client) {
        if (clientsLog.containsKey(client)) {
            System.out.println("Client exists.");
            return true;
        } else {
            clientsLog.put(client,new HashMap<Integer, ServiceResponse>());
            System.out.println("Client registered.");
            return false;
        }
    }

    private void sendCallbacks(ServiceResponse response) {
        System.out.println("Sending callbacks");
        if (response.getResponseCode() == SUCCESS) {
            for (Client client : clients.getClients()) {
                DatagramPacket callbackPacket =
                        new DatagramPacket(serializer.getBuffer(), serializer.getBufferLength(),
                                client.getClientAddress(), client.getClientPort());
                try {
                    socket.send(callbackPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void help() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Main", options);
        System.exit(0);
    }
}