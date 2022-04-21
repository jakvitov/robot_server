package dataStruct;

public enum Facing {
    UP,
    RIGHT,
    DOWN,
    LEFT;

    private static Facing[] values = values();

    public Facing next (){
        return values[(this.ordinal()+1) % values.length];
    }

    public Facing prev (){
        return values[(this.ordinal()-1) % values.length];
    }
}
