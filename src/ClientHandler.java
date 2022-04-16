import dataStruct.AutKeys;
import dataStruct.Client;
import dataStruct.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

public class ClientHandler implements Runnable{

    private PrintWriter clientWriter;
    private BufferedReader clientReader;
    private Socket clientSocket;
    private AutKeys keyDatabase;
    private Pair autKey;
    private Client client;
    private String suffix = "\\a\\b";

    public ClientHandler (Socket clientSocket){
        try {
            this.clientWriter = new PrintWriter(clientSocket.getOutputStream());
            this.clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            keyDatabase = new AutKeys();
        }
        catch (IOException IOE){
            System.out.println("Error while opening the reader/writer!");
            System.exit(1);
        }
    }

    public boolean getClientName (){
        String message = new String();

        while ((message.contains(this.suffix) == false) && (message.length() < 21)){
            try {
                message += this.clientReader.readLine();
            }
            catch (IOException IOE){
                System.out.println("Error while reading from the client socket!");
                System.exit(1);
            }
        }

        if (message.contains(this.suffix) == false || message.length() == this.suffix.length()){
            System.out.println("Wrong input client name: " + message);
            this.clientWriter.println("301 SYNTAX ERROR\\a\\b");
            this.clientWriter.flush();
            return false;
        }

        StringTokenizer tokenizer = new StringTokenizer(message, this.suffix);
        this.client.username = tokenizer.nextToken();
        return true;
    }

    @Override
    public void run (){

    }

}
