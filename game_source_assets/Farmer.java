import java.awt.*;
import java.net.*;
import java.util.*;
import java.io.*;
import java.applet.*;

// ----------------------------------------------------------------------
class Farmer {
  // state information
  Point loc;  // location
  public int locale; // id # of the location slot occupied

  public Image imageList[] = new Image[6]; // ptr to images used
  public static final int STAND = 0;
  public static final int MILK1 = 1;
  public static final int MILK2 = 2;
  public static final int DUMP = 3;
  public static final int RAISE_GUN = 4;
  public static final int FIRE_GUN = 5;

  private final int FIRE_GUN_DELAY = 20; // delay after firing the gun
  public final int FULL_BUCKET_BONUS = 100; // bonus for full bucket

  public int state = STAND;

  private final int FARMER_X_OFFSET = 10;
  private final int FARMER_Y_OFFSET = 20;

  public final int MAX_MILK = 300;  // max amount of milk able to collect
  int milkCollected = 0;             // milk collected so far
  private final int MILK_COLLECT_RATE = 10;
  public int milkCollectRate = MILK_COLLECT_RATE;  // milk collected per squeezing

  private final int ANIM_COUNT_DOWN = 5;
  int animCntDwn = ANIM_COUNT_DOWN;   // used to handle animations

  // delay time for after firing the gun- farmer'll freeze
  public final int GUN_DELAY = 5;
  public int delayTime = 0;

  public Farmer(int startpt, Image imgList[]) {
    loc = Farm.getLocale(startpt);
    state = STAND;
    imageList = imgList;
  }

  public void reset() {
    milkCollected = 0;
    locale = 0;
    state = STAND;
  }

  public final void handle() {
    // handles animations, etc... 

    // dec the delay time (if any)
    if (delayTime > 0)
      delayTime--;

    // on gun slot?
    if (locale == Farm.GUN_SLOT) {
      if (state == FIRE_GUN) {
	// in the midst of firing, animCntDwn to finish firing
	animCntDwn--;
	if (animCntDwn <= 0)
	  state = RAISE_GUN;
      } else
	state = RAISE_GUN;
    } else {
    
      /*      // if not standing, then animating
	      animCntDwn--;
	      if (animCntDwn <= 0) {
	      animCntDwn = ANIM_COUNT_DOWN;
	      if (state == MILK1)
	      state = MILK2;
	      else
	  if (state == MILK2)
	  state = MILK1;
	  }
      */
    }
  }

  public final void draw(Graphics g) {
    loc = Farm.getLocale(locale);
    g.drawImage(imageList[state],loc.x + FARMER_X_OFFSET,
		loc.y + FARMER_Y_OFFSET,null);
  }

  public final void moveUp() {
    if (delayTime <= 0) {
      locale = Farm.moveUp(locale);    
      loc = Farm.getLocale(locale);
      state = Farmer.STAND;
    }
  }

  public final void moveDown() {
    if (delayTime <= 0) {
      locale = Farm.moveDown(locale);    
      loc = Farm.getLocale(locale);
      state = Farmer.STAND;
    }
  }

  public final void moveLeft() {
    if (delayTime <= 0) {
      locale = Farm.moveLeft(locale);    
      loc = Farm.getLocale(locale);
      state = Farmer.STAND;
    }
  }

  public final void moveRight() {
    if (delayTime <= 0) {
      locale = Farm.moveRight(locale);    
      loc = Farm.getLocale(locale);
      state = Farmer.STAND;
    }
  }

  public final void fireGun() {
      animCntDwn = ANIM_COUNT_DOWN;
      state = FIRE_GUN;
  }

  public final void milk() {
    // initiates milking
    if (state == MILK2)
      state = MILK1;
    else 
      state = MILK2;
    //    animCntDwn = ANIM_COUNT_DOWN;
  }
  
  public int addMilk(int amount) {
    // adds milk to sum and returns amount not able to carry (if bucket is full)
    int temp = milkCollected + amount;
    int runOff = 0;
    if (temp > MAX_MILK) {
      runOff = temp - MAX_MILK;
      temp = MAX_MILK;
    }
    milkCollected = temp;
    //    System.out.println("milk so far="+milkCollected+" runoff="+runOff);
    return runOff;
  }
  
  public void dumpMilk() {
    // unload milk into repository
    state = DUMP;
    milkCollected = 0;
  }


}
