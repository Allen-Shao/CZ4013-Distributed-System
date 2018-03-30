package bankingsys.server;
import bankingsys.io.Deserializer;
import bankingsys.io.Serializer;
import bankingsys.message.ServiceRequest;
import bankingsys.message.ServiceResponse;
import bankingsys.server.handler.*;
import bankingsys.server.model.BankAccount;
import bankingsys.server.model.Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import static bankingsys.Constant.BUFFER_SIZE;
import static bankingsys.Constant.SERVER_PORT;
import static bankingsys.message.ServiceResponse.ResponseType.SUCCESS;

/**
 * Main class that implements the server
 */
public class RequestReceiver {

    public static void main(String[] args) {
        // Default use at-least-once invocation semantic
        // If the first argument is "1", use at-most-once invocation semantic
        // boolean atMostOnce = args.length > 0 && args[0].equals("1");
        // System.out.println("at most once: " + atMostOnce);

        // Second argument indicates the mode of datagram socket.
        // "1": reply packets from server will lost for 3 times
        // "2": request packets from client will lost for 3 times
        // "": normal datagramsocket.
        // String unstable = args.length > 1 ? args[1] : "";
        // System.out.println("unstable datagram: " + unstable);


        HashMap <Integer, BankAccount> accountDatabase = new HashMap<>();

        int databaseSize = accountDatabase.size();

        HashSet<Client> clients = new HashSet<>();

        HashMap <Character, ServiceHandler> handlerMap = new HashMap<>();
        handlerMap.put('a', new AccountCancellationHandler(accountDatabase));
        handlerMap.put('b', new AccountCreationHandler(accountDatabase));
        handlerMap.put('c', new AccountMonitoringHandler(accountDatabase, clients));
        handlerMap.put('d', new BalanceCheckHandler(accountDatabase));
        handlerMap.put('e', new BalanceUpdateHandler(accountDatabase));
        handlerMap.put('f', new TransferHandler(accountDatabase));

        HashMap<Integer, ServiceResponse> clientHistory = new HashMap<>();
        HashMap<Client, HashMap<Integer, ServiceResponse>> clientsLog = new HashMap<>();

        DatagramSocket socket = null;
        Deserializer deserializer = null;
        Serializer serializer = null;
        try {
            byte[] receiveBuffer = new byte[BUFFER_SIZE];
            socket = new DatagramSocket(SERVER_PORT);

            System.out.println("Start listening");
            while (true) {
                // receive request and parse message
                DatagramPacket requestPacket =
                        new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(requestPacket);

                // Check client in log. If not exist, register and log a new client
                Client tempClient = new Client(requestPacket.getAddress(),requestPacket.getPort());
                if (CheckClient(tempClient, clientsLog)) {
                    if (CheckRequestHistory(clientsLog.get(tempClient), 0)) {

                    }
                }

                deserializer = new Deserializer(receiveBuffer);
                ServiceRequest serviceRequest = new ServiceRequest();
                serviceRequest.read(deserializer);
                serviceRequest.setRequestAddress(requestPacket.getAddress());
                serviceRequest.setRequestPort(requestPacket.getPort());

                // handle the request
                Character op = serviceRequest.getRequestType();
                ServiceResponse response =  handlerMap.get(op).handleRequest(serviceRequest);

                // send response
                serializer = new Serializer();
                response.write(serializer);
                DatagramPacket responsePacket =
                        new DatagramPacket(serializer.getBuffer(), serializer.getBufferLength(),
                                requestPacket.getAddress(), requestPacket.getPort());
                socket.send(responsePacket);

                // send callbacks
                if (op != 'c')
                    sendCallbacks(socket, clients, response, serializer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

    private static Boolean CheckRequestHistory(HashMap<Integer, ServiceResponse> history, Integer id) {
        if (history.containsKey(id)) {
            System.out.println("Request already handled.");
            return true;
        } else {
            System.out.println("New request.");
            return false;
        }
    }

    private static Boolean CheckClient(Client client, HashMap<Client, HashMap<Integer, ServiceResponse>> log) {
        if (log.containsKey(client)) {
            System.out.println("Client exists.");
            return true;
        } else {
            log.put(client,new HashMap<Integer, ServiceResponse>());
            System.out.println("Client registered.");
            return false;
        }
    }

    private static void sendCallbacks(DatagramSocket socket, HashSet<Client> clients, ServiceResponse response, Serializer serializer) {
        System.out.println("Sending callbacks");
        if (response.getResponseCode() == SUCCESS) {
            for (Client client : clients) {
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
}