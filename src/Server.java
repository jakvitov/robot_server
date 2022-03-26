import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import dataStruct.AutKeys;
import dataStruct.Pair;

public class Server {
    private String suffix = "\\a\\b";
    private Socket clientSocket;
    private BufferedReader clientReader;
    private PrintWriter clientWriter;
    private Pair AutKey;
    private AutKeys keyDatabase;
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
        }
        catch (IOException e){
            System.out.println("Error in opening the socket");
        }
    }
    //Get username from each robot
    public String readName () {
        try {
            this.clientReader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            String read_name = clientReader.readLine();
            //If the username is too long or too short
            if (read_name.length() != 22){
                System.out.println("Too short / too long client name");
                this.clientSocket.close();
                System.exit(1);
            }
            //If the name contains the correct suffix inside
            else if (read_name.substring(0, 17).contains(suffix)){
                System.out.println("Client name contains /a/b");
                this.clientSocket.close();
                System.exit(1);
            }
            //If the end of the message does not include the requiered suffix
            else if (!read_name.substring(18, 21).contains(suffix)){
                System.out.println("Client name contains /a/b");
                this.clientSocket.close();
                System.exit(1);
            }
            return read_name.substring(0, 17);
        }
        catch (IOException e){
           System.out.println("Error in reading the message.");
           System.exit(1);
        }
        return null;
    }
    //Server key request and key_id reading
    public void keyRequest (Socket clientSocket) {
        int int_id = 0;
        try {
            this.clientWriter = new PrintWriter(this.clientSocket.getOutputStream(), true);
            //Send the key request message
            this.clientWriter.print("107 KEY REQUEST\\a\\b");
            //Scan the incomming key_id
            String key_id = this.clientReader.readLine();
            //If the key_id is too long
            if (key_id.length() > 3 + suffix.length() || key_id.length() <= suffix.length()){
                System.out.println("Too long key_id!");
                this.clientSocket.close();
                System.exit(1);
            }
            else if (!(key_id.substring(key_id.length() - suffix.length(), key_id.length() - 1).contains(suffix))){
                System.out.println("No suffix in the key");
                this.clientSocket.close();
                System.exit(1);
            }
            //Finally parse the key into and int and return it
            else {
                try {
                    key_id = key_id.substring(0, key_id.length() - suffix.length());
                    int_id = Integer.parseInt(key_id);
                }
                catch (NumberFormatException NFE){
                    System.out.println("Key id is not parseable!");
                    this.clientSocket.close();
                    System.exit(1);
                }
            }
        }
        catch (IOException e){
            System.out.println("Trouble in opening the writing stream.");
            System.exit(1);
        }
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
        this.clientWriter.print(hash + suffix);
        //CLIENT_CONFIRM
        //Now we wait for client to send his confirmation, compute back hash and confirm or cut off comunication
        try {
            String client_confirmation = this.clientReader.readLine();
            //todo: check if the client confirmation has correct format
            client_confirmation = client_confirmation.substring(0, 4);
            int_client_confirmation = Integer.parseInt(client_confirmation);
        }
        catch (IOException e){
            System.out.println("Error while reading/parsing client_confirmation message");
            System.exit(0);
        }
    }

    public static void main(String[] args) {

    }
}
