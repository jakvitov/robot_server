package dataStruct;

import java.util.Objects;

/**
 * A basic data structure representing coordinates in the two dimensional linear
 * coordination Cartesian system
 */

public class Coord {

    private Integer x;
    private Integer y;
    private Integer errorFlag = -111111;

    public Coord (Integer x, Integer y){
        this.x = x;
        this.y = y;
    }

    public Integer getX (){
        return this.x;
    }

    public Integer getY (){
        return this.y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coord coord = (Coord) o;
        return Objects.equals(x, coord.x) && Objects.equals(y, coord.y);
    }

    //Return true if the coordinates include false flag
    public boolean errorFlag (){
        if (this.x.equals(this.errorFlag) && this.y.equals(this.errorFlag)) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean areEqual (){
        if (x.equals(y)){
            return true;
        }
        else {
            return false;
        }
    }

    public void printCoord (){
        System.out.println("X: "+ this.x + " Y: " + this.y);
    }
}
