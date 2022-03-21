import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private String suffix = "\\a\\b";
    private Socket clientSocket;
    private BufferedReader clientReader;
    private PrintWriter clientWriter;
    //Constructor
    public Server () {

    }
    //Start server to listen on the given port number
    //Return socket to communicate with the client
    public void start(int port_num) throws Exception {
        try {
            ServerSocket serverSocket = new ServerSocket(port_num);
            this.clientSocket = serverSocket.accept();
        }
        catch (Exception e){
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
        catch (Exception e){
           System.out.println("Error in reading the message.");
           System.exit(1);
        }
        return null;
    }
    //Server key request and key_id reading
    public int keyRequest (Socket clientSocket) {
        int int_id = 0;
        try {
            this.clientWriter = new PrintWriter(this.clientSocket.getOutputStream(), true);
            //Send the key request message
            this.clientWriter.println("107 KEY REQUEST\\a\\b");
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
        catch (Exception e){
            System.out.println("Trouble in opening the writing stream.");
            System.exit(1);
        }
        return int_id;
    }

    public static void main(String[] args) {

    }
}
