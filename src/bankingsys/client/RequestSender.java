package bankingsys.client;

import bankingsys.io.Deserializer;
import bankingsys.io.Serializer;
import bankingsys.message.ServiceRequest;
import bankingsys.message.ServiceResponse;
import bankingsys.server.model.BankAccount;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.cli.*;

import static bankingsys.Constant.*;
import static bankingsys.message.ServiceResponse.ResponseStatus.SUCCESS;

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

    private static final Logger logger = Logger.getLogger(RequestSender.class.getName());
    private static Options options = new Options();

    private static Boolean simulation = false;

    private DatagramSocket socket = null;
    private byte[] buffer = new byte[BUFFER_SIZE];
    private Integer requestID = 0;

    public static void main(String args[]) throws IOException {
        options.addOption("h", "help", false, "Show help.");
        options.addOption("sim", "simulation", false, "Set mode to 'simulation' with error rate.");
        CommandLineParser parser = new DefaultParser();

        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption("h"))
                help();

            if (cmd.hasOption("sim")) {
                logger.log(Level.INFO, "Using cli argument -sim=" + cmd.getOptionValue("sim"));
                // Whatever you want to do with the setting goes here
                simulation = true;
            } else {
                logger.log(Level.SEVERE, "Missing mode option");
                help();
            }

        } catch (ParseException e) {
            logger.log(Level.SEVERE, "Failed to parse command line properties", e);
            help();
        }

        new RequestSender().run();
    }

    private void run() {
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(TIMEOUT);
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
                            logger.log(Level.SEVERE, "Password length must be " + PASSWORD_LENGTH);
                            continue;
                        }
                        request = new ServiceRequest(
                                requestID,
                                ACCOUNT_CANCEL,
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
                                ACCOUNT_CREATE,
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
                                BALANCE_UPDATE,
                                commandSplits[1],
                                Integer.parseInt(commandSplits[2]),
                                commandSplits[3],
                                Float.parseFloat(commandSplits[5]),
                                null,
                                BankAccount.Currency.valueOf(commandSplits[4]),
                                null);
                        break;
                    case "withdraw":
                        request = new ServiceRequest(
                                requestID,
                                BALANCE_UPDATE,
                                commandSplits[1],
                                Integer.parseInt(commandSplits[2]),
                                commandSplits[3],
                                -Float.parseFloat(commandSplits[5]),
                                null,
                                BankAccount.Currency.valueOf(commandSplits[4]),
                                null);
                        break;
                    case "monitor":
                        request = new ServiceRequest(
                                requestID,
                                ACCOUNT_MONITER,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                Integer.parseInt(commandSplits[1]));
                        break;
                    case "check":
                        request = new ServiceRequest(
                                requestID,
                                BALANCE_CHECK,
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
                                TRANSFER,
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

                    ServiceResponse response = sendRequest(packet);
                    if (request.getRequestType() == ACCOUNT_MONITER && response.getResponseCode() == SUCCESS) {
                        startMonitoring();
                    }
                } else {
                    logger.log(Level.SEVERE, "Command parse error.");
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

    private ServiceResponse sendRequest(DatagramPacket packet) {
        try {
            Boolean timeout = true;
            while (timeout) {
                try {
                    //TODO: simulate sending failure.
                    socket.send(packet);
                    DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                    socket.receive(reply);
                } catch (SocketTimeoutException e) {
                    continue;
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Send or receive error on sending request.");
                    continue;
                }
                timeout = false;
            }

            Deserializer deserializer = new Deserializer(buffer);
            ServiceResponse response = new ServiceResponse();
            response.read(deserializer);
            logger.log(Level.INFO, response.getResponseCode() + response.getResponseMessage());
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void startMonitoring() {
        try {
            socket.setSoTimeout(0);
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                socket.receive(packet);


                Deserializer deserializer = new Deserializer(buffer);
                ServiceResponse response = new ServiceResponse();
                response.read(deserializer);
                logger.log(Level.INFO, "Type: " + response.getResponseType());
                logger.log(Level.INFO, "Type: " + response.getResponseMessage());
                if (response.getResponseType() == END_MONITER) {
                    socket.setSoTimeout(TIMEOUT);
                    return;
                }
                logger.log(Level.INFO, "Update: Account No. " + response.getResponseAccount() +
                        " now has balance " + response.getResponseAmount());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void help() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Main", options);
        System.exit(0);
    }
}
