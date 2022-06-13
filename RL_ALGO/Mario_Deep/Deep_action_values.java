package RL_ALGO.Mario_Deep;

import RL_ALGO.Action_value;
import RL_ALGO.Mario_Movement;

import java.util.HashMap;

public class Deep_action_values {
    private HashMap<Integer,Action_value>  action_valueIntegerHashMap = new HashMap();

    public Deep_action_values(){
        int index = 0;
        for (Mario_Movement.Direction direction: Mario_Movement.Direction.values()){
            for (Mario_Movement.Jump jump: Mario_Movement.Jump.values()){
                for (Mario_Movement.Speed speed: Mario_Movement.Speed.values()){
                    Action_value action_value = new Action_value(direction,jump,speed,0.1);
                    action_valueIntegerHashMap.put(index,action_value);
                    index++;
                }}}
    }
    public HashMap<Integer,Action_value> getAction_valueIntegerHashMap(){
        return this.action_valueIntegerHashMap;
    }

}
