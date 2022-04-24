package Activity;

import Functional.Tokenizer;
import dataStruct.AutKeys;
import dataStruct.Client;
import dataStruct.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * A Logger class that is used to log the robot in for the Client Handler
 */

public class Logger {

    private PrintWriter clientWriter;
    private BufferedReader clientReader;
    private Socket clientSocket;
    private Pair autKey;
    private Client client;
    private String suffix;
    private AutKeys keyDatabase;

    public Logger(PrintWriter clientWriter, BufferedReader clientReader, String suffix,
                  Socket clientSocket) {
        this.clientWriter = clientWriter;
        this.clientReader = clientReader;
        this.suffix = suffix;
        this.clientSocket = clientSocket;
        keyDatabase = new AutKeys();
        this.client = new Client();
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

        while ((message.contains(this.suffix) == false) && (message.length() < 20)){
            try {
                message += (char)this.clientReader.read();
            }
            catch (IOException IOE){
                System.out.println("Error while reading from the client socket!");
                this.closeClient();
                return false;
            }
        }
        System.out.println("Client name - " + message);
        if (message.contains(this.suffix) == false || message.length() == this.suffix.length()){
            System.out.println("Wrong input client name: " + message);
            this.clientWriter.print("301 SYNTAX ERROR" + this.suffix);
            this.clientWriter.flush();
            this.closeClient();
            return false;
        }

        Tokenizer tokenizer = new Tokenizer(message, this.suffix);
        this.client.username = tokenizer.nextToken();
        return true;
    }

    public boolean getClienID (){

        this.clientWriter.print("107 KEY REQUEST" + this.suffix);
        this.clientWriter.flush();

        //Now we listen for the response
        String message = new String();
        while ((message.contains(this.suffix) == false) && (message.length() < 5)){
            try {
                message += (char)this.clientReader.read();
            }
            catch (IOException IOE){
                System.out.println("Error while reading from the client socket");
                return false;
            }
        }
        Tokenizer tokenizer = new Tokenizer(message, this.suffix);
        //We check if we have the suffix in the message
        if (tokenizer.hasMoreTokens() == false || message.length() >5){
            System.out.println("Not valid client id " + message);
            this.clientWriter.print("301 SYNTAX ERROR" + this.suffix);
            this.clientWriter.flush();
            this.closeClient();
        }
        //Now we extract the client AutKey number from the string and set current aut. key based on it
        try {
            String strKeyID = tokenizer.nextToken();
            System.out.println("Key id: " + strKeyID);
            Integer keyID = Integer.parseInt(strKeyID);

            //Now we need to check if the key is in the available range
            if (keyID >= 0 && keyID < 5){
                this.client.keyID = keyID;
                this.autKey = this.keyDatabase.returnKeys(this.client.keyID);
            }
            else {
                System.out.println("The key is out of the permissible range.");
                this.clientWriter.print("303 KEY OUT OF RANGE" + this.suffix);
                this.clientWriter.flush();
                this.closeClient();
                return false;
            }
        }
        catch (NumberFormatException NFE){
            System.out.println("The client ID is not a number!");
            this.clientWriter.print("301 SYNTAX ERROR" + this.suffix);
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

        if (this.autKey != null) {
            this.clientWriter.print(Integer.toString((hash + this.autKey.getServerKey()) % 65536) + this.suffix);
            this.clientWriter.flush();
        }
        else {
            return false;
        }

        //Now we read the client confirmation message and compare the two hashes
        String message = new String();
        while ((message.contains(this.suffix) == false) && (message.length() < 7)){
            try {
                message += (char) this.clientReader.read();
            }
            catch (IOException IOE){
                System.out.println("Error while reading from the client socket!");
                this.closeClient();
                return false;
            }
        }
        //Now we get the tokenizers client hash from the message
        Tokenizer tokenizer = new Tokenizer(message, this.suffix);
        if (tokenizer.hasMoreTokens() == false){
            System.out.println("Wrong message format: " + message);
            this.clientWriter.print("301 SYNTAX ERROR" + this.suffix);
            this.clientWriter.flush();
            this.closeClient();
        }
        message = tokenizer.nextToken();
        Integer clientHash;
        try {
            clientHash = Integer.parseInt(message);
        }
        catch (NumberFormatException NFE){
            System.out.println("Client confirmation hash contains other symbols than numbers!");
            this.clientWriter.print("301 SYNTAX ERROR" + this.suffix);
            this.clientWriter.flush();
            this.closeClient();
            return false;
        }
        //Now we check if the two hashes are equal, so we can log in the client
        if (clientHash.equals((hash + this.autKey.getClientKey()) % 65536) == false){
            System.out.println("Server login failed!");
            this.clientWriter.print("300 LOGIN FAILED" + this.suffix);
            this.clientWriter.flush();
            this.closeClient();
            return false;
        }

        this.clientWriter.print("200 OK" + this.suffix);
        this.clientWriter.flush();
        return true;
    }
}
