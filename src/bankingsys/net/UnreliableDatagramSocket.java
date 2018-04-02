package bankingsys.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Random;

/**
 * Custom socket class for simulating random failures
 */
public class UnreliableDatagramSocket extends DatagramSocket {
    private float failureProbability = 0.5f;
    private Random randomGenerator = new Random();

    public UnreliableDatagramSocket() throws SocketException {
        super();
    }

    public UnreliableDatagramSocket(int port) throws SocketException {
        super(port);
    }

    @Override
    public void send(DatagramPacket packet) throws IOException {
        float failure = randomGenerator.nextFloat();
        if (failure > failureProbability) {
            super.send(packet);
        } else {
            throw new IOException("Simulated failure");
        }
    }
}
