import Functional.Tokenizer;
import dataStruct.AutKeys;
import dataStruct.Client;
import dataStruct.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
            this.clientSocket = clientSocket;
            this.client = new Client();
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
                message += (char)this.clientReader.read();
            }
            catch (IOException IOE){
                System.out.println("Error while reading from the client socket!");
                System.exit(1);
            }
        }
        System.out.println("Client name - " + message);
        if (message.contains(this.suffix) == false || message.length() == this.suffix.length()){
            System.out.println("Wrong input client name: " + message);
            this.clientWriter.println("301 SYNTAX ERROR\\a\\b");
            this.clientWriter.flush();
            this.closeClient();
            return false;
        }

        Tokenizer tokenizer = new Tokenizer(message, this.suffix);
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
                message += (char)this.clientReader.read();
            }
            catch (IOException IOE){
                System.out.println("Error while reading from the client socket");
                return false;
            }
        }
        message = message.replace("\n", "");
        Tokenizer tokenizer = new Tokenizer(message, this.suffix);
        //We check if we have the suffix in the message
        if (tokenizer.hasMoreTokens() == false || message.length() >5){
            System.out.println("Not valid client id " + message);
            this.clientWriter.println("301 SYNTAX ERROR\\a\\b");
            this.clientWriter.flush();
            this.closeClient();
        }
        //Now we extract the client AutKey number from the string and set current aut. key based on it
        try {
            String strKeyID = tokenizer.nextToken();
            System.out.println("Key id: " + strKeyID);
            Integer keyID = Integer.parseInt(strKeyID);

            //Now we need to check if the key is in the available range
            if (keyID > 0 && keyID < 5){
                this.client.keyID = keyID;
                this.autKey = this.keyDatabase.returnKeys(this.client.keyID);
            }
            else {
                System.out.println("The key is out of the permissible range.");
                this.clientWriter.println("303 KEY OUT OF RANGE\\a\\b");
                this.clientWriter.flush();
                return false;
            }
        }
        catch (NumberFormatException NFE){
            System.out.println("The client ID is not a number!");
            this.clientWriter.println("301 SYNTAX ERROR\\a\\b");
            this.clientWriter.flush();
            this.closeClient();
        }
        return true;
    }

    public boolean serverConfirmation (){
        //First we calculate the hash from the client name as required
        int hash = 0;
        for (int i = 0; i < this.client.username.length(); i++){
            hash += this.client.username.charAt(i);
        }
        hash = (hash * 1000) % 65536;

        System.out.println("basic hash : " + hash);

        this.clientWriter.println(Integer.toString((hash + this.autKey.getServerKey()) % 65536) + this.suffix);
        this.clientWriter.flush();

        //Now we read the client confirmation message and compare the two hashes
        String message = new String();
        while ((message.contains(this.suffix) == false) && (message.length() < 9)){
            try {
                message += (char)this.clientReader.read();
            }
            catch (IOException IOE){
                System.out.println("Error while reading from the client socket!");
                return false;
            }
        }

        message = message.replace("\n", "");
        Tokenizer tokenizer = new Tokenizer(message, this.suffix);
        if (tokenizer.hasMoreTokens() == false){
            System.out.println("Wrong message format: " + message);
            this.clientWriter.println("301 SYNTAX ERROR\\a\\b");
            this.clientWriter.flush();
            this.closeClient();
        }

        Integer clientHash;
        try {
            clientHash = Integer.parseInt(tokenizer.nextToken());
        }
        catch (NumberFormatException NFE){
            System.out.println("Client confirmation hash contains other symbols than numbers!");
            this.clientWriter.println("301 SYNTAX ERROR\\a\\b");
            this.clientWriter.flush();
            this.closeClient();
            return false;
        }

        if (clientHash.equals((hash + this.autKey.getClientKey()) % 65536) == false){
            System.out.println("Server login failed!");
            this.clientWriter.println("300 LOGIN FAILED\\a\\b\t");
            this.clientWriter.flush();
            this.closeClient();
            return false;
        }

        this.clientWriter.println("200 OK\\a\\b");
        this.clientWriter.flush();
        return true;
    }

    

    @Override
    public void run (){
        //This suggests that some reading exception etc. has occured
        if (this.getClientName() == false || this.getClienID() == false || this.serverConfirmation() == false) {
            return;
        }
        System.out.println("Logged in all right!");
        System.out.println(this.client.username);
        System.out.println(this.client.keyID);
    }

}
