package Testing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * A basic class to connect to the server and send messges from the console
 * Used just for debugging
 */
public class ClientTester {

    private PrintWriter writer;
    private BufferedReader reader;
    private Socket socket;
    private String suffix = "\\a\\b";

    public ClientTester (){
        try {
            this.socket = new Socket("127.0.0.1", 8080);
            this.writer = new PrintWriter(socket.getOutputStream());
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (IOException IOE){
            System.out.println("Error while connecting to the server!");
            System.exit(1);
        }
    }

    public void message (){

        Thread thread = new Thread(new ClientReader(this.reader));
        thread.start();

        Scanner scan = new Scanner(System.in);
        System.out.println("Enter your message:");
        String message = scan.nextLine();
        this.writer.println(message + suffix);
        this.writer.flush();
    }

}
