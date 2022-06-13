package RL_ALGO;
/*


import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.engine.sprites.Sprite;package ch.idsia.agents.controllers;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.engine.sprites.Sprite;
//import ch.idsia.benchmark.mario.environments.Environment;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, sergey_at_idsia_dot_ch
 * Date: May 9, 2009
 * Time: 1:42:03 PM
 * Package: ch.idsia.agents.controllers
 */

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.engine.sprites.Sprite;

public class Q_Agent extends BasicMarioAIAgent implements Agent
{
    public Q_Agent()
    {
        super("Q_Learning_Agent");
    }

    int trueJumpCounter = 0;
    int trueSpeedCounter = 0;


    public boolean[] getAction()
    {
        int x = marioEgoRow;
        int y = marioEgoCol;


        return action;
    }
    public byte jump(){
       if( isMarioAbleToJump || !isMarioOnGround){
           return 1;
       }else {
           return 0;
       }
    }
    public byte mode(){

        return (byte) marioMode;
    }
    public byte[][] level() {
        return this.levelScene;
    }

    public byte[][] get_enemies(){
        return enemies;
    }
    public void reset()
    {
        action[Mario.KEY_RIGHT] = true;
//    action[Mario.KEY_SPEED] = true;
    }
}
