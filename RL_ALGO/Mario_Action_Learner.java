package RL_ALGO;
import ch.idsia.benchmark.mario.engine.GeneralizerLevelScene;
import ch.idsia.benchmark.mario.engine.sprites.Sprite;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.mario.environments.MarioEnvironment;
import ch.idsia.benchmark.tasks.BasicTask;
import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by macbookpro on 08.06.18.
 */
public class Mario_Action_Learner {
    private static MarioEnvironment environment = MarioEnvironment.getInstance();
    private int epsiode;
    private int step;
    private int zustande;
    private Mario_ENV_ basic_env;
    private State state;
    private Q_Agent q_agent;
    private State start;
    private HashMap<State,ArrayList<Action_value>> state_action_hashmap;
    private HashMap<State,ArrayList<Action_value>> q2;
    private ArrayList<HashMap<State,ArrayList<Action_value>>> select_hashmap;
    private Mario_Movement movement = new Mario_Movement();
    private State_Space state_space;
    private int anzahl_rand = 0;
    private PrintWriter writer;


    public Mario_Action_Learner(Mario_ENV_ baisc_env, int epsiode, int step, State state, Q_Agent agent){
        this.basic_env = baisc_env;
        this.epsiode = epsiode;
        this.step = step;
        this.state = state;
        this.q_agent = agent;
        environment.reset(this.basic_env.getOptions());
        this.q_agent.integrateObservation(environment);
        this.state_action_hashmap = new HashMap<>();
        this.q2 = new HashMap<>();
        this.select_hashmap = new ArrayList<>();
        this.select_hashmap.add(this.state_action_hashmap);
        this.select_hashmap.add(this.q2);
        this.start_write();
    }

    public HashMap<State,ArrayList<Action_value>> training(){
        ArrayList<Float> length = new ArrayList<>();
        double alpha= 1;
        boolean vi = false;
        double epsilon = 0.00;
        int erfolgreich = 0;
        String action_vorher = "";
        double ende = 0;
        boolean visual_ =false;
        for (int epi = 0; epi<this.epsiode; epi++){
            this.anzahl_rand = 0;
            int count   = 0;
            ArrayList<String> past_actions = new ArrayList<>();
            ArrayList<Action_value> past_values = new ArrayList<>();
            int level = this.basic_env.getOptions().getLevelRandSeed();
            //if (epi>990){
            //    this.basic_env.getOptions().setVisualization(true);}
            //this.basic_env.getOptions().setLevelRandSeed(level+1);
            // this.basic_env.getOptions().setVisualization(visual_);
            if(erfolgreich>5){
                this.basic_env.getOptions().setVisualization(true);
                //this.writer.close();
               // epsilon = 0;
               // alpha = 0;
            }
            environment.reset(this.basic_env.getOptions());
            this.q_agent.integrateObservation(environment);
            //epsilon = epsilon<0.05 ? 0.05: epi%6000 == 0? epsilon/2:epsilon;
            inner_loop:
            for (int ste =0 ; ste<this.step;ste++){
                 //Collections.shuffle(this.select_hashmap);
                 float mario_posX = environment.getMarioFloatPos()[0];
                  int mode = environment.getMarioMode();
                 State new_state = one_step();
                // System.out.println("State1" + new_state.toString());
                 double before_reward = environment.getIntermediateReward();
                 //System.out.println("STATE; " +" " + new_state.toString());
                 check_hashkey(new_state);
                ArrayList<Action_value> action_set  = epsilon_greedy(epsilon,new_state);
                 Action_value value = action_set.get(0);
                 past_values.add(value);
                 Action_value search_zero = search_if_zero(new_state);
                 if(search_zero!=null){
                        //value = search_zero;
                 }
                 ArrayList<Action_value> tmp = this.state_action_hashmap.get(new_state);
                 this.movement.perform_Action(new_state,environment,value);
                 //Update_RULE
                visual_ = action_vorher.equals(value.toString())?true:false;
                past_actions.add(value.toString());
                 State update_state = one_step();
                //System.out.println("State2:" + update_state.toString());

                ArrayList<Action_value> max_set = search_max(update_state);
                Action_value max = max_set.get(0);
                before_reward = environment.getIntermediateReward()-before_reward > 0 ? 0.5:0;
                double inter = 0;
                byte feind = this.state_space.matrix_reward(6);
                double alpha_chosen = alpha/Math.pow(value.getCounter(),0.9);
                alpha_chosen = 0.1;
                double gamma = 0.6;
                if (feind==1) {
                    double reward = environment.getMarioFloatPos()[0] - mario_posX > 0 ? 0.25 : 0;
                    reward += environment.getMarioFloatPos()[0] - mario_posX == 0 ? 0.1 : 0;
                    //reward +=0.1;
                    double add = value.getValue() < 0 ? value.getValue() : value.getValue();
                    inter = alpha_chosen * (reward + before_reward + gamma * max.getValue() - add);
                }else{
                   // System.out.println("Feind");
                    double reward = 0;
                    reward += environment.getMarioFloatPos()[0] - mario_posX >0 ? 0.25 : 0;
                    double add = value.getValue() < 0 ? +value.getValue() : value.getValue();
                    inter = alpha_chosen * (reward +before_reward + gamma * max.getValue() - add);
                }
                int status = MarioEnvironment.getInstance().getMario().getStatus();
                if (status != MarioEnvironment.MARIO_STATUS_DEAD){
                 value.setValue(inter);}
                //if (environment.getTimeLeft()<10){
                 //   value.setValue(-inter -0.25);

           // }
                //System.out.println("Value" + value.getValue());
               // System.out.println("Value" + value.getValue());//System.out.println("status"+ status);
                 if (status == MarioEnvironment.MARIO_STATUS_DEAD ) {

                     length.add(environment.getMarioFloatPos()[0]);
                     boolean time_out = environment.getTimeLeft()<10 ? true: false;
//                     System.out.println("Time_out" + time_out);
                     action_vorher = past_actions.get(past_actions.size()-2);
  //                   System.out.println("Action1" + past_actions.get(past_actions.size()-2));
   //                  System.out.println("Action2" + past_actions.get(past_actions.size()-1));

                     if (erfolgreich<5){
                     past_values.get(past_values.size()-2).setValue(-5);
                     //value.setValue(-inter);
                     //past_values.get(past_values.size()-1).setValue(-inter);

                         // value.death();
                     }
                     //value.death();
                     erfolgreich = 0;
                     break inner_loop;
                }else if (status == MarioEnvironment.MARIO_STATUS_WIN){
                     length.add(environment.getMarioFloatPos()[0]);
                     vi = true;
                     erfolgreich +=1;
                     epsilon = 0.00;
                     ende +=1;
                     //value.setValue(0.1);
                     break inner_loop;
                 }
                // value.setValue(0.1);
            }
            System.out.println("reward  " + environment.getMarioFloatPos()[0] );
            System.out.println("EPISODIE" + epi);
            System.out.println("Rand" + this.anzahl_rand);
            System.out.println("Zustand" + this.select_hashmap.get(0).size());
            System.out.println("ende"+ ende/epi);
            writer.println(environment.getMarioFloatPos()[0] );
           // writer.println("EPISODIE" + epi);
            //writer.println("Zustand" + this.select_hashmap.get(0).size());


        }//EPISODE
        System.out.println((length));
        writer.close();
        return this.state_action_hashmap;
    }
   private State one_step(){
       State new_state = new State(this.state.getState());
       ArrayList<Byte> state_arrayList = new_state.getArrayList();
       State_Space space_1 = new State_Space_3(environment,this.state.getState(),this.q_agent);
       state_space = space_1;
       return space_1.getState();
   }
   private ArrayList<Action_value> epsilon_greedy(double epislon, State s){
       int min = 0;
       int max = 1000;
       int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
       double vgl = ((double)randomNum)/(max +1.0);
       boolean entscheidung = epislon > vgl;
       if (entscheidung){
           this.anzahl_rand +=1;
          return random_action(s);
       }else
         return search_max(s);
   }
   private Action_value search_if_zero(State s){
       for (Action_value value :this.select_hashmap.get(0).get(s))    {
           if (value.getValue() == 0){
                 return value;
           }
       }
       return null;
   }
   private void check_hashkey(State s){
    ArrayList<Action_value> pair = this.select_hashmap.get(0).get(s);
    if (pair != null){
       //search_max(s);
    }else{
        this.init_action_value_pair(s);

    }
   }
   private ArrayList<Action_value> random_action(State s){
       ArrayList<Action_value> values =  this.select_hashmap.get(0).get(s);
       if (values ==null){
            init_action_value_pair(s);}
       Collections.shuffle(values);
       return values;
   }
   private ArrayList<Action_value> search_max(State s){
       ArrayList<Action_value> values =  this.select_hashmap.get(0).get(s);
      if (values == null ){
          init_action_value_pair(s);}
       values =  this.select_hashmap.get(0).get(s);
       Collections.sort(values);
       return values;
   }
   private ArrayList<Action_value> search_update_max(State s){
       ArrayList<Action_value> values =  this.select_hashmap.get(1).get(s);
       if (values == null ){
            init_action_q2(s);}

        values = this.select_hashmap.get(1).get(s);
        Collections.sort(values);
        return values;

   }
   private void init_action_q2(State s){
           movement.Action_set(this.select_hashmap.get(1),s);
   }
   private void init_action_value_pair(State s){
    movement.Action_set(this.select_hashmap.get(0),s);
   }

    private void start_write(){
        try {
            this.writer = new PrintWriter("score.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    //TODO!
    private void save_hashQFunction(){
    /* boolean[] index = {true,false};
     ArrayList<Action_value> action_values = new ArrayList<>();
                       for(int i = 0; i <4;i++){
                        for (boolean jump: index){
                        for (boolean e: index) {

                               boolean[]  action_set = new boolean[environment.numberOfKeys];
                               action_set[0]= i == 0 ? true:false;
                               action_set[1]= i == 1 ? true:false;
                               action_set[2] = i == 2 ? true:false;
                               action_set[3] = jump;
                               action_set[4] = false;
                               action_set[5] =i == 3 ? true:false;
                               action_values.add(new Action_value(action_set,0.0));}}
                       }
             this.state_action_hashmap.put(s,action_values);*/
    }
}
