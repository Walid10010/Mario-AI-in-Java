package RL_ALGO;

import ch.idsia.benchmark.mario.engine.GeneralizerLevelScene;
import ch.idsia.benchmark.mario.engine.sprites.Sprite;
import ch.idsia.benchmark.mario.environments.Environment;
import com.sun.corba.se.spi.orbutil.fsm.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by macbookpro on 09.06.18.
 */
public class State_Space_3 implements State_Space {
    private Environment environment;
    private ArrayList<Enum_State> enum_states;
    private Q_Agent q_agent;
    private int marioX;
    private int marioY;
    private byte [][] scene;
    private int episode;

    public State_Space_3(Environment environment, ArrayList<Enum_State> enum_states,Q_Agent q_agent){
        this.environment = environment;
        this.enum_states = enum_states;
        this.q_agent = q_agent;
    }

    public void setEpisode(int episode) {
        this.episode = episode;
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
        if (this.enum_states.contains(Enum_State.MARIO_SMALL_VIEW)){
            state_arrayList.addAll(matrix_view(8));
        }
        new_state.toString();
        return  new_state;
    }


    private byte check(Enum_State state1){
        switch (state1){
            case MARIO_HORIZONTAL_0: return mario_horizontal(0);
            case MARIO_HORIZONTAL_1: return mario_horizontal(1);
            case MARIO_HORIZONTAL_2:return mario_horizontal(2);
            case MARIO_RIGHT_UPPER_HALF:return right_upper();
            case MARIO_RIGHT_BOTTOM_HALF: return right_bottom();
            case MARIO_SPRUNG: return environment.isMarioOnGround()?(byte)0:(byte)1;
            case MARIO_MODE: return (byte) environment.getMarioMode();
            case MARIO_RIGHT_OBSTACLE: return right_obstacle();
            case MARIO_RECHTS_RECHTS: return rechts(+2);
            case MARIO_RECHTS_WEIT: return rechts(3);
            case MARIO_RECHTS: return rechts(1);
            case MARIO_LINKS: return rechts(-2);
            case MARIO_LINKS_LINKS:  return  rechts(-3);
            case MARIO_LINK_UNTEN: return sicht(-1,0,0,3);
            case MARIO_LINKS_OBEN: return sicht(-1,0,0,-3);
            case MARIO_RECHT_OBEN: return sicht(0,0,6,-3);
            case MARIO_RECHTS_UNTEN: return sicht(0,0,6,3);

            default:return -1; }
    }


    public byte sicht(int vor_x,int vor_y,int nach_x, int nach_y) {
        byte boden = 0;
        byte oben = 0;
        for ( int x = marioX+vor_x; x< marioX +nach_x; x++){
            for ( int y = marioY+vor_y; y<marioY +nach_y;y++){
                byte check = this.environment.getEnemiesObservationZ(2)[y][x];
                  switch (check){
                      case Sprite.KIND_GOOMBA:
                      case Sprite.KIND_RED_KOOPA:
                      case Sprite.KIND_GREEN_KOOPA:
                          boden = 1;break;
                      case  Sprite.KIND_GOOMBA_WINGED:
                      case   Sprite.KIND_RED_KOOPA_WINGED:
                      case  Sprite.KIND_GREEN_KOOPA_WINGED:
                          oben =2; break;
                      default:

                  }
                   }}
        return  (byte) (boden + oben);
    }
    public byte matrix_reward(int shift){
        State new_state = new State(this.enum_states);
        ArrayList<Byte> state_arrayList = new_state.getArrayList();
        for ( int x = marioX-1; x< marioX +shift; x++){
            for ( int y = marioY-3; y<marioY +shift;y++){
                byte check = this.environment.getEnemiesObservationZ(1)[y][x];
                if (check != 0){
                    return 1;}
            }}
        //System.out.println("MatrixView" + state_arrayList);
        return 0;
    }

    private ArrayList<Byte> matrix_view(int shift){
        State new_state = new State(this.enum_states);
        ArrayList<Byte> state_arrayList = new_state.getArrayList();
        for ( int x = marioX-2; x< marioX +shift+2; x++){
            for ( int y = marioY-shift; y<marioY +shift;y++){
                byte check = this.environment.getEnemiesObservationZ(2)[y][x];
                if (check == 0){
                    //check = this.environment.getLevelSceneObservationZ(1)[y][x];
                    //check = check == 0? 0: (byte)(check +2);
                }
               // state_arrayList.add(check);
                state_arrayList.add(check);

            }}
       //  System.out.println("MatrixView" + state_arrayList);
       // System.out.println("State" +(state_arrayList));
        return state_arrayList;
    }
    private byte right_obstacle(){
        for (int x = marioX ; x<marioX +3;x++){
            int aktuell = this.environment.getLevelSceneObservationZ(1)[marioY][x];
            byte check = check_level(aktuell);
            if (check != 0){
                return 1;
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
            case   Sprite.KIND_RED_KOOPA_WINGED:
            case  Sprite.KIND_GREEN_KOOPA_WINGED:
                return 2;
             case  Sprite.KIND_SPIKY_WINGED:
                 return 10;

           /* case  Sprite.KIND_WAVE_GOOMBA:
                return 10;
            case Sprite.KIND_GREEN_MUSHROOM:
                return 11;*/
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
                return 7;
            case GeneralizerLevelScene.FLOWER_POT:
                return 8;
            case GeneralizerLevelScene.BRICK:
                return 9;
    }
        return 0;
    }

}
