package Activity;

import dataStruct.Facing;
import dataStruct.Quadrant;

import java.io.BufferedReader;
import java.io.PrintWriter;

/**
 * A navigator class where the navigation of the robot to [0,0] takes place
 */
public class Navigator {

    private PrintWriter clientWriter;
    private BufferedReader clientReader;
    private Mover mover;
    private String suffix;

    public Navigator(PrintWriter clientWriter, BufferedReader clientReader, Mover mover, String suffix) {
        this.clientWriter = clientWriter;
        this.clientReader = clientReader;
        this.mover = mover;
        this.suffix = suffix;
    }

    public boolean checkBlock (){
        if (this.mover.lastCoord.equals(this.mover.prevCoord)){
            return true;
        }
        return false;
    }

    public Facing getFirstDirection (){
        if (this.mover.quadrant.equals(Quadrant.SECOND) || this.mover.quadrant.equals(Quadrant.FIRST)){
            return Facing.DOWN;
        }
        else {
            return Facing.UP;
        }
    }

    public Facing getSecondDirection (){
        if (this.mover.quadrant.equals(Quadrant.SECOND) || this.mover.quadrant.equals(Quadrant.FOURTH)){
            return Facing.LEFT;
        }
        else {
            return Facing.RIGHT;
        }
    }

    //First, second -> down, Third and fourth -> up
    public boolean quadrantFirstMove (){
        if (this.mover.quadrant.equals(Quadrant.SECOND) || this.mover.quadrant.equals(Quadrant.FIRST)){
            return this.mover.moveDown();
        }
        else {
            return this.mover.moveUP();
        }
    }

    //First third -> right, Second, fourth -> left
    public boolean quadrantSecondMove (){
        if (this.mover.quadrant.equals(Quadrant.SECOND) || this.mover.quadrant.equals(Quadrant.FOURTH)){
            return this.mover.moveLeft();
        }
        else {
            return this.mover.moveRight();
        }
    }

    public void dodgeBlock (){
        if (this.mover.facing.equals(this.getSecondDirection())){
            this.quadrantFirstMove();
            this.quadrantSecondMove();
            this.quadrantSecondMove();
        }
        else if (this.mover.facing.equals(this.getFirstDirection())){
            this.quadrantSecondMove();
            this.quadrantFirstMove();
            this.quadrantFirstMove();
        }
    }

    //Method that navigates the robot to the diagonal
    public boolean navigateToDiagonal (){

        System.out.println("quadrant: " + this.mover.quadrant);

        while (this.mover.lastCoord.areEqual() == false){
            this.mover.quadrant = this.mover.lastCoord.getQuadrant();

            if (this.checkBlock()){
                this.dodgeBlock();
            }

            //This part only works for the first quadrant
            if (Math.abs(this.mover.lastCoord.getX()) > Math.abs(this.mover.lastCoord.getY())){
                if (this.quadrantSecondMove() == false){
                    this.clientWriter.print("301 SYNTAX ERROR" + this.suffix);
                    this.clientWriter.flush();
                    return false;
                }
            }
            //Y  > X
            else {
                if (this.quadrantFirstMove() == false){
                    this.clientWriter.print("301 SYNTAX ERROR" + this.suffix);
                    this.clientWriter.flush();
                    return false;
                }
            }

        }
        return true;
    }

    //A method that navigates robot to the [0:0] on the diagonal
    public boolean navigateToEnd(){
        while(this.mover.lastCoord.isFinal() == false){
            this.mover.quadrant = this.mover.lastCoord.getQuadrant();

            if (this.checkBlock()){
                this.dodgeBlock();
            }

            //this.mover.lastCoord.printCoord();
            if (quadrantFirstMove() == false){
                this.clientWriter.print("301 SYNTAX ERROR" + this.suffix);
                this.clientWriter.flush();
                return false;
            }

            if (this.mover.lastCoord.isFinal()){
                break;
            }

            if (this.quadrantSecondMove() == false){
                this.clientWriter.print("301 SYNTAX ERROR" + this.suffix);
                this.clientWriter.flush();
                return false;
            }
        }
        return true;
    }

}
