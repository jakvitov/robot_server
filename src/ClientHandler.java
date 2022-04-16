import dataStruct.AutKeys;
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

    @Override
    public void run (){

    }

}
