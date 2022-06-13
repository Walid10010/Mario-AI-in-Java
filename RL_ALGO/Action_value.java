package RL_ALGO;

import ch.idsia.benchmark.mario.environments.MarioEnvironment;

/**
 * Created by macbookpro on 08.06.18.
 */
public class Action_value  implements Comparable{
    private boolean[] action_set;
    private double value;
    private Mario_Movement.Direction direction;
    private Mario_Movement.Jump jump;
    private Mario_Movement.Speed speed;
    private double counter = 1;

    public Action_value(boolean[] action_set, double value) {
        this.action_set = action_set;
        this.value = value;

    }
    public Action_value(Mario_Movement.Direction direction, Mario_Movement.Jump jump, Mario_Movement.Speed speed, double value){
        this.direction = direction;
        this.jump = jump;
        this.speed = speed;
        this.value = value;

    }

    public Mario_Movement.Direction getDirection() {
        return direction;
    }

    public Mario_Movement.Jump getJump() {
        return jump;
    }

    public Mario_Movement.Speed getSpeed() {
        return speed;
    }

    public double getValue() {
        return value;
    }

    public boolean[] getAction_name() {
        return action_set;
    }

    public void setValue(double value) {
        this.value += value;

    }

    public double getCounter() {
        this.counter += 1;
        return counter;
    }

    public void death(){
        this.value =0;
       // this.value -= 0.3;
    }
    @Override
    public String toString(){
        return this.direction +"," + this.jump +"," +this.speed;
    }
    @Override
    public int compareTo(Object o) {
        Action_value obj = (Action_value) o;
        return -Double.compare((this.value), (obj.value));
    }

}
