import java.awt.*;
import java.net.*;
import java.util.*;
import java.io.*;
import java.applet.*;


// ----------------------------------------------------------------------

class Tipper {
  // the cow tipper
  public static AudioClip noonieSound, tipsydaisySound,chachingSound, dieSound;
  public Cow cowList[]; // ref to list of cows NOTE: This must be set 

  Point loc; // location
  public boolean appear = false; // on screen or not
  
  Image backGroundImage; // ptr to the background image

  // value to subtract level from for appearTime computations
  private final int LEVEL_BASE = 10; 
  //  private final int APPEAR_TIME_LOG_FACTOR = 100; // factor to multiply by

  // multiplication factor
  private final int LEVEL_FACTOR = 60;

  // bonus for killing the tipper
  private final int KILL_TIPPER_BONUS = 500;

  private final int BASE_APPEAR_TIME = 30;  // add this to above
  int appearTime = 0;
  
  // movement
  private final int TIPPER_Y = 70;
  private final int TIPPER_START_X = 450;
  private final int TIPPER_GOAL_X = 0;
  public static int RUNAWAY_MULTIPLIER = 2; // multiply moveRate by this to run
  public int moveRate = INITIAL_MOVE_RATE;

  // walk in speeds
  public static int INITIAL_MOVE_RATE = 5;
  public static int WALK_IN_INCREASE = 1;
  public static int MAX_WALK_IN_RATE = 10;

  // chance that the tipper will run in instead
  private final int RUN_IN_RATE = 10;
  private final int RUN_IN_INCREASE = 1;
  private final int MAX_RUN_IN_RATE = 20;

  private final int RUN_IN_PERCENTAGE = 8; 
  private final int BASE_RUN_IN_PERCENTAGE = 0;
  private final int MAX_RUN_IN_PERCENTAGE = 85;

  // state variables
  public final int WALK_1 = 0;
  public final int WALK_2 = 1;
  public final int DIE_1 = 2;
  public final int DIE_2 = 3;
  public final int DIE_3 = 4;
  public final int DIE_4 = 5;
  public final int DIE_5 = 6;
  public int state = WALK_1; 

  // behavior
  private final int MONEY_STOLEN = 1000; // amount of money stolen
  private final int BASE_MONEY_STOLEN = 100; // amount of money stolen

  private final int TIPPING_COUNT_DOWN = 70; // amount of time flailing legs
  private final int SNEAKING_UP = 0; // sneaking up to do bad stuff
  private final int RUN_AWAY = 1; // run away!
  private final int TIPPING = 2; // tipping the cow
  private final int DYING = 3; // tipping the cow
  private int behavior = SNEAKING_UP;

  private final int TIP_PERCENTAGE = 5; // % x level of tipping a cow
  private final int BASE_TIP_PERCENTAGE = 25;
  private final int MAX_TIP_PERCENTAGE = 85; // max % chance of tipping the cow
  private final int MAX_PERCENTILE = 100;

  // animation  
  private final int TIPPER_X_OFFSET = 0;
  private final int TIPPER_Y_OFFSET = -10;
  private final int ANIM_CNT_DWN = 2; // intervals to wait to animate
  private int animCntDwn = ANIM_CNT_DWN;
  public static Image imageList[] = new Image[7];

  public void setCowList(Cow cwList[]) {
    // sets the cow list
    cowList = cwList;
  }

  public Tipper(Image imgList[], Image backGnd) {
    // instantiate the cow tipper
    loc = new Point(TIPPER_START_X, TIPPER_Y);
    imageList = imgList;
    backGroundImage = backGnd;
    // set up the initial time to appear for tipping
    reset();
  }

  public void handle() {
    if (appear) { 
      // if onscreen, then handle
      animCntDwn--;
      if (animCntDwn <= 0) {
	// check if tipping, if so, leave alone

	if (behavior == TIPPING) {
	  reset(); //finished tipping, now reset
	} 
	  
	// check if in midst of dying.  If so, do that instead
	if (behavior == DYING)
	  if (state >= DIE_1) {
	    ++state;
	    
	    // if finished death throes, then reset
	    if (state > DIE_5) {
	      // draw the bloody image onto the background image!
	      Graphics g = backGroundImage.getGraphics();
	      g.drawImage(imageList[DIE_5],loc.x,loc.y,null);
	      reset();
	    }
	  }
	
	if ((behavior == SNEAKING_UP) || (behavior == RUN_AWAY)){
	    // switch left and right and then move forward
	    if (state == WALK_1)
	      state = WALK_2; 
	    else
	      state = WALK_1;
	    loc.x -= moveRate;
	    
	    // now check to see if tipper has reached the left. If so
	    // then something bad happens
	    
	    if (loc.x <= TIPPER_GOAL_X) {
	      // now determine behavior
	      int tipChance = BASE_TIP_PERCENTAGE + Farm.level * TIP_PERCENTAGE;
	      if (tipChance > MAX_TIP_PERCENTAGE)
		tipChance = MAX_TIP_PERCENTAGE;
	      if (((int) (Math.random() * MAX_PERCENTILE)) <= tipChance) {
		// tipping.  Tip the cow over and then place self behind the
		// cow.  Gloat for TIPPING_COUNT_DOWN;
		for (int i=0;i<Farm.maxCowCount;i++) {
		  if (cowList[i].milkReady()){
		    // if cow is available (i.e. not dead), then tip.
		    cowList[i].tip();
		    animCntDwn = TIPPING_COUNT_DOWN;
		    behavior = TIPPING;  // reset after animCntDwn is finished
		    tipsydaisySound.play();
		    loc.x = cowList[i].loc.x + TIPPER_X_OFFSET;
		    loc.y = cowList[i].loc.y + TIPPER_Y_OFFSET;
		    break;
		  }
		}
	      }

	      // if tipping was chosen, then the behavior wouldn't be tipping, so 
	      // steal some money instead.
	      if (behavior != TIPPING) {
		// if no cows left to tip, then steal money instead
		behavior = RUN_AWAY;  // got munchie cash, now flee!
		moveRate = -(RUNAWAY_MULTIPLIER * moveRate);  // boogy outta there
		chachingSound.play();
		Farm.score -= BASE_MONEY_STOLEN + (int) (Math.random() * MONEY_STOLEN);
	      }
	    }
	    
	    if ((behavior == RUN_AWAY) && (loc.x >= TIPPER_START_X)) {
	      // bastard got away!
	      //	    System.out.println("got away!");
	      reset();
	    }
	}
      }
    } else {
      // not onscreen, determine if it is time to reappear
      appearTime--;
      //      System.out.println("aptime="+appearTime);

      // tipper appears when countdown is finished.  Still appears when no cows
      // to steal munchie money

      //      if ((appearTime <= 0) && (Farm.cowCount > 0)) {
      if (appearTime <= 0) {
	// appear the tipper, if the time allows it
	if (Sun.endTime() == false) {
	  noonieSound.play();
	  appear = true;
	  loc.x = TIPPER_START_X;
	}
      }
    }
  }

  public void draw(Graphics g) {
    // if onscreen, then draw
    if (appear) {
      //      System.out.println("state = "+state);
      g.drawImage(imageList[state],loc.x,loc.y,null); 
    }
  }

  public void reset() {
    // reset tipper for next iteration
    appear = false;
    /*    int levelFactor = (int) (LEVEL_BASE - 
			     Math.log(APPEAR_TIME_LOG_FACTOR *
				      (Farm.level + 1))); */
    int levelFactor = LEVEL_BASE - Farm.level ;
    if (levelFactor < 0)
      levelFactor = 0;
    appearTime = (int)(Math.random() * (levelFactor * LEVEL_FACTOR));
    appearTime += BASE_APPEAR_TIME;

    //    System.out.println("tipper appeartime = "+appearTime);
    behavior = SNEAKING_UP;

    // now determine new walk-in speed.  Tipper either walks in or runs in, 
    // up to the maximum speed for that mode (run or walk).
    int runChance = Farm.level * RUN_IN_PERCENTAGE + BASE_RUN_IN_PERCENTAGE;
    if (runChance > MAX_RUN_IN_PERCENTAGE)
      runChance = MAX_RUN_IN_PERCENTAGE;
    if (((int) (Math.random() * MAX_PERCENTILE)) <= runChance) {
      moveRate = RUN_IN_RATE;
      moveRate += Farm.level * RUN_IN_INCREASE;
      if (moveRate > MAX_RUN_IN_RATE)
	moveRate = MAX_RUN_IN_RATE;
    }
    else {
      moveRate = INITIAL_MOVE_RATE;
      moveRate += Farm.level * WALK_IN_INCREASE;
      if (moveRate > MAX_WALK_IN_RATE)
	moveRate = MAX_WALK_IN_RATE;
    }

    state = WALK_1;
    loc.x = TIPPER_START_X; loc.y = TIPPER_Y;
  }

  public void shootAt() {
    if (appear) {
      // if this is the 2nd shot, the 2nd shot aims true...
      // or if tipping, the shot aims true..
      if ((behavior == RUN_AWAY) && (state < DIE_1) ||
	  (behavior == TIPPING)){
	state = DIE_1;
	behavior = DYING;
	moveRate = 0;
	animCntDwn = ANIM_CNT_DWN;
	Farm.score += KILL_TIPPER_BONUS; // add bonus for killing the interloper
	dieSound.play();
      } else {
	if (state < DIE_1) {
	  // not dying, but shot at. Tipper's been shot at!  Flee!
	  behavior = RUN_AWAY;
	  moveRate = -(RUNAWAY_MULTIPLIER * moveRate);  // boogy outta there!
	}
      }
    }
  }

}
