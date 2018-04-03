package bankingsys.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Custom socket class for simulating random failures
 */
public class UnreliableDatagramSocket extends DatagramSocket {

    private static final Logger logger = Logger.getLogger(UnreliableDatagramSocket.class.getName());
    private float failureProbability = 0.5f;
    private Random randomGenerator = new Random();

    public UnreliableDatagramSocket() throws SocketException {
        super();
    }

    public UnreliableDatagramSocket(int port) throws SocketException {
        super(port);
    }

    public UnreliableDatagramSocket(int port, InetAddress address) throws SocketException {
        super(port, address);
    }

    @Override
    public void send(DatagramPacket packet) throws IOException {
        float failure = randomGenerator.nextFloat();
        if (failure > failureProbability) {
            super.send(packet);
        } else {
            logger.log(Level.WARNING, "Simulated failure");
            throw new IOException("Simulated failure");
        }
    }

    public void sendWithoutFailure(DatagramPacket packet) throws IOException {
        super.send(packet);
    }
}
