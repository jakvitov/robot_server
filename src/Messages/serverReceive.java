package Messages;

import dataStruct.Pair;

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
    public int client_confirmation(){
        int result = 0;
        String input = "not_initialised";
        try {
            input = clientReader.readLine();
        }
        catch (IOException e){
            System.out.println("Error while reading the message.");
            System.exit(0);
        }
        input = input.substring(0, input.length()-2);
        try {
            result = Integer.parseInt(input);
        }
        catch (Exception e){
            System.out.println("Not parsable int!");
            this.serverMsg.server_syntax_error();
            this.closeConnection();
        }
        return result;
    }
    public Pair client_ok (){
        String input = "not_initialised";
        try {
            input = this.clientReader.readLine();
        }
        catch (IOException e){
            System.out.println("Error whiel reading the message");
            System.exit(0);
        }
        int x = -10901;
        int y = -10901;
        try {
            x = Integer.parseInt(input.substring(3,4));
            y = Integer.parseInt(input.substring(5, 6));
        }
        catch (Exception e){
            System.out.println("Not parsable int!");
            this.serverMsg.server_syntax_error();
            this.closeConnection();
        }
        Pair result = new Pair(x, y);
        return result;
    }
    //Robot fully charged message
    public boolean client_full_power(){
        String input = "not_initialised";
        try {
            input = this.clientReader.readLine();
        }
        catch (IOException IOE){
            System.out.println("Error while reading from the input buffer.");
            return false;
        }
        if (input.equals("FULL POWER" + suffix)){
            return true;
        }
        return false;
    }
    //If we receive robot recharging message
    public void client_recharging(){
        try {
            this.clientSocket.setSoTimeout(5 * 1000);
            if (this.client_full_power()) {
                return;
            }
            else {
                this.serverMsg.server_logic_error();
                this.closeConnection();
            }
        }
        catch (java.net.SocketException SE){
         //The socket timeouted
         serverMsg.server_logic_error();
         this.closeConnection();
        }
    }
}

