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
public class State_Space_1 implements State_Space {
    private Environment environment;
    private ArrayList<Enum_State> enum_states;
    private Q_Agent q_agent;

    public State_Space_1(Environment environment, ArrayList<Enum_State> enum_states,Q_Agent q_agent){
        this.environment = environment;
        this.enum_states = enum_states;
        this.q_agent = q_agent;
    }

    @Override
    public State getState() {
        State new_state = new State(this.enum_states);
        ArrayList<Byte> state_arrayList = new_state.getArrayList();
        for (Enum_State enum_state: this.enum_states){
            state_arrayList.add(check(enum_state));}
        //System.out.println("STATE" + " " + new_state.toString());
        return  new_state;
    }

    @Override
    public byte matrix_reward(int x) {
        return 0;
    }

    private byte check(Enum_State state1){ //vorrübergehend void

        switch (state1){
            case MARIO_LINKS_LINKS: return (range_mario(- 2));
            case MARIO_LINKS: return (range_mario(- 1));
            case MARIO_RECHTS:return (range_mario(+ 1));
            case MARIO_RECHTS_RECHTS:return (range_mario(+ 2));
            case MARIO_RECHTS_WEIT: return (check_rechts_sicht(+ 2));
            case MARIO_SPRUNG: return (this.q_agent.jump());
            case MARIO_MODE: return (this.q_agent.mode());
            default:return -1; }
    }
    private byte range_mario(int x){
        int marioX = environment.getMarioEgoPos()[0];
        int marioY = environment.getMarioEgoPos()[1];
        float mario_posY = environment.getMarioFloatPos()[1];
        // environment.getEnemiesObservationZ(0);
        byte[][] scene = environment.getMergedObservationZZ(1, 1);
        for (int height = 0; height<scene.length; height ++) {
            byte check_creature = isCreature(scene[height][marioX +x], x);
            if (check_creature != 0) {
                return check_creature;
            }
        }
        return check_level(x);
    }

    private byte isCreature(int c, int x)
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

    private byte check_level(int x){
        int marioX = environment.getMarioEgoPos()[0];
        int marioY = environment.getMarioEgoPos()[1];
        int c = 0;//TODO!
        switch (c){
            case GeneralizerLevelScene.BORDER_CANNOT_PASS_THROUGH:
            case GeneralizerLevelScene.FLOWER_POT:
            case GeneralizerLevelScene.BRICK:
                return 6;}
        return 0;
    }
    private byte check_rechts_sicht(int x){
        int marioX = environment.getMarioEgoPos()[0];
        int marioY = environment.getMarioEgoPos()[1];
        byte check_creature = 0;
        byte[][] scene = environment.getMergedObservationZZ(1, 1);
        for (int height = 0; height<scene.length; height ++) {
            for (int rechts = marioX + x; rechts < 4; rechts++) {
                check_creature = isCreature(scene[height][rechts], x);
                if (check_creature != 0) {
                    return check_creature;}
            }
            for (int rechts = marioX + x; rechts < 4; rechts++) {
                check_creature = check_level(rechts);
                if (check_creature != 0) {
                    return check_creature;
                }
            }
        }
        return 0;
    }
}
