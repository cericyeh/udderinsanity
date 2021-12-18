import java.awt.*;
import java.net.*;
import java.util.*;
import java.io.*;
import java.applet.*;

// ----------------------------------------------------------------------


class Cow {
  // state info
  Point loc; // location
  int locale; // # id of location slot occupied

  public static final int MAX_COW_IMAGES = 28;
  public static Image imageList[] = new Image[MAX_COW_IMAGES];

  public int milk = 0;  // amount of milk built up
  public final int MAX_MILK = 200; // max amount of milk before explosion
  public final static int MILK_FAIRY_CEILING = 175; // max amount of milk the milk fairy can
                                             // shove into the cow

  public static final int STRESS_LIMIT = 12; // stress limit before cow bursts
  public static final int STRESS_INCREMENT = 7; // inc for each stressed
  public int stress = 0;   // amount of stress on cow


  public static final int COW_DEATH = 5; //state at which cow dies
  public static final int COW_FLIP_TIME = 4; // time for flipping cow over to be tipped
  public static final int COW_TIPPED = 6; // cow tipped
  private final int TIPPED_TIME_TO_DEATH = 100; // time before cow is considered dead.


  // drawstates
  public static final int COW_MAD = 15; // draw state for cow is mad
  public static final int COW_MAD_DEATH = 16; // draw state cow is dead from stress  
  public static final int COW_MAD_DEATH_END = 20; // last frame
  public static final int COW_MILK_DEATH_END = 14;
  public static final int COW_START_TIP = 21;
  public static final int COW_FLIP_1 = 24;
  public static final int COW_FLIP_2 = 25;
  public static final int COW_FLIP_DEATH_START = 26;
  public static final int COW_FLIP_DEATH_END = 27;

  public int state = 0;  // state cow is in.  2*state + tailWag == image
  public int drawState = 0; // actual ID to draw

  public int tailWag = 0; // secondary state

  private final int ANIM_CNT_DWN_POP = 8; // time cow waits after stressed
  public int animCntDwn = (MAX_MILK - milk)/4;
  public int soundCntDwn = (MAX_MILK - milk)/4;

  private static final int COUNT_DOWN_RANGE = 50; // range over which countdown can span
  private static final int INITIAL_COUNT_DOWN = 40; // initial time inc to milk++
  private final int MIN_COUNT_DOWN = 15;

  // set this to be the min_count_down initially, to make gameplay more interesting
  // in the earlier levels
  private final int EARLY_LEVEL_MIN_COUNT_DOWN = 8;
  private final int EARLY_LEVEL_LIMIT = 3;

  private static final int INITIAL_MILK_GAIN_RATE = 5;
  private static final int MAX_MILK_GAIN_RATE = 15;
  private static final int MILK_GAIN_LEVEL_FACTOR = 4;
  private static final int MILK_MAD_LEVEL = 20; // psycho level
  private static final int MILK_MAD_FACTOR = 5;
  private int milkGainRate;  // rate at which milk is gained


  public static int milkCntDwn = INITIAL_COUNT_DOWN; // max value for time inc
  public static int maxMilkCntDwn = INITIAL_COUNT_DOWN; // max value for time inc
  public int cntDwn = INITIAL_COUNT_DOWN; // time inc to milk++

  private final int COW_X_OFFSET = -30;
  private final int COW_Y_OFFSET = 0;

  public static AudioClip longMoo, quickMoo, fastMoo, burstMoo,popSound,annoyedMoo; // refs to main sounds

  public Cow(int startpt, Image imgList[]) {
    loc = Farm.getLocale(startpt);
    imageList = imgList;
    tailWag = (int)(Math.random() * 2);
    milk = (int) (Math.random() * (MAX_MILK/5));
    animCntDwn = (int)(Math.random() * (MAX_MILK - milk)/4);
    soundCntDwn = (int)(Math.random() * (MAX_MILK - milk)/4);
    //    maxMilkCntDwn = (int) (Math.random() * COUNT_DOWN_RANGE);
    //    maxMilkCntDwn += MIN_COUNT_DOWN;

    if (Farm.level <= EARLY_LEVEL_LIMIT)
      maxMilkCntDwn = EARLY_LEVEL_MIN_COUNT_DOWN;
    else
      maxMilkCntDwn = MIN_COUNT_DOWN;
    setMilkCntDwn();
    int levelFactor = Farm.level - MILK_GAIN_LEVEL_FACTOR;
    if (levelFactor < 0) 
      levelFactor = 0;
    milkGainRate = levelFactor + INITIAL_MILK_GAIN_RATE;
    if (milkGainRate > MAX_MILK_GAIN_RATE)
      milkGainRate = MAX_MILK_GAIN_RATE;
    milkGainRate = (int) (Math.random() * milkGainRate);
    if (milkGainRate == 0)
      milkGainRate = 1;
    if (Farm.level >= MILK_MAD_LEVEL)
      milkGainRate += MILK_MAD_FACTOR;

    //        System.out.println("milk gain rate="+milkGainRate+" cntdwn="+milkCntDwn);
  }

  public boolean milkReady() {
    // returns true if cow can be milked (i.e. still alive), else false
    return ((state != COW_DEATH) && (state != COW_TIPPED));
  }

  public void setMilkCntDwn() {
    // sets milk gain speed & rate based on level
    milkCntDwn = maxMilkCntDwn;
    //    milkCntDwn = (int)(Math.random() * INITIAL_COUNT_DOWN) + 1;    
    //    milkGainRate = (int)(Math.random() * MILK_GAIN_RATE) + 1;
    //    milk = (int) (Math.random() * (MAX_MILK/25));
  }


  public final void handle() {
    if (state == COW_TIPPED) {
      // cow tipped dynamics
      if (drawState < COW_FLIP_1) {
	animCntDwn--;
	if (animCntDwn <= 0) {
	  // animating the falling
	  drawState++;
	  if (drawState == COW_FLIP_1) 
	    animCntDwn = TIPPED_TIME_TO_DEATH; // now set the time for the cow to flail
	  else
	    animCntDwn = COW_FLIP_TIME;
	}
      } else {
	// handle the flailing, flailing for animCntDwn iterations
	animCntDwn--;
	if (animCntDwn <= 0) {
	  // perform death animation
	  state = COW_DEATH;
	  Farm.cowCount--; // decrement the # of live cows
	  Farm.decCowsLeft();
	  drawState = COW_FLIP_DEATH_START;
	  animCntDwn = 3; // A MAGIC NUMBER!!! woooo
	} else {
	  // wag arms and legs in desperation
	  if (drawState == COW_FLIP_1)
	    drawState = COW_FLIP_2;
	  else 
	  if (drawState == COW_FLIP_2)
	    drawState = COW_FLIP_1;
	}
      }
    } else
	if (state >= COW_DEATH) {
      // dead cow      
      if ((drawState != COW_MILK_DEATH_END) &&
	  (drawState != COW_MAD_DEATH_END) &&
	  (drawState != MAX_COW_IMAGES - 1) &&
	   (drawState != COW_FLIP_DEATH_END)){
	animCntDwn--;
	if (animCntDwn <= 0) {
	  // place pop here so that it'll 
	  if (drawState == COW_MAD_DEATH) {
	    popSound.play();
	    Farm.cowCount--; // decrement the # of live cows	    
	    Farm.decCowsLeft();
	  }
	  drawState++;
	  animCntDwn = ANIM_CNT_DWN_POP;
	    if (drawState >= MAX_COW_IMAGES) 
	      drawState = MAX_COW_IMAGES - 1;
	  }
      }
    } else {
      // cow is still alive
      cntDwn--;      
      if (cntDwn <= 0) {
	if (milk <= 5)
	  setMilkCntDwn(); 
	
	// if not endtime, then increment milk
	if (Sun.endTime() == false)
	  milk += milkGainRate;
	cntDwn = milkCntDwn;
      }
      
      animCntDwn--;
      if (animCntDwn <=0) {
	if (tailWag == 0)
	  tailWag = 1;
	else
	  tailWag = 0;
	animCntDwn = (MAX_MILK - milk)/2;
      }
      
      // now handle stress
      if (stress > 0) {
	stress--;
	drawState = COW_MAD;
      }  else
      drawState = 2*state + tailWag;

      state = milk/40;
      
      // check for death, if so, play death sound
      if (state == COW_DEATH) {
	burstMoo.play();
	Farm.cowCount--; // decrement the # of live cows
	Farm.decCowsLeft();
      } else {
	// handle sounds
	soundCntDwn--;
	if (soundCntDwn <= 0) {
	  if (state <=1)
	    longMoo.play();  // moo when tail wags
	  else
	    if (state <= 3)
	      quickMoo.play(); // desperate
	    else 
	      fastMoo.play(); // really desperate
	  soundCntDwn = (MAX_MILK - milk)/2;
	  if (soundCntDwn < 30)
	    soundCntDwn = 30;
	}
      }
    }
  }

  public final void stress() {
    stress+= STRESS_INCREMENT;
    if (stress > STRESS_LIMIT) {
      // pop that cow!
      animCntDwn = 3;
      state = COW_DEATH;
      drawState = COW_MAD_DEATH;
    } else
      annoyedMoo.play();  // stress that cow!
      drawState = COW_MAD; 
  }

  public final void tip() {
    // if cow ain't dead, or already tipped, then tip
    if (state < COW_DEATH) {
      // tip over the cow
      //      System.out.println("tip!");
      //      drawState = COW_FLIP_1;
      drawState = COW_START_TIP;
      animCntDwn = COW_FLIP_TIME;
      state = COW_TIPPED;
    }
  }

  public final void milkFairy(int amount) {
    // milk fairy effect: adds amount to the current milk sum, but making sure that
    // the milk does not overflow
    //    System.out.println("milkfairy amount="+amount);
    milk += amount;
    if (milk > MILK_FAIRY_CEILING)
      milk = MILK_FAIRY_CEILING;
  }

  public final void draw(Graphics g) {
    try {
      g.drawImage(imageList[drawState],loc.x + COW_X_OFFSET,
		  loc.y + COW_Y_OFFSET,null);
    } catch (NullPointerException e) {
    }
  }

}
