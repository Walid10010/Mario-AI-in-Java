package RL_ALGO;

import ch.idsia.benchmark.mario.engine.GeneralizerLevelScene;
import ch.idsia.benchmark.mario.engine.sprites.Sprite;
import ch.idsia.benchmark.mario.environments.Environment;
import com.sun.corba.se.spi.orbutil.fsm.*;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by macbookpro on 09.06.18.
 */
public class State_Space_2 implements State_Space {
    private Environment environment;
    private ArrayList<Enum_State> enum_states;
    private Q_Agent q_agent;
    private int marioX;
    private int marioY;
    private byte [][] scene;

    public State_Space_2(Environment environment, ArrayList<Enum_State> enum_states,Q_Agent q_agent){
        this.environment = environment;
        this.enum_states = enum_states;
        this.q_agent = q_agent;
    }
    @Override
    public byte matrix_reward(int x){
        return 0;
    }
    @Override
    public State getState() {
        State new_state = new State(this.enum_states);
        ArrayList<Byte> state_arrayList = new_state.getArrayList();
        this.marioX = this.environment.getMarioEgoPos()[0];
        this.marioY = this.environment.getMarioEgoPos()[1];
        this.scene = this.environment.getMergedObservationZZ(1,1);
        for (Enum_State enum_state: this.enum_states){
            state_arrayList.add(check(enum_state));}
        //System.out.println("STATE" + " " + new_state.toString());
        return  new_state;
    }
    private byte check(Enum_State state1){
        switch (state1){
            case MARIO_HORIZONTAL_0: return mario_horizontal(0);
            case MARIO_HORIZONTAL_1: return mario_horizontal(1);
            case MARIO_HORIZONTAL_2:return mario_horizontal(2);
            case MARIO_RIGHT_UPPER_HALF:return right_upper();
            case MARIO_RIGHT_BOTTOM_HALF: return right_bottom();
            case MARIO_SPRUNG: return (this.q_agent.jump());
            case MARIO_MODE: return (this.q_agent.mode());
            case MARIO_RIGHT_OBSTACLE: return right_obstacle();
            case MARIO_RECHTS_RECHTS: return rechts(+2);
            case MARIO_RECHTS_WEIT: return rechts(3);
            case MARIO_RECHTS: return rechts(1);
            default:return -1; }
    }
    private byte right_obstacle(){
        for (int x = marioX ; x<marioX +3;x++){
            int aktuell = this.scene[marioY][x];
            byte check = check_level(aktuell);
            if (check != 0){
                return check;
            }
        }
        return 0;
    }
    private byte right_upper(){
        for ( int height=0; height<this.marioY;height++)
        for (int x = marioX ; x<marioX +3;x++){
            int aktuell = this.scene[height][x];
            byte check = isCreature(aktuell);
            if (check != 0){
                return check;
            }
        }
        return 0;

    }
    private byte rechts(int x) {
        for (int y= 0; y<scene.length;y++){
           byte check = isCreature(scene[y][marioX+x]);
           if (check!=0){
               return check;
           }
        }
        return 0;
    }

    private byte right_bottom(){
        for ( int height=this.marioY+1; height<this.scene.length;height++)
            for (int x = marioX ; x<marioX +3;x++){
                int aktuell = this.scene[height][x];
                byte check = isCreature(aktuell);
                if (check != 0){
                    return check;
                }
            }
        return 0;


    }
    private byte mario_horizontal(int y){
        int height = this.marioY +y;
        for (int x = marioX -2; x<marioX +4;x++){
            int aktuell = this.scene[height][x];
            byte check = isCreature(aktuell);
            if (check != 0){
                return check;
            }
        }
        return 0;
    }
    private byte isCreature(int c)
    {
        switch (c)
        {
            case Sprite.KIND_GOOMBA:
            case Sprite.KIND_RED_KOOPA:
            case Sprite.KIND_GREEN_KOOPA:
                return 1;
            case  Sprite.KIND_GOOMBA_WINGED:
            case  Sprite.KIND_SPIKY_WINGED:
            case   Sprite.KIND_RED_KOOPA_WINGED:
                return 2;
            case Sprite.KIND_BULLET_BILL:
                return 3;
            case Sprite.KIND_ENEMY_FLOWER:
                return 4;
            case Sprite.KIND_FIRE_FLOWER:
                return 5;
        }
        return 0;
    }
    private byte spedd(int y){
        return 0;
    }
    private byte check_level(int c){
        switch (c){
            case GeneralizerLevelScene.BORDER_CANNOT_PASS_THROUGH:
            case GeneralizerLevelScene.FLOWER_POT:
            case GeneralizerLevelScene.BRICK:
                return 1;}
        return 0;
    }

}
