package dataStruct;

import java.util.Objects;

//A simple data class to represent a pair
public class Pair {
    private int first;
    private int second;
    public Pair(int first,int second){
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair pair = (Pair) o;
        return first == pair.first && second == pair.second;
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    public int getServerKey(){
        return this.first;
    }
    public int getClientKey(){
        return this.second;
    }
}
