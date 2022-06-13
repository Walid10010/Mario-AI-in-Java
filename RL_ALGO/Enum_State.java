package RL_ALGO;

/**
 * Created by macbookpro on 08.06.18.
 */
public enum  Enum_State {
   /**********   Space_STATE_1 ! *****/
    MARIO_LINKS_LINKS,
    MARIO_LINKS,
    MARIO_RECHTS,
    MARIO_RECHTS_RECHTS,
    MARIO_RECHTS_WEIT,
    MARIO_SPRUNG,
    MARIO_MODE,
    MARIO_RECHTS_WEITSICHT,


  /**********    Space_STATE_2 ! ******/
  MARIO_HORIZONTAL_0, MARIO_HORIZONTAL_1,  MARIO_HORIZONTAL_2,
  MARIO_RIGHT_UPPER_HALF, MARIO_RIGHT_BOTTOM_HALF,
  MARIO_RIGHT_OBSTACLE,
  /*** Matrix View ****/
  MARIO_SMALL_VIEW,

  /**** Generalizierung ****/

  MARIO_LINKS_OBEN, MARIO_LINK_UNTEN,
  MARIO_RECHT_OBEN,MARIO_RECHTS_UNTEN




}
