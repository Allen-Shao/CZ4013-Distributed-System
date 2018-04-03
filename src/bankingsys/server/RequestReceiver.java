package bankingsys.server;
import bankingsys.io.Deserializer;
import bankingsys.io.Serializer;
import bankingsys.message.ServiceRequest;
import bankingsys.message.ServiceResponse;
import bankingsys.net.UnreliableDatagramSocket;
import bankingsys.server.handler.*;
import bankingsys.server.model.BankAccount;
import bankingsys.server.model.Client;
import bankingsys.server.model.MonitoringClients;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.*;

import static bankingsys.Constant.BUFFER_SIZE;
import static bankingsys.Constant.SERVER_PORT;
import static bankingsys.Constant.TIMEOUT;
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

    private static final Logger logger = Logger.getLogger(RequestReceiver.class.getName());
    private static Options options = new Options();

    private static Boolean simulation = false;

    public static void main(String[] args) {
        //Arguments Handle
        Boolean atMostOnce = false;

        options.addOption("h", "help", false, "Show help.");
        options.addOption("m", "mode", true, "Set mode to 'at-least-once' or 'at-most-once'.");
        options.addOption("sim", "simulation", false, "Set mode to 'simulation' with error rate.");
        CommandLineParser parser = new DefaultParser();

        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption("h"))
                help();

            if (cmd.hasOption("sim")) {
                logger.log(Level.INFO, "Using cli argument -sim=" + cmd.getOptionValue("sim"));
                simulation = true;
            }

            if (cmd.hasOption("mode")) {
                logger.log(Level.INFO, "Using cli argument -mode=" + cmd.getOptionValue("mode"));
                // Whatever you want to do with the setting goes here
                if (cmd.getOptionValue("mode").equals("at-most-once"))
                    atMostOnce = true;
            } else {
                logger.log(Level.SEVERE, "Missing mode option");
                help();
            }

        } catch (ParseException e) {
            logger.log(Level.SEVERE, "Failed to parse command line properties", e);
            help();
        }

        new RequestReceiver().run(atMostOnce);
    }

    private void run(boolean atMostOnce) {
        HashMap<Character, ServiceHandler> handlerMap = new HashMap<>();
        handlerMap.put('a', new AccountCancellationHandler(accountDatabase, this));
        handlerMap.put('b', new AccountCreationHandler(accountDatabase, this));
        handlerMap.put('c', new AccountMonitoringHandler(accountDatabase, this, clients));
        handlerMap.put('d', new BalanceCheckHandler(accountDatabase, this));
        handlerMap.put('e', new BalanceUpdateHandler(accountDatabase, this));
        handlerMap.put('f', new TransferHandler(accountDatabase, this));


        try {
            if (simulation) {
                socket = new UnreliableDatagramSocket(SERVER_PORT);
            } else {
                socket = new DatagramSocket(SERVER_PORT);
            }
            logger.log(Level.INFO, "Start listening");
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
                // Check client in logger. If not exist, register and logger a new client
                Client tempClient = new Client(requestPacket.getAddress(),requestPacket.getPort());
                Boolean registered = checkClient(tempClient);

                if (atMostOnce && registered && checkRequestHistory(clientsLog.get(tempClient), serviceRequest.getRequestID())) {
                    response = clientsLog.get(tempClient).get(serviceRequest.getRequestID());
                    sendResponse(response, requestPacket.getAddress(), requestPacket.getPort(), simulation);
                    //if (op != 'c')
                    //    sendCallbacks(response);
                } else {
                    serviceRequest.setRequestAddress(requestPacket.getAddress());
                    serviceRequest.setRequestPort(requestPacket.getPort());
                    // handle the request
                    response = handlerMap.get(op).handleRequest(serviceRequest, simulation);
                    clientsLog.get(tempClient).put(serviceRequest.getRequestID(), response);
                    sendResponse(response, requestPacket.getAddress(), requestPacket.getPort(), simulation);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

    private void sendResponse(ServiceResponse response, InetAddress address, int port, boolean simulation) {
        // send response
        serializer = new Serializer();
        response.write(serializer);
        DatagramPacket responsePacket =
                new DatagramPacket(serializer.getBuffer(), serializer.getBufferLength(),
                        address, port);
        try {
            socket.send(responsePacket);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "failure on sending reply");
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
            clientsLog.put(client,new HashMap<>());
            System.out.println("Client registered.");
            return false;
        }
    }

    public void sendCallbacks(ServiceResponse response) {
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