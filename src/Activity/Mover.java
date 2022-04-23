package Activity;

import Functional.Tokenizer;
import dataStruct.Coord;
import dataStruct.Facing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * This class is used to move the robots around and navigate them to the [0,0] coordinate
 * to pick up the treasure
 *
 *
 */

public class Mover {

    private PrintWriter clientWriter;
    private BufferedReader clientReader;
    private Socket clientSocket;
    private String suffix;
    private int errorFlag = -111111;
    public Facing facing;
    public Coord lastCoord;

    public Mover (PrintWriter clientWriter, BufferedReader clientReader, Socket clientSocket, String suffix){
        this.clientReader = clientReader;
        this.clientWriter = clientWriter;
        this.clientSocket = clientSocket;
        this.suffix = suffix;
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
        this.clientWriter.print("104 TURN RIGHT" + this.suffix);
        this.clientWriter.flush();

        if (this.facing != null){
            this.facing = this.facing.next();
        }
    }

    public void turnLeft (){
        this.clientWriter.print("103 TURN LEFT"+ this.suffix);
        this.clientWriter.flush();

        if (this.facing != null){
            this.facing = this.facing.next();
        }
    }

    public void goForward (){
        this.clientWriter.print("102 MOVE" + this.suffix);
        this.clientWriter.flush();
    }

    //Method used to move the robot one left  ( + [-1,0])
    public boolean moveLeft (){

        while (this.facing.equals(Facing.LEFT) == false){
            this.turnRight();
            Coord check = clientOk();

            if (check.errorFlag()){
                return false;
            }
        }

        this.goForward();
        this.lastCoord = clientOk();
        if (this.lastCoord.errorFlag()){
            return false;
        }
        return true;
    }

    //Method used to move the robot one right  ( + [1,0])
    public boolean moveRight (){

        while (this.facing != Facing.RIGHT){
            this.turnRight();
            Coord check = clientOk();

            if (check.errorFlag()){
                return false;
            }
        }

        this.goForward();
        this.lastCoord = clientOk();
        if (this.lastCoord.errorFlag()){
            return false;
        }
        return true;
    }

    //Method used to move the robot one up  ( + [0,1])
    public boolean moveUP (){

        while (this.facing != Facing.UP){
            this.turnRight();
            Coord check = clientOk();

            if (check.errorFlag()){
                return false;
            }
        }

        this.goForward();
        this.lastCoord = clientOk();
        if (this.lastCoord.errorFlag()){
            return false;
        }
        return true;
    }
    //Method used to move the robot one down  ( + [0,-1])
    public boolean moveDown (){

        while (this.facing != Facing.DOWN){
            this.turnRight();
            Coord check = clientOk();

            if (check.errorFlag()){
                return false;
            }
        }

        this.goForward();
        this.lastCoord = clientOk();
        if (this.lastCoord.errorFlag()){
            return false;
        }
        return true;
    }


    //A method used to get the starting coordinates from the robot
    //And to get the way we are facting with the robot
    public boolean init (){
        this.goForward();
        this.lastCoord = this.clientOk();

        if (this.lastCoord.errorFlag()){
            return false;
        }

        if (this.lastCoord.isFinal()){
            return true;
        }

        this.goForward();
        Coord newCoord = this.clientOk();

        if (newCoord.errorFlag()){
            return false;
        }

        this.facing = newCoord.getDirection(this.lastCoord, newCoord);
        this.lastCoord = newCoord;

        return true;
    }

    //A method used to navigate the robot to the [0,0] after the init method has been used

    /**
     * Out main goal is to get the robot to the point where both of the coordinates are equal [x,x], than we can
     * move him down the diagonal which is the shortest patha at that moment
     * Return true when the robot reaches [0,0]
     */
    public boolean navigator (){
        Navigator navigator = new Navigator(this.clientWriter, this.clientReader, this, this.suffix);

        if (navigator.navigateToDiagonal() == false|| navigator.navigateToEnd() == false){
            return false;
        }
        System.out.println("Reached destination: ");
        this.lastCoord.printCoord();
        return true;
    }

}
