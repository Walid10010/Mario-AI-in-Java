package RL_ALGO;

/**
 * Created by macbookpro on 09.06.18.
 */
public interface State_Space {
    public State getState();
    public byte matrix_reward(int x);
}
