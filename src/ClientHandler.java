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

    public void closeClient(){
        try {
            this.clientSocket.close();
        }
        catch (IOException IOE){
            System.out.println("Error while closing the client socket!");
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
            this.closeClient();
            return false;
        }

        StringTokenizer tokenizer = new StringTokenizer(message, this.suffix);
        this.client.username = tokenizer.nextToken();
        return true;
    }

    public boolean getClienID (){

        this.clientWriter.println("107 KEY REQUEST\\a\\b");
        this.clientWriter.flush();

        //Now we listen for the response
        String message = new String();
        while ((message.contains(this.suffix) == false) && (message.length() < 6)){
            try {
                message += this.clientReader.readLine();
            }
            catch (IOException IOE){
                System.out.println("Error while reading from the client socket");
                return false;
            }
        }

        StringTokenizer tokenizer = new StringTokenizer(message, this.suffix);
        //We check if we have the suffix in the message
        if (tokenizer.hasMoreTokens() == false){
            return false;
        }
        //Now we extract the client AutKey number from the string and set current aut. key based on it
        try {
            Integer keyID = Integer.parseInt(tokenizer.nextToken());
            this.client.keyID = keyID;
            this.autKey = this.keyDatabase.returnKeys(this.client.keyID);
        }
        catch (NumberFormatException NFE){
            System.out.println("The client ID is not a number!");
            return false;
        }
        return true;
    }

    @Override
    public void run (){

    }

}
