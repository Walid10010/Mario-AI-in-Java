package RL_ALGO;

import ch.idsia.agents.controllers.human.HumanKeyboardAgent;
import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.MarioAIOptions;

/**
 * Created by macbookpro on 08.06.18.
 */
public class Mario_basic_environment implements Mario_ENV_ {
   private MarioAIOptions marioAIOptions;

    public Mario_basic_environment(int levelRandSeed, boolean visu){
        marioAIOptions = new MarioAIOptions("");
        marioAIOptions.setZLevelScene(1);
        marioAIOptions.setZLevelEnemies(1);
        marioAIOptions.setFlatLevel(false);
        marioAIOptions.setBlocksCount(true);
        marioAIOptions.setCoinsCount(true);
        marioAIOptions.setLevelRandSeed(levelRandSeed); // comment out for random levels
        marioAIOptions.setVisualization(visu); // false: no visualization => faster learning
        marioAIOptions.setGapsCount(false);
        //marioAIOptions.setLevelType(0);
        //marioAIOptions.setMarioInvulnerable(true);
        marioAIOptions.setMarioMode(0);
        //marioAIOptions.setFrozenCreatures(true);
        //marioAIOptions.setFrozenCreatures(false);
        marioAIOptions.setFPS(24);
        marioAIOptions.setLevelLength(150);
        marioAIOptions.setCannonsCount(false);
        marioAIOptions.setTimeLimit(200);
        marioAIOptions.setMarioInvulnerable(false);
        marioAIOptions.setDeadEndsCount(false);
       // marioAIOptions.setFlatLevel(true);
       // marioAIOptions.setLevelHeight(5);
       // marioAIOptions.setEnemies("gk,rk,rkw,gw");
        marioAIOptions.setTubesCount(true);
        marioAIOptions.setCannonsCount(true);
        marioAIOptions.setLevelDifficulty(0);


       // HumanKeyboardAgent basic = new HumanKeyboardAgent();
        //marioAIOptions.setAgent(basic);
        //basicTask.doEpisodes(1, false, 1000);


    }

    @Override
    public MarioAIOptions getOptions() {
        return this.marioAIOptions;
    }
}
