package RL_ALGO.Mario_Deep;

import org.apache.commons.lang3.ObjectUtils;
import org.nd4j.linalg.api.ndarray.INDArray;
/*
 * @author: Md. Rezaul Karim, 06/09/2018
 * 
 */

public class Replay {
	public INDArray Input;
	public int Action;
	public float Reward;
	public INDArray NextInput;
	public  String next_input;
	public int NextActionMask[] ;
	public String id;
	// Initialize Replay memory
	Replay(INDArray input , int action , float reward , INDArray nextInput){
		Input = input;
		Action = action;
		Reward = reward;
		NextInput = nextInput;
		if (this.NextInput == null){
			this.next_input =  "0";
		}else{
			this.next_input = this.NextInput +"";
		}
		id = this.Input + "," + this.Action;
	}
	@Override
	public int hashCode(){
		return this.id.hashCode();
	}
	@Override
	public boolean equals(Object b){
		Replay a = (Replay) b;

		boolean vgl = this.Input.equals(a.Input) && this.Action == a.Action ;
		//return this.Input.equals(a.Input);
		return vgl;
	}
	
}
