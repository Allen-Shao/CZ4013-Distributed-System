package bankingsys.server;
import bankingsys.io.Deserializer;
import bankingsys.io.Serializer;
import bankingsys.message.ServiceRequest;
import bankingsys.message.ServiceResponse;
import bankingsys.server.handler.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;

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
        boolean atMostOnce = args.length > 0 && args[0].equals("1");
        System.out.println("at most once: " + atMostOnce);

        // Second argument indicates the mode of datagram socket.
        // "1": reply packets from server will lost for 3 times
        // "2": request packets from client will lost for 3 times
        // "": normal datagramsocket.
        String unstable = args.length > 1 ? args[1] : "";
        System.out.println("unstable datagram: " + unstable);

        HashMap <Character, ServiceHandler> handlerMap = new HashMap<Character, ServiceHandler>();
        handlerMap.put('a', new AccountCancellationHandler());
        handlerMap.put('b', new AccountCreationHandler());
        handlerMap.put('c', new AccountMonitoringHandler());
        handlerMap.put('d', new BalanceCheckHandler());
        handlerMap.put('e', new BalanceUpdateHandler());
        handlerMap.put('f', new TransferHandler());

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
}