import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.Map;
import dataStruct.AutKeys;
import dataStruct.Pair;
import Messages.serverMsg;
import Messages.serverReceive;

public class Server {
    private String suffix = "\\a\\b";
    private Socket clientSocket;
    private BufferedReader clientReader;
    private Pair AutKey;
    private AutKeys keyDatabase;
    private serverMsg serverMsg;
    private serverReceive serverReceive;
    //Constructor
    public Server () {
        this.keyDatabase = new AutKeys();
    }
    //Start server to listen on the given port number
    //Return socket to communicate with the client
    public void start(int port_num) throws Exception {
        try {
            ServerSocket serverSocket = new ServerSocket(port_num);
            this.clientSocket = serverSocket.accept();
            this.serverMsg = new serverMsg(this.clientSocket);
            this.serverReceive = new serverReceive(this.clientSocket);
        }
        catch (IOException e){
            System.out.println("Error in opening the socket");
        }
    }
    //Get username from each robot
    public String readName () {
        try {
          String clientName = this.serverReceive.client_username();
          return clientName;
        }
        catch (Exception e){
           System.out.println("Error in reading the message.");
           System.exit(1);
        }
    return null;
    }
    //Server key request and key_id reading
    public void keyRequest (Socket clientSocket) {
        int int_id = this.serverReceive.client_key_id();
        this.AutKey = keyDatabase.returnKeys(int_id);
    }
    //Server confirm message and hash computing
    public void serverConfirm(int client_key, String client_name){
        //First we calculate the hast from the client_key id
        int sum = 0;
        int int_client_confirmation = 0;
        for (int i = 0; i < client_name.length(); i++){
            sum += client_name.charAt(i);
        }
        int hash = (sum * 1000) % 65536;
        //Add the server id num to the hash
        hash = (hash + AutKey.getServerKey()) % 65536;
        //Send server confirmation to the client
        this.serverMsg.server_confirmation(hash);
        //CLIENT_CONFIRM
        //Now we wait for client to send his confirmation, compute back hash and confirm or cut off comunication
        try {
            int client_confirmation = this.serverReceive.client_confirmation();
            //Now we try to compute client hash again using the scanned client confirmation
            int client_hash = ((((sum * 1000) % 65536) + AutKey.getClientKey()) % 65536);
            if  (client_hash == int_client_confirmation){
                this.serverMsg.server_ok();
            }
            else {
                this.serverMsg.server_login_failed();
                clientSocket.close();
            }
        }
        catch (IOException e){
            System.out.println("Error while reading/parsing client_confirmation message");
            System.exit(0);
        }
    }

    public static void main(String[] args) {

    }
}
