package RL_ALGO;

import java.util.ArrayList;

/**
 * Created by macbookpro on 08.06.18.
 */
public class State {
    private ArrayList<Enum_State> state;
    private ArrayList<Byte> state_value = new ArrayList<>();
    private String id;
   // public State(){}
    public State(ArrayList<Enum_State> state){
        this.state = state;
    }

    public ArrayList<Byte> getArrayList(){
        return this.state_value;
    }

    public ArrayList<Enum_State> getState() {
        return state;
    }


    @Override
    public int hashCode(){
        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object o){
        State obj = (State) o ;
        return this.id.equals( obj.id);
    }
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (Byte s : this.getArrayList())
        {
            sb.append(s);
            sb.append(",");
        }
        this.id = sb.toString();
        return id;
    }
}
