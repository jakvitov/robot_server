import Activity.Mover;
import Activity.Picker;
import Functional.Tokenizer;
import dataStruct.AutKeys;
import dataStruct.Client;
import dataStruct.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Client handler takes care of the individual clients in a separate thread
 */

public class ClientHandler implements Runnable{

    private PrintWriter clientWriter;
    private BufferedReader clientReader;
    private Socket clientSocket;
    private AutKeys keyDatabase;
    private Pair autKey;
    private Client client;
    private String suffix;

    public ClientHandler (Socket clientSocket, String suffix){
        try {
            this.suffix = suffix;
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

    public void setTimeout (int time){
        try {
          this.clientSocket.setSoTimeout(time);
        }
        catch (IOException IOE){
            System.out.println("Error while setting up the timeout.");
            return;
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
                this.closeClient();
                return false;
            }
        }
        System.out.println("Client name - " + message);
        if (message.contains(this.suffix) == false || message.length() == this.suffix.length()){
            System.out.println("Wrong input client name: " + message);
            this.clientWriter.print("301 SYNTAX ERROR\\a\\b");
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
        while ((message.contains(this.suffix) == false) && (message.length() < 6)){
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

        this.clientWriter.print(Integer.toString((hash + this.autKey.getServerKey()) % 65536) + this.suffix);
        this.clientWriter.flush();

        //Now we read the client confirmation message and compare the two hashes
        String message = new String();
        while ((message.contains(this.suffix) == false) && (message.length() < 9)){
            try {
                message += (char) this.clientReader.read();
            }
            catch (IOException IOE){
                System.out.println("Error while reading from the client socket!");
                this.closeClient();
                return false;
            }
        }

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



    @Override
    public void run (){

        this.setTimeout(1000);

        //This suggests that some reading exception etc. has occured
        if (this.getClientName() == false || this.getClienID() == false || this.serverConfirmation() == false) {
            return;
        }
        System.out.println("Logged in all right!");
        System.out.println(this.client.username);
        System.out.println(this.client.keyID);

        //Now we start to move the client to the [0:0]
        Mover mover = new Mover(this.clientWriter, this.clientReader, this.clientSocket, this.suffix);

        if (mover.init() == false){
            System.out.println("Error while initing the client");
            this.closeClient();
        }
        if (mover.navigator() == false){
            System.out.println("Error while navigatinng the client to the diagonal!");
        }

        //Now we pick up the message
        Picker picker = new Picker(this.clientWriter, this.clientReader, this.clientSocket, this.suffix);
        if (picker.pickUp() == false){
            System.out.println("Error while picking up the message!");
        }

    }

}
