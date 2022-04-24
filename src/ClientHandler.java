import Activity.Logger;
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
 * Once a client logs in, this thread starts and it logs the client in and invokes the mover and Picker classes
 * to navigate him to the [0:0] and pick up the message
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

    @Override
    public void run (){

        this.setTimeout(1000);
        Logger logger = new Logger(this.clientWriter, this.clientReader, this.suffix, this.clientSocket);


        //This suggests that some reading exception etc. has occured
        if (logger.getClientName() == false || logger.getClienID() == false || logger.serverConfirmation() == false) {
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
            this.closeClient();
        }

        //Now we pick up the message
        Picker picker = new Picker(this.clientWriter, this.clientReader, this.clientSocket, this.suffix);
        if (picker.pickUp() == false){
            System.out.println("Error while picking up the message!");
            this.closeClient();
        }
    }
}
