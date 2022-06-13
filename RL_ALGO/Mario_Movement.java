package RL_ALGO;

import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.mario.environments.MarioEnvironment;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by macbookpro on 11.06.18.
 */
public class Mario_Movement {
   public static enum Direction{
     RIGHT,LEFT,
        UP, DOWN,

    }
    public static enum Jump{
        //NO_JUMP,
        SMALL_JUMP,MIDDLE_JUMP,
        BIG_JUMP;
    }
   public static enum Speed {
        NO_SPEED, Y_SPEED
    }

    public void Action_set(HashMap<State,ArrayList<Action_value>> state_action_hashmap, State state){
        ArrayList<Action_value> action_values = new ArrayList<>();
        for (Direction direction: Direction.values()){
            for (Jump jump: Jump.values()){
                for (Speed speed: Speed.values()){
                    Action_value action_value = new Action_value(direction,jump,speed,0.1);
                    action_values.add(action_value);
                }}}
       // System.out.println("Web:"+ action_values.size());
         state_action_hashmap.put(state,action_values);}

     public void perform_Action(State s, MarioEnvironment environment, Action_value action_value){
        Jump jump = action_value.getJump();
        switch (jump){
           // case NO_JUMP:  perform_no_jump(s,environment,action_value); break;
            case SMALL_JUMP: perform_small_jump(s,environment,action_value); break;
            case MIDDLE_JUMP: perform_middle_jump(s,environment,action_value); break;
            case BIG_JUMP:    perform_big_jump(s,environment,action_value); break;
            default:
                System.out.println("CHECK_JUMP THERE MAY BE A PROBLEM");

        }}

     private void perform_no_jump (State s, MarioEnvironment environment, Action_value action_value){
         boolean [] action = new boolean[MarioEnvironment.numberOfKeys];
         direction_set(action,action_value.getDirection());
         speed_set(action,action_value.getSpeed());
         environment.performAction(action);
         environment.tick();
         //environment.tick();
     }
     private void perform_small_jump(State s, MarioEnvironment environment, Action_value action_value){
         boolean [] action = new boolean[MarioEnvironment.numberOfKeys];
         direction_set(action,action_value.getDirection());
         speed_set(action,action_value.getSpeed());
         int jump_counter = 0;
         action[3] = true;
         int status = environment.getMarioStatus();
     while (jump_counter<4 && status != Mario.STATUS_DEAD ){
         environment.performAction(action);
         environment.tick();
         jump_counter++;}
         if( status != Mario.STATUS_DEAD){
         action[3] = false;
         environment.performAction(action);
         environment.tick();}
     }

     private void perform_middle_jump(State s, MarioEnvironment environment, Action_value action_value){
         boolean [] action = new boolean[MarioEnvironment.numberOfKeys];
         direction_set(action,action_value.getDirection());
         speed_set(action,action_value.getSpeed());
         int jump_counter = 0;
         action[3] = true;
         int status = environment.getMarioStatus();
         while (jump_counter<8 && status != Mario.STATUS_DEAD){
             environment.performAction(action);
             environment.tick(); jump_counter++;}
         action[3] = false;
         environment.performAction(action);
         environment.tick();

     }
     private void perform_big_jump(State s, MarioEnvironment environment, Action_value action_value) {
         boolean[] action = new boolean[MarioEnvironment.numberOfKeys];
         direction_set(action, action_value.getDirection());
         speed_set(action, action_value.getSpeed());
         int jump_counter = 0;
         action[3] = true;
         int status = environment.getMarioStatus();
         while (jump_counter < 12 &&  status != Mario.STATUS_DEAD) {
             environment.performAction(action);
             environment.tick(); jump_counter++;
         }
         if ( status != Mario.STATUS_DEAD){
         action[3] = false;
         environment.performAction(action);
         environment.tick();}
         //System.out.println(environment.getMario().isOnGround());

     }
     private void direction_set(boolean[] actionset, Direction direction){
         switch (direction){
             case UP: actionset[5] = true; break;
             case DOWN: actionset[2] = true; break;
             case LEFT: actionset[0] = true; break;
             case RIGHT: actionset[1] = true;break;
         }}
     private void speed_set(boolean[] actionset, Speed speed) {
     switch (speed){
         case NO_SPEED: actionset[4] = false;break;
         case Y_SPEED: actionset[4] = true;break;
     }
     }
}
