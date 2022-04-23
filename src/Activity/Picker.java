package Activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * This class is used to pick up the final treasure
 */
public class Picker {

    private PrintWriter clientWriter;
    private BufferedReader clientReader;
    private Socket clientSocket;
    private String suffix;

    public Picker (PrintWriter clientWriter, BufferedReader clientReader, Socket clientSocket, String suffix){
        this.clientWriter = clientWriter;
        this.clientReader = clientReader;
        this.clientSocket = clientSocket;
        this.suffix = suffix;
    }

    public void closeClient(){
        try {
            this.clientSocket.close();
        }
        catch (IOException IOE){
            System.out.println("Error while closing the client socket!");
        }
    }

    public boolean pickUp (){

        this.clientWriter.print("105 GET MESSAGE" + this.suffix);
        this.clientWriter.flush();

        String message = new String();
        while ((message.contains(this.suffix) == false) && message.length() < 100){
            try {
                message += (char) this.clientReader.read();
            }
            catch (IOException IOE){
                System.out.println("Error while reading from the client reader!");
                this.clientWriter.print("301 SYNTAX ERROR" + this.suffix);
                this.clientWriter.flush();
                this.closeClient();
                return false;
            }
        }

        if (message.contains(this.suffix) == false){
            this.clientWriter.print("301 SYNTAX ERROR" + this.suffix);
            this.clientWriter.flush();
            this.closeClient();
            return false;
        }

        //We socessfully picked up the message so we log out
        this.clientWriter.print("106 LOGOUT" + this.suffix);
        this.clientWriter.flush();
        this.closeClient();
        return true;
    }
}
