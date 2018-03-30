package bankingsys.client;

import bankingsys.io.Deserializer;
import bankingsys.io.Serializer;
import bankingsys.message.ServiceRequest;
import bankingsys.message.ServiceResponse;
import bankingsys.server.model.BankAccount;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

import static bankingsys.Constant.BUFFER_SIZE;
import static bankingsys.Constant.SERVER_PORT;
import static bankingsys.message.ServiceResponse.ResponseStatus.SUCCESS;
import static bankingsys.Constant.PASSWORD_LENGTH;
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
    private DatagramSocket socket = null;
    private byte[] buffer = new byte[BUFFER_SIZE];
    private Integer requestID = 0;

    public static void main(String args[]) throws IOException {
        new RequestSender().run();
    }

    public void run() {
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(200);
            Scanner sc = new Scanner(System.in);
            while (true) {
                System.out.print(">>> ");
                String command = sc.nextLine();
                String[] commandSplits = command.split(" ");
                String commandType = commandSplits[0];

                ServiceRequest request = null;
                switch (commandType) {
                    case "create":
                        if (commandSplits[2].length() != PASSWORD_LENGTH) {
                            System.out.println("Password length must be 6");
                            continue;
                        }
                        request = new ServiceRequest(
                                requestID,
                                'b',
                                commandSplits[1],
                                null,
                                commandSplits[2],
                                Float.parseFloat(commandSplits[4]),
                                null,
                                BankAccount.Currency.valueOf(commandSplits[3]),
                                null);
                        break;
                    case "close":
                        request = new ServiceRequest(
                                requestID,
                                'a',
                                commandSplits[1],
                                Integer.parseInt(commandSplits[2]),
                                commandSplits[3],
                                null,
                                null,
                                null,
                                null);
                        break;
                    case "deposit":
                        request = new ServiceRequest(
                                requestID,
                                'e',
                                commandSplits[1],
                                Integer.parseInt(commandSplits[2]),
                                commandSplits[3],
                                -Float.parseFloat(commandSplits[5]),
                                null,
                                BankAccount.Currency.valueOf(commandSplits[4]),
                                null);
                        break;
                    case "withdraw":
                        request = new ServiceRequest(
                                requestID,
                                'e',
                                commandSplits[1],
                                Integer.parseInt(commandSplits[2]),
                                commandSplits[3],
                                Float.parseFloat(commandSplits[5]),
                                null,
                                BankAccount.Currency.valueOf(commandSplits[4]),
                                null);
                        break;
                    case "monitor":
                        request = new ServiceRequest(
                                requestID,
                                'c',
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                Integer.parseInt(commandSplits[1]));
                        break;
                    case "Check":
                        request = new ServiceRequest(
                                requestID,
                                'd',
                                commandSplits[1],
                                Integer.parseInt(commandSplits[2]),
                                commandSplits[3],
                                null,
                                null,
                                null,
                                null);
                        break;
                    case "transfer":
                        request = new ServiceRequest(
                                requestID,
                                'f',
                                commandSplits[1],
                                Integer.parseInt(commandSplits[2]),
                                commandSplits[3],
                                Float.parseFloat(commandSplits[5]),
                                Integer.parseInt(commandSplits[4]),
                                null,
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
                    System.out.println(response.getResponseMessage());

                    if (request.getRequestType() == 'c' && response.getResponseCode() == SUCCESS) {
                        startMonitoring();
                    }
                } else {
                    System.out.println("Command parse error.");
                }

                requestID++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

    private void startMonitoring() {
        try {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            Deserializer deserializer = new Deserializer(buffer);
            ServiceResponse response = new ServiceResponse();
            response.read(deserializer);
            System.out.println("Update: Account No. " + response.getResponseAccount() +
                    " now has balance " + response.getResponseAmount());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
