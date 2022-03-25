package dataStruct;

import java.security.KeyPair;
import java.util.ArrayList;

public class AutKeys {
    ArrayList<Pair> list = new ArrayList<Pair>();
    public AutKeys(){
        //Fill the array with the keys
        list.add(new Pair(23019, 32037));
        list.add(new Pair(32037, 29295));
        list.add(new Pair(18789, 13603));
        list.add(new Pair(16443, 29533));
        list.add(new Pair(18189, 21952));
    }
    public Pair returnKeys(int index){
        return list.get(index);
    }
}
