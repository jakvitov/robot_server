package Messages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

//Class to receive messages from clients
public class serverReceive {
    private Socket clientSocket;
    private BufferedReader clientReader;
    private String suffix = "\\a\\b";
    serverMsg serverMsg;
    private void closeConnection(){
       try {
           this.clientSocket.close();
       }
       catch (IOException e){
           System.out.println("Error while ending the communication.");
       }
    }
    public serverReceive(Socket inputSocket){
        this.clientSocket = inputSocket;
        try {
            this.clientReader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            this.serverMsg = new serverMsg(this.clientSocket);
        } catch (IOException e){
            System.out.println("Error while opening the reader stream from client socket.");
            System.exit(1);
        }

    }
    public String  client_username(){
        String input = "not_initialised";
        String resultName = "not_initialised";
        //Read the input username
        try {
            input = clientReader.readLine();
        }
        catch (IOException e){
            System.out.println("Error while reading from the input stream.");
            this.closeConnection();
        }
        if (input.length() > 22){
            System.out.println("Too short client name");
            this.closeConnection();
        }
        if (input.length() > 2 && input.substring(input.length() - 2, input.length()).equals(suffix)){
            resultName = input.substring(0,input.length() - 2);
            return resultName;
        }
        return null;
    }
    public int client_key_id(){
        String input = "not_initialised";
        int key_id = 0;
        try {
            input = clientReader.readLine();
        }
        catch (IOException e){
            System.out.println("Error while reading the messgae");
        }
        if (input.length() > 7){
            System.out.println("Too long client key_id");
            this.closeConnection();
        }
        try {
            Integer.parseInt(input);
        } catch (NumberFormatException NFE){
            System.out.println("Error while parsing the client_key_id");
            this.closeConnection();
        }
        return key_id;
    }
}

