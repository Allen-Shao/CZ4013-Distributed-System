package bankingsys.server;
import bankingsys.io;

/**
 * Created by koallen on 29/3/18.
 */
public class RequestReceiver {

    private static final int SERVER_PORT = 6789;
    public static final int BUFFER_SIZE = 1024;
    private static final int MONITOR_CLIENT_PORT = 8888;

    public static void main(String[] args) {
        // Default use at-least-once invocation semantic
        // If the first argument is "1", use at-most-once invocation semantic
        boolean atMostOnce = args.length > 0 && args[0].equals("1");
        log.info("at most once: " + atMostOnce);

        // Second argument indicates the mode of datagram socket.
        // "1": reply packets from server will lost for 3 times
        // "2": request packets from client will lost for 3 times
        // "": normal datagramsocket.
        String unstable = args.length > 1 ? args[1] : "";
        log.info("unstable datagram: " + unstable);

        DatagramSocket socket = null;
        try {
            //bound to host and port
//            if (unstable.equals("1")) {
//                // The server always receives the request packet
//                // But the first three reply packet will be droped.
//                socket = new UnstableDatagramSocket(SERVER_PORT, "1111111111", "0001111111");
//            } else if (unstable.equals("2")) {
//                socket = new UnstableDatagramSocket(SERVER_PORT, "0001111111", "1111111111");
//            } else {
//                socket = new DatagramSocket(SERVER_PORT);
//            }
            socket = new DatagramSocket(SERVER_PORT);
            Deserializer deserializer = new Deserializer();
            Serializer serializer = new Serializer();

            //a buffer for receive
            byte[] receiveBuffer = new byte[BUFFER_SIZE];
            while (true) {
                DatagramPacket requestPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(requestPacket);

                System.out.printf("Request: %s\n", new String(requestPacket.getData()));

                NekoData request = deserializer.deserialize(requestPacket.getData());

                NekoData respond;

                String requestId = request.getRequestId();
                if (atMostOnce && history.containsKey(requestId)) {
                    respond = history.get(requestId);
                } else {
                    switch (request.getOpcode()) {
                        case READ:
                            if (request.getOffset() == null && request.getLength() == null) {
                                respond = handleRead(request.getPath());
                            } else {
                                respond = handleRead(request.getPath(),
                                        request.getOffset(),
                                        request.getLength());
                            }
                            break;
                        case INSERT:
                            respond = handleInsert(request.getPath(),
                                    request.getOffset(),
                                    request.getText());
                            break;
                        case MONITOR:
                            respond = handleMonitor(requestPacket.getAddress(),
                                    request.getPath(),
                                    request.getInterval());
                            break;
                        case COPY:
                            respond = handleCopy(request.getPath());
                            break;
                        case COUNT:
                            respond = handleCount(request.getPath());
                            break;
                        case LAST_MODIFIED:
                            respond = handleLastModified(request.getPath());
                            break;
                        default:
                            // If the operation code is not defined, we just skip this request
                            continue;
                    }
                }

                byte[] respondBytes = serializer.serialize(respond).toBytes();

                DatagramPacket reply = new DatagramPacket(
                        respondBytes,
                        respondBytes.length,
                        requestPacket.getAddress(),
                        requestPacket.getPort());
                socket.send(reply); //send packet using socket method
                history.put(requestId, respond);
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