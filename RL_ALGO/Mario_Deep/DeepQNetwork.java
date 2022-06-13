package RL_ALGO.Mario_Deep;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.*;
/*
 * @author: Md. Rezaul Karim, 06/09/2018
 * 
 */

public class DeepQNetwork {
	int ReplayMemoryCapacity;
	List<Replay> ReplayMemory;
	List<Replay> recent_replay;
	double Epsilon;
	float Discount;
	Set<Replay> already_seen = new HashSet<>();
	MultiLayerNetwork DeepQ;
	MultiLayerNetwork TargetDeepQ;
	
	int BatchSize;
	int UpdateFreq;
	int UpdateCounter;
	int ReplayStartSize;
	Random r;
	
	int InputLength;
	int NumActions;
	
	INDArray LastInput;
	int LastAction;
	
	DeepQNetwork(MultiLayerConfiguration conf, int replayMemoryCapacity, float discount, double epsilon, int batchSize, int updateFreq, int replayStartSize, int inputLength, int numActions){
		DeepQ = new MultiLayerNetwork(conf);
		DeepQ.init();
		
		TargetDeepQ = new MultiLayerNetwork(conf);
		TargetDeepQ.init();
		
		TargetDeepQ.setParams(DeepQ.params());
		ReplayMemoryCapacity = replayMemoryCapacity;
		
		Epsilon = epsilon;
		Discount = discount;
		
		r = new Random();
		BatchSize = batchSize;
		UpdateFreq = updateFreq;
		UpdateCounter = 0;
		recent_replay = new ArrayList<>();
		ReplayMemory = new ArrayList<Replay>();
		ReplayStartSize = replayStartSize;
		InputLength = inputLength;
		NumActions = numActions;
	}
	
	void SetEpsilon(double e){
		Epsilon = e;
	}
	
// We first run our agent to collect enough transitions to fill up the replay memory, without training. For example, our memory may be of size 10,000.
//Then at every step, agent will obtain a transition and we add this to the end of the memory, and pop off the earliest one. 
//Then sample a mini batch of experiences from the memory randomly, and update our Q function on that, similar to mini-batch gradient descent. 
	void addReplay(float reward , INDArray NextInput){
		if( ReplayMemory.size() >= ReplayMemoryCapacity ){
			//System.out.println("remove");
			//ReplayMemory.remove(0);
		}
        Replay current_replay = new Replay(LastInput , LastAction , reward , NextInput);
		if (!already_seen.contains(current_replay) || reward == -5){
		 already_seen.add(current_replay);
		 if (reward==-5){
		 recent_replay.add(current_replay);}
			//System.out.println("ID" + current_replay.id);
		 ReplayMemory.add(new Replay(LastInput , LastAction , reward , NextInput));}
		 else{
			//System.out.println("seen");
			}
	}
	
	Replay[] getMiniBatch(int BatchSize){
		int size = ReplayMemory.size() < BatchSize ? ReplayMemory.size() : ReplayMemory.size() ;
		Replay[] retVal = new Replay[size];
		
		for(int i = 0 ; i < size ; i++){
			retVal[i] = ReplayMemory.get(i);
		}
		return retVal;
		
	}
	Replay[] mini_2(int BatchSize){
		int size = recent_replay.size() < BatchSize ? recent_replay.size() : recent_replay.size() ;
		Replay[] retVal = new Replay[size];

		for(int i = 0 ; i < size ; i++){
			retVal[i] = recent_replay.get(i);
		}
		return retVal;

	}


	INDArray set_action_max(INDArray Inputs ,int numActions){
		Inputs.putScalar(0,0);
		INDArray outputs = DeepQ.output(Inputs);
		int set_index = 0;
		/** MAX Action **/
		for(int i= 1; i<numActions;i++){
			Inputs.putScalar(0,i);
			INDArray output_next_action = DeepQ.output(Inputs);
			if(output_next_action.getFloat(0)>outputs.getFloat(0)){
				outputs = output_next_action;
				set_index = i;
			} }

		Inputs.putScalar(0,set_index);
		return Inputs;

	}
	int getAction(INDArray Inputs,int numActions){

		Inputs.putScalar(0,0);
		INDArray outputs = DeepQ.output(Inputs);
		LastInput = Inputs;
		LastAction = 0;
		//System.out.println("Epsion" + this.Epsilon);
	//	System.out.println("Action" + 0);
//		System.out.println("Output" + outputs);
		if(Epsilon> r.nextDouble()){
			//System.out.println("Random");
			int set_index = r.nextInt(numActions);
			Inputs.putScalar(0,set_index);
			LastInput = Inputs;
			LastAction = set_index;

		}else {
			/** MAX Action **/
			for (int i = 1; i < numActions; i++) {
				Inputs.putScalar(0, i);
				INDArray output_next_action = DeepQ.output(Inputs);
//				System.out.println("Action" + i);
//				System.out.println("Output" + output_next_action);
//				System.out.println("Output_Index" + output_next_action.getFloat(0));
				if (output_next_action.getFloat(0) > outputs.getFloat(0)) {
					outputs = output_next_action;
					LastInput = Inputs;
					LastAction = i;
				}
			}
			Inputs.putScalar(0, LastAction);
		}
		return LastAction;
	}
	
	void observeReward(float Reward , INDArray NextInputs,boolean finish){
		//if(!finish)
		addReplay(Reward , NextInputs);
		if(finish){
			UpdateCounter++;
			//train_2(100);
			//reconcileNetworks();
			//recent_replay = new ArrayList<>();
		}
		//System.out.println("ReplaySize"+ ReplayMemory.size());
		if(UpdateCounter==UpdateFreq)
			networkTraining(BatchSize);
		if(UpdateCounter==UpdateFreq) {
			UpdateCounter = 0;
			System.out.println("Reconciling Networks");
			reconcileNetworks();
		}
	}
	
	INDArray combineInputs(Replay replays[]){
		INDArray retVal = Nd4j.create(replays.length , InputLength);
		for(int i = 0; i < replays.length ; i++){
			retVal.putRow(i, replays[i].Input);
		}
		return retVal;
	}
	
	INDArray combineNextInputs(Replay replays[]){
		INDArray retVal = Nd4j.create(replays.length , InputLength);
		for(int i = 0; i < replays.length ; i++){
			if(replays[i].NextInput != null)
				retVal.putRow(i, replays[i].NextInput);
		}
		return retVal;
	}
	void train_2(int BatchSize){
		Replay replays[] = mini_2(BatchSize);
		INDArray CurrInputs = combineInputs(replays);
		INDArray TargetInputs = combineNextInputs(replays);

		//deep Q fix
		INDArray CurrOutputs = DeepQ.output(CurrInputs);
		INDArray TargetOutputs = TargetDeepQ.output(TargetInputs);
		//System.out.println("vorher" + CurrInputs);
		//
		// System.out.println("Target" + TargetOutputs);
		float y[] = new float[replays.length];
		//System.out.println("REplaylen" + replays.length);
		for(int i = 0 ; i < y.length ; i++){
			//System.out.println("Action_i" +  replays[i].Action );
			int ind[] = { i };
			//for (int jj: ind){
			//System.out.println("ind" +jj);}
			float FutureReward = 0 ;
			if(replays[i].NextInput != null)
				FutureReward = TargetOutputs.getFloat(i);
			//System.out.println("Future" + replays[i].Reward);
			//System.out.println("Action" + replays[i].Action);
			//	System.out.println("future" + TargetOutputs.getFloat(i));
			float TargetReward = replays[i].Reward + Discount * FutureReward;
			//System.out.println("Voher" + CurrOutputs);
			CurrOutputs.putScalar(ind , TargetReward ) ;
			//System.out.println("Voher" + CurrOutputs);

		}
		//System.out.println("Avgerage Error: " + (TotalError / y.length) );
		/*System.out.println("Input.Shape" + CurrInputs.shapeInfoToString());
		System.out.println("TargetInput.Shape" + TargetInputs.shapeInfoToString());
		System.out.println("Curr_out" + CurrOutputs.shapeInfoToString());
		System.out.println("Target_out" + TargetOutputs.shapeInfoToString());
		System.out.println("Current" + CurrOutputs);*/
		//System.out.println("CurrentOutPut" + CurrOutputs);
		//System.out.println("CurrentInput" + CurrInputs);
		for (int i= 0; i< 150; i++){
			DeepQ.fit(CurrInputs, CurrOutputs);
			//INDArray nach = DeepQ.output(CurrInputs);
			//System.out.println("Current" + nach);

		}

		//System.out.println("Nach.Shape" + nach.shapeInfoToString());

		//ReplayMemory  = new ArrayList<>();
	}
	void networkTraining(int BatchSize){
		Replay replays[] = getMiniBatch(BatchSize);
		INDArray CurrInputs = combineInputs(replays);
		INDArray TargetInputs = combineNextInputs(replays);

		//deep Q fix
		INDArray CurrOutputs = DeepQ.output(CurrInputs);
		INDArray TargetOutputs = TargetDeepQ.output(TargetInputs);
		//System.out.println("vorher" + CurrInputs);
		//
		// System.out.println("Target" + TargetOutputs);
		float y[] = new float[replays.length];
		//System.out.println("REplaylen" + replays.length);
		for(int i = 0 ; i < y.length ; i++){
			//System.out.println("Action_i" +  replays[i].Action );
			int ind[] = { i };
			//for (int jj: ind){
			//System.out.println("ind" +jj);}
			float FutureReward = 0 ;
			if(replays[i].NextInput != null)
				FutureReward = TargetOutputs.getFloat(i);
			   //System.out.println("Future" + replays[i].Reward);
			   //System.out.println("Action" + replays[i].Action);
			//	System.out.println("future" + TargetOutputs.getFloat(i));
			float TargetReward = replays[i].Reward + Discount * FutureReward;
			//System.out.println("Voher" + CurrOutputs);
			CurrOutputs.putScalar(ind , TargetReward ) ;
			//System.out.println("Voher" + CurrOutputs);

		}
		//System.out.println("Avgerage Error: " + (TotalError / y.length) );
		/*System.out.println("Input.Shape" + CurrInputs.shapeInfoToString());
		System.out.println("TargetInput.Shape" + TargetInputs.shapeInfoToString());
		System.out.println("Curr_out" + CurrOutputs.shapeInfoToString());
		System.out.println("Target_out" + TargetOutputs.shapeInfoToString());
		System.out.println("Current" + CurrOutputs);*/
		//System.out.println("CurrentOutPut" + CurrOutputs);
		//System.out.println("CurrentInput" + CurrInputs);
		for (int i= 0; i< 150; i++){
			DeepQ.fit(CurrInputs, CurrOutputs);
			INDArray nach = DeepQ.output(CurrInputs);
			//System.out.println("Current" + nach);

		}

		//System.out.println("Nach.Shape" + nach.shapeInfoToString());

		//ReplayMemory  = new ArrayList<>();
	}
	
	void reconcileNetworks(){

		TargetDeepQ.setParams(DeepQ.params());
	}
	/*
	public boolean saveNetwork(String ParamFileName , String JSONFileName){
	    //Write the network parameters for later use:
	    try(DataOutputStream dos = new DataOutputStream(Files.newOutputStream(Paths.get(ParamFileName)))){
	        Nd4j.write(DeepQ.params(),dos);
	    } catch (IOException e) {
	    	System.out.println("Failed to write params");
			return false;
		}
	    
	    //Write the network configuration:
	    try {
			FileUtils.write(new File(JSONFileName), DeepQ.getLayerWiseConfigurations().toJson());
		} catch (IOException e) {
			System.out.println("Failed to write json");
			return false;
		}
	    return true;
	}
	
	public boolean restoreNetwork(String ParamFileName , String JSONFileName){
		//Load network configuration from disk:
	    MultiLayerConfiguration confFromJson;
		try {
			confFromJson = MultiLayerConfiguration.fromJson(FileUtils.readFileToString(new File(JSONFileName)));
		} catch (IOException e1) {
			System.out.println("Failed to load json");
			return false;
		}

	    //Load parameters from disk:
	    INDArray newParams;
	    try(DataInputStream dis = new DataInputStream(new FileInputStream(ParamFileName))){
	        newParams = Nd4j.read(dis);
	    } catch (FileNotFoundException e) {
	    	System.out.println("Failed to load parems");
			return false;
		} catch (IOException e) {
	    	System.out.println("Failed to load parems");
			return false;
		}
	    //Create a MultiLayerNetwork from the saved configuration and parameters 
	    DeepQ = new MultiLayerNetwork(confFromJson); 
	    DeepQ.init(); 
	    
	    DeepQ.setParameters(newParams); 
	    reconcileNetworks();
	    return true;	    
	}*/
}
