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

    //Method that navigates the robot to the diagonal
    public boolean navigateToDiagonal (){
        while (this.mover.lastCoord.areEqual() == false){
            //This part only works for the first quadrant
            if (this.mover.lastCoord.getX() > this.mover.lastCoord.getY()){
                if (this.mover.moveLeft() == false){
                    this.clientWriter.print("301 SYNTAX ERROR" + this.suffix);
                    this.clientWriter.flush();
                    return false;
                }
            }
            //Y  > X
            else {
                if (this.mover.moveDown() == false){
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
            if (this.mover.moveDown() == false){
                this.clientWriter.print("301 SYNTAX ERROR" + this.suffix);
                this.clientWriter.flush();
                return false;
            }
            if (this.mover.moveLeft() == false){
                this.clientWriter.print("301 SYNTAX ERROR" + this.suffix);
                this.clientWriter.flush();
                return false;
            }
        }
        return true;
    }

}
