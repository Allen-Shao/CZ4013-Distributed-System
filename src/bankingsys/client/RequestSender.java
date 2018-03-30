package bankingsys.client;

import java.util.Scanner;

/**
 * Created by koallen on 29/3/18.
 */
public class RequestSender {

    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print(">>> ");
            String command = sc.nextLine();
            System.out.println(command);
        }


    }
}
