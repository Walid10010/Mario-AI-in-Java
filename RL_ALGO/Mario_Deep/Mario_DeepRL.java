package RL_ALGO.Mario_Deep;

import RL_ALGO.*;


import ch.idsia.benchmark.mario.engine.GeneralizerLevelScene;
import ch.idsia.benchmark.mario.engine.sprites.Sprite;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.mario.environments.MarioEnvironment;
import ch.idsia.benchmark.tasks.BasicTask;
import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.util.ArrayUtil;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import RL_ALGO.Mario_Deep.Deep_action_values;


/**
 * Created by macbookpro on 08.06.18.
 */
public class Mario_DeepRL{
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
    DeepQNetwork RLNet;
    int size = 4;
    HashMap<Integer,Action_value> action_valueIntegerHashMap;


    public Mario_DeepRL(Mario_ENV_ baisc_env, int epsiode, int step, State state, Q_Agent agent){
        this.basic_env = baisc_env;
        this.epsiode = epsiode;
        this.step = step;
        this.state = state;
        this.q_agent = agent;
        environment.reset(this.basic_env.getOptions());
        this.q_agent.integrateObservation(environment);
        this.state_action_hashmap = new HashMap();
        this.q2 = new HashMap();
        this.select_hashmap = new ArrayList();
        this.select_hashmap.add(this.state_action_hashmap);
        this.select_hashmap.add(this.q2);
        Deep_action_values deep_values = new  Deep_action_values();
        action_valueIntegerHashMap = deep_values.getAction_valueIntegerHashMap();
        this.networkConstruction();
        this.start_write();
    }

    public HashMap<State,ArrayList<Action_value>> training(){
        ArrayList<Float> length = new ArrayList<>();
        int erfolg = 0;
        for (int epi = 0; epi<this.epsiode; epi++){
            this.anzahl_rand = 0;
            if (epi>100){
                this.RLNet.Epsilon = 0;
               // this.basic_env.getOptions().setVisualization(true);
                //this.basic_env.getOptions().setLevelRandSeed(300);

            }
            if(erfolg>5){
                this.basic_env.getOptions().setVisualization(true);
            }
            //this.basic_env.getOptions().setLevelRandSeed(300);
                // }
            environment.reset(this.basic_env.getOptions());
            this.q_agent.integrateObservation(environment);
            ArrayList<Integer> save_float = new ArrayList<>();
            ArrayList<INDArray> save_ind = new ArrayList<>();
            ///System.out.println("Level"+ environment.getLevel().randomSeed);
            inner_loop:
            for (int ste =0 ; ste<this.step;ste++){
                float mario_posX = environment.getMarioFloatPos()[0];
                State new_state = one_step();
                double before_reward = environment.getIntermediateReward();
                check_hashkey(new_state);

                ArrayList<Byte> tmp_array = new_state.getArrayList();
                //System.out.println("Len" + tmp_array.size());
                //System.out.println("Feed" + new_state.toString());
                float [] feedforward  = new float[tmp_array.size()+1];
                feedforward[0] = 0;
                for (int float_index = 1 ; float_index<tmp_array.size()+1;float_index++){
                    feedforward[float_index] = tmp_array.get(float_index-1);
                }
                float[] flat = ArrayUtil.flattenFloatArray(feedforward);
                INDArray row_to_flat = Nd4j.create(flat);
                int number_action = this.action_valueIntegerHashMap.keySet().size();
                int a = RLNet.getAction(row_to_flat,number_action);
                //RLNet.LastInput = row_to_flat;
                //RLNet.LastAction = a;
                //System.out.println("Feed" + row_to_flat);
                //System.out.println("Last Action" + a);
                save_float.add(a);
                save_ind.add(row_to_flat);
                Action_value deep_value = this.action_valueIntegerHashMap.get(a);
                this.movement.perform_Action(new_state,environment,deep_value);
                /*** NEW STATE START **/

                State update_state = one_step();
                ArrayList<Byte> update_array = update_state.getArrayList();
                for (int float_index = 1; float_index<tmp_array.size()+1;float_index++){
                    feedforward[float_index] = update_array.get(float_index-1);
                }
                float[] up_dateflat = ArrayUtil.flattenFloatArray(feedforward);
                INDArray  up_date_row_to_flat = Nd4j.create(up_dateflat);
                up_date_row_to_flat = RLNet.set_action_max(up_date_row_to_flat,number_action);
                before_reward = environment.getIntermediateReward()-before_reward > 0 ? 0.5:-0.;
                byte feind = this.state_space.matrix_reward(6);
                double inter = 0;

                if (feind==1) {
                    double reward = environment.getMarioFloatPos()[0] - mario_posX > 0 ? 5.45 : -5.1;
                    reward += environment.getMarioFloatPos()[0] - mario_posX == 0 ? 5.3 : -5.1;
                    //reward +=before_reward;
                    inter = reward;

                }else{
                    double reward = 0;
                    reward += environment.getMarioFloatPos()[0] - mario_posX >0 ? 5.3 : -5.1;
                    //reward +=before_reward;
                    inter = reward;
                }

                int status = MarioEnvironment.getInstance().getMario().getStatus();
                if(status != MarioEnvironment.MARIO_STATUS_DEAD){
                    //System.out.println("Reward" + inter);
                RLNet.observeReward((float) inter,up_date_row_to_flat,false);}
                //System.out.println("State" +  new_state.toString());
                //System.out.println("LastAction" + a);

                if (status == MarioEnvironment.MARIO_STATUS_DEAD || environment.getTimeLeft()<10 ) {
                   // RLNet.ReplayMemory.remove(RLNet.ReplayMemory.size()-1);
                    length.add(environment.getMarioFloatPos()[0]);
                    //RLNet.LastAction = save_float.get(save_float.size()-1);
                    //RLNet.LastInput = save_ind.get(save_float.size()-1);
                      //RLNet.ReplayMemory.get(RLNet.ReplayMemory.size()-1).NextInput = null;
                      //RLNet.ReplayMemory.get(RLNet.ReplayMemory.size()-1).Reward = -5;
                      RLNet.observeReward((float) -5,row_to_flat,false);
                    // RLNet.observeReward((float) -5,up_date_row_to_flat,false);

                     RLNet.observeReward((float) -5,null,true);
                     erfolg = 0;
                    System.out.println("Death");
                    System.out.println("step" + ste);
                   // RLNet.observeReward((float) -5,up_date_row_to_flat,true);

                    break inner_loop;

                }else if (status == MarioEnvironment.MARIO_STATUS_WIN){
                    System.out.println("WIN");
                    erfolg +=1;
                    length.add(environment.getMarioFloatPos()[0]);
                   // RLNet.observeReward((float) +1,null,true);
                    break inner_loop;
                }
            }
            System.out.println("reward  " + environment.getMarioFloatPos()[0] );
            System.out.println("EPISODIE" + epi);
            System.out.println("Rand" + this.anzahl_rand);
            System.out.println("Zustand" + this.select_hashmap.get(0).size());
            this.writer.println(environment.getMarioFloatPos()[0]);

        }//EPISODE
        System.out.println((length));
        this.writer.close();
        return this.state_action_hashmap;
    }
    private State one_step(){
        State new_state = new State(this.state.getState());
        ArrayList<Byte> state_arrayList = new_state.getArrayList();
        State_Space space_1 = new State_Space_3(environment,this.state.getState(),this.q_agent);
        state_space = space_1;
        return space_1.getState();
    }
    private void check_hashkey(State s){
        ArrayList<Action_value> pair = this.select_hashmap.get(0).get(s);
        if (pair != null){
            //search_max(s);
        }else{
            this.init_action_value_pair(s);

        }
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
    void networkConstruction() {
        int InputLength = 195;
        int number_action = this.action_valueIntegerHashMap.keySet().size();
        int HiddenLayerCount = 250;
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(12345)    //Random number generator seed for improved repeatability. Optional.
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .weightInit(WeightInit.XAVIER)
                .updater(new Adam())
                .l2(0.00) // l2 regularization on all layers
                .list()
                .layer(0, new DenseLayer.Builder()
                        .nIn(InputLength)
                        .nOut(HiddenLayerCount)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.RELU)
                        .build())
                .layer(1, new DenseLayer.Builder()
                        .nIn(HiddenLayerCount)
                        .nOut(HiddenLayerCount)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.RELU)
                        .build())
                .layer(2, new DenseLayer.Builder()
                        .nIn(HiddenLayerCount)
                        .nOut(HiddenLayerCount)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.RELU)
                        .build())
                .layer(3,new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .nIn(HiddenLayerCount)
                        .nOut(1) // for 4 possible actions
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.IDENTITY)
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .pretrain(false).backprop(true).build();
        System.out.println("number" + number_action);
        RLNet = new DeepQNetwork(conf, 500, .99f, 0.4, 500, 20, 102, InputLength, number_action);
    }

}
