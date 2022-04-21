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
    private String suffix = "\\a\\b";

    public Picker (PrintWriter clientWriter, BufferedReader clientReader, Socket clientSocket){
        this.clientWriter = clientWriter;
        this.clientReader = clientReader;
        this.clientSocket = clientSocket;
    }

    public boolean pickUp (){
        String message = new String();
        while (message.contains(this.suffix) && message.length() < 101){
            try {
                message += this.clientReader.read();
            }
            catch (IOException IOE){
                System.out.println("Error while reading from the client reader!");
                this.clientWriter.println("301 SYNTAX ERROR\\a\\b");
                this.clientWriter.flush();
                return false;
            }
        }

        if (message.contains(this.suffix) == false){
            this.clientWriter.println("301 SYNTAX ERROR\\a\\b");
            this.clientWriter.flush();
            return false;
        }

        //We socessfully picked up the message so we log out
        this.clientWriter.println("106 LOGOUT\\a\\b");
        this.clientWriter.flush();
        return true;
    }
}
