package bankingsys.server;
import bankingsys.io.Deserializer;
import bankingsys.io.Serializer;
import bankingsys.message.ServiceRequest;
import bankingsys.message.ServiceResponse;
import bankingsys.server.handler.*;
import bankingsys.server.model.BankAccount;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by koallen on 29/3/18.
 */
public class RequestReceiver {

    private static final int SERVER_PORT = 6789;
    private static final int BUFFER_SIZE = 1024;
    private static final int MONITOR_CLIENT_PORT = 8888;

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
        accountDatabase.put(0, randomAccount(0));
        accountDatabase.put(1, randomAccount(1));
        accountDatabase.put(2, randomAccount(2));
        accountDatabase.put(3, randomAccount(3));

        int databaseSize = accountDatabase.size();

        HashMap <Character, ServiceHandler> handlerMap = new HashMap<Character, ServiceHandler>();
        handlerMap.put('a', new AccountCancellationHandler(accountDatabase));
        handlerMap.put('b', new AccountCreationHandler(accountDatabase));
        handlerMap.put('c', new AccountMonitoringHandler(accountDatabase));
        handlerMap.put('d', new BalanceCheckHandler(accountDatabase));
        handlerMap.put('e', new BalanceUpdateHandler(accountDatabase));
        handlerMap.put('f', new TransferHandler(accountDatabase));


        DatagramSocket socket = null;
        try {

            socket = new DatagramSocket(SERVER_PORT);
            Deserializer deserializer;
            Serializer serializer;

            //a buffer for receive
            byte[] receiveBuffer = new byte[BUFFER_SIZE];
            while (true) {
                DatagramPacket requestPacket =
                        new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(requestPacket);
                deserializer = new Deserializer(receiveBuffer);
                ServiceRequest serviceRequest = new ServiceRequest();
                serviceRequest.read(deserializer);

                Character op = serviceRequest.getRequestType();
                ServiceResponse response =  handlerMap.get(op).handleRequest(serviceRequest);
                serializer = new Serializer();
                response.write(serializer);
                DatagramPacket responsePacket =
                        new DatagramPacket(serializer.getBuffer(), serializer.getBufferLength(),
                                requestPacket.getAddress(), requestPacket.getPort());
                socket.send(responsePacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

    private static BankAccount randomAccount(int accountNumber){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random=new Random();
        StringBuffer randomName=new StringBuffer();
        for(int i=0;i<(5+(int)random.nextInt(5));i++){
            int number=random.nextInt(52);
            randomName.append(str.charAt(number));
        }
        String name = randomName.toString();
        String password = "123456";
        BankAccount.Currency currency = BankAccount.Currency.SGD;
        float balance = (float) 0.0;
        return new BankAccount(accountNumber, name, password, currency, balance);
    }
}