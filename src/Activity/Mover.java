package Activity;

import Functional.Tokenizer;
import dataStruct.Coord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * This class is used to move the robots around and navigate them to the [0,0] coordinate
 * to pick up the treasure
 */

public class Mover {

    private PrintWriter clientWriter;
    private BufferedReader clientReader;
    private Socket clientSocket;
    private String suffix = "\\a\\b";
    private int errorFlag = -111111;
    private Coord lastCoord;

    public Mover (PrintWriter clientWriter, BufferedReader clientReader, Socket clientSocket){
        this.clientReader = clientReader;
        this.clientWriter = clientWriter;
        this.clientSocket = clientSocket;
    }

    //A method used to get the coordinates from the client ok messages
    public Coord clientOk(){
        String message = new String();
        while (message.contains(this.suffix) == false && message.length() < 13) {
            try {
                message += (char) this.clientReader.read();
            } catch (IOException IOE) {
                System.out.println("Error while reading from the client socket!");
                //We return a fail flag - this value can never be in a message 12 chars long
                return new Coord(this.errorFlag, this.errorFlag);
            }
        }
        if (message == null || message.contains(this.suffix) == false){
            System.out.println("The coord message is null or without suffix!");
            return new Coord(this.errorFlag, this.errorFlag);
        }

        message = message.replace(this.suffix, "");
        message = message.replace("\n", "");
        StringTokenizer tokenizer = new StringTokenizer(message, " ");
        if (tokenizer.countTokens() != 3){
            System.out.println("The message has a wrong format: " + message);
            return new Coord(this.errorFlag, this.errorFlag);
        }

        try {
            //We skip the first token, that is the OK in the message
               tokenizer.nextToken();
               return new Coord(Integer.parseInt(tokenizer.nextToken()), Integer.parseInt(tokenizer.nextToken()));
        }
        catch (NumberFormatException NFE){
            System.out.println("The coordinates do not include numbers: " + message);
            return new Coord(this.errorFlag, this.errorFlag);
        }
    }

    public void turnRight (){
        this.clientWriter.println("104 TURN RIGHT\\a\\b");
        this.clientWriter.flush();
    }

    public void turnLeft (){
        this.clientWriter.println("103 TURN LEFT\\a\\b\t");
        this.clientWriter.flush();
    }

    public void goForward (){
        this.clientWriter.println("\t102 MOVE\\a\\b");
        this.clientWriter.flush();
    }

    //A method used to get the starting coordinates from the robot
    public boolean init (){
        this.turnLeft();
        this.lastCoord = this.clientOk();

        if (this.lastCoord == null) {
            return false;
        }

        if (this.lastCoord.errorFlag()){
            return false;
        }

        this.turnRight();
        if (this.lastCoord.errorFlag()){
            return false;
        }
        this.lastCoord.printCoord();
        return true;
    }


}
