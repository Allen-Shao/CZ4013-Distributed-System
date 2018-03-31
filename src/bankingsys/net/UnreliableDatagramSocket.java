package bankingsys.net;

import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by koallen on 1/4/18.
 */
public class UnreliableDatagramSocket extends DatagramSocket {
    public UnreliableDatagramSocket() throws SocketException {
    }
}
