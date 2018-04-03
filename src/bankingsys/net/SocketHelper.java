package bankingsys.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A helper class to implement at-most-once and at-least-once semantics
 */
public class SocketHelper {

    private static final Logger logger = Logger.getLogger(SocketHelper.class.getName());

    /**
     * Send the given package and return the received reply package
     * @param socket Socket used for sending
     * @param packetToSend Packet to be sent
     * @param packetToReceive Packet to be received
     */
    public static void sendReliably(DatagramSocket socket, DatagramPacket packetToSend, DatagramPacket packetToReceive) {
        Boolean done = false;
        while (!done) {
            try{
                socket.send(packetToSend);
                socket.receive(packetToReceive);
            } catch (SocketTimeoutException e) {
                logger.log(Level.SEVERE, "Timeout on receive.");
                continue;
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Send or receive error on sending request.");
                continue;
            }
            done = true;
        }
    }

    public static void receiveAtMostOnce(DatagramSocket socket, DatagramPacket packetToReceive) {

    }

    public static void receiveAtLeastOnce() {

    }
}
