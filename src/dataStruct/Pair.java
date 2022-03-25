package dataStruct;
//A simple data class to represent a pair
public class Pair {
    private int first;
    private int second;
    public Pair(int first,int second){
        this.first = first;
        this.second = second;
    }
    public int getServerKey(){
        return this.first;
    }
    public int getClientKey(){
        return this.second;
    }
}
