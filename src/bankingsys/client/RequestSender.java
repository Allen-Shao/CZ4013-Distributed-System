package bankingsys.client;

import bankingsys.io.Deserializer;
import bankingsys.io.Serializer;
import bankingsys.message.ServiceRequest;
import bankingsys.message.ServiceResponse;
import bankingsys.server.model.BankAccount;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;
import java.util.Scanner;

import static bankingsys.Constant.BUFFER_SIZE;
import static bankingsys.Constant.MONITOR_PORT;
import static bankingsys.Constant.SERVER_PORT;
import static bankingsys.message.ServiceResponse.ResponseType.SUCCESS;

/**
 * Main class that implements the client
 *
 * Available commands:
 * - Create Name Password CurrencyType Amount
 * - Close Name AccountNumber Password
 * - Deposit Name AccountNumber Password CurrencyType Amount
 * - Withdraw Name AccountNumber Password CurrencyType Amount
 * - Monitor Interval
 * - Check Name AccountNumber Password
 * - Transfer Name AccountNumber Password TargetAccount Amount
 */

public class RequestSender {

    public static void main(String args[]) throws IOException {
        DatagramSocket socket = null;
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            socket = new DatagramSocket();
            Scanner sc = new Scanner(System.in);
            while (true) {
                System.out.print(">>> ");
                String command = sc.nextLine();
                String[] commandSplits = command.split(" ");
                String commandType = commandSplits[0];

                ServiceRequest request = null;
                switch (commandType) {
                    case "create":
                        request = new ServiceRequest(
                                'b',
                                commandSplits[1],
                                null,
                                commandSplits[2],
                                Float.parseFloat(commandSplits[4]),
                                null,
                                BankAccount.Currency.valueOf(commandSplits[3]));
                        break;
                    case "close":
                        request = new ServiceRequest(
                                'a',
                                commandSplits[1],
                                Integer.parseInt(commandSplits[2]),
                                commandSplits[3],
                                null,
                                null,
                                null);
                        break;
                    case "deposit":
                        request = new ServiceRequest(
                                'e',
                                commandSplits[1],
                                Integer.parseInt(commandSplits[2]),
                                commandSplits[3],
                                -Float.parseFloat(commandSplits[5]),
                                null,
                                BankAccount.Currency.valueOf(commandSplits[4]));
                        break;
                    case "withdraw":
                        request = new ServiceRequest(
                                'e',
                                commandSplits[1],
                                Integer.parseInt(commandSplits[2]),
                                commandSplits[3],
                                Float.parseFloat(commandSplits[5]),
                                null,
                                BankAccount.Currency.valueOf(commandSplits[4]));
                        break;
                    case "monitor":
                        request = new ServiceRequest(
                                'c',
                                null,
                                null,
                                null,
                                null,
                                null,
                                null);
                        break;
                    case "Check":
                        request = new ServiceRequest(
                                'd',
                                commandSplits[1],
                                Integer.parseInt(commandSplits[2]),
                                commandSplits[3],
                                null,
                                null,
                                null);
                        break;
                    case "transfer":
                        request = new ServiceRequest(
                                'f',
                                commandSplits[1],
                                Integer.parseInt(commandSplits[2]),
                                commandSplits[3],
                                Float.parseFloat(commandSplits[5]),
                                Integer.parseInt(commandSplits[4]),
                                null);
                        break;
                    case "exit":
                        return;
                }

                if (request != null) {
                    Serializer serializer = new Serializer();
                    request.write(serializer);

                    InetAddress address = InetAddress.getByName("localhost");
                    DatagramPacket packet = new DatagramPacket(serializer.getBuffer(), serializer.getBufferLength(),
                            address, SERVER_PORT);
                    socket.send(packet);

                    DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                    socket.receive(reply);
                    Deserializer deserializer = new Deserializer(buffer);
                    ServiceResponse response = new ServiceResponse();
                    response.read(deserializer);
                    System.out.println(response.getResponseCode());

                    if (request.getRequestType() == 'c' && response.getResponseCode() == SUCCESS) {
                        startMonitoring();
                    }
                } else {
                    System.out.println("Command parse error.");
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

    private static void startMonitoring() {
        DatagramSocket monitoringSocket = null;
        byte[] buffer = null;
        try {
            buffer = new byte[BUFFER_SIZE];
            monitoringSocket = new DatagramSocket(MONITOR_PORT);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            monitoringSocket.receive(packet);
            Deserializer deserializer = new Deserializer(buffer);
            ServiceResponse response = new ServiceResponse();
            response.read(deserializer);
            System.out.println("Update: Account No. " + response.getResponseAccount() +
                    " now has balance " + response.getResponseAmount());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (monitoringSocket != null)
                monitoringSocket.close();
        }
    }
}
