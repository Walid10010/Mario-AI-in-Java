package RL_ALGO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import static RL_ALGO.Enum_State.*;

/**
 * Created by macbookpro on 08.06.18.
 */
public class main {
    public static void main(String [] args){
        ArrayList<Enum_State> enum_states = new ArrayList<>();
       // enum_states.add(MARIO_HORIZONTAL_0);//
        //enum_states.add(MARIO_HORIZONTAL_1); enum_states.add(MARIO_HORIZONTAL_2);
        //enum_states.add(MARIO_RIGHT_BOTTOM_HALF); enum_states.add(MARIO_RIGHT_UPPER_HALF);  enum_states.add(MARIO_SPRUNG);
        enum_states.add(MARIO_MODE);
       //
        //enum_states.add(MARIO_RECHTS); enum_states.add(MARIO_RECHTS_RECHTS); enum_states.add(MARIO_RECHTS_WEIT);
        enum_states.add(MARIO_SPRUNG);
        enum_states.add(MARIO_RIGHT_OBSTACLE);
        //enum_states.add(MARIO_LINK_UNTEN);enum_states.add(MARIO_LINKS_OBEN);
       // enum_states.add(MARIO_RECHT_OBEN); enum_states.add(MARIO_RECHTS_UNTEN);
        //enum_states.add(MARIO_HORIZONTAL_0);
        enum_states.add(MARIO_SMALL_VIEW);
        State state = new State(enum_states);
        int min = 0;
        int max = 1000000;
        int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
        Mario_Action_Learner mario_action_learner = new Mario_Action_Learner(new Mario_basic_environment(randomNum,
                false),
                2000,3000, state, new Q_Agent());
       HashMap<State,ArrayList<Action_value>> state_action_hashmap = mario_action_learner.training();
//       for ( State key: state_action_hashmap.keySet()){
//            System.out.println("New_State");
//            for (Action_value value: state_action_hashmap.get(key)){
//                System.out.println(",");
//                System.out.print(value.getValue());
//            }
//        }

    }
}
