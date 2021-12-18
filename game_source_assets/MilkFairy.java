import java.awt.*;
import java.net.*;
import java.util.*;
import java.io.*;
import java.applet.*;

// ----------------------------------------------------------------------
class MilkFairy {
  // the milk fairy.  Adds X amount of milk to a cow (presuming it's there).
  Cow cowList[]; // ref to the cowlist

  private final int EARLY_SPARE_COW_LIMIT = 1; // if more than this # of cows near
                                         // bursting, don't appear easy levels
  private final int EARLY_SPARE_COW_LEVEL = 4; // if more than this # of cows near
                                         // bursting, don't appear easy levels  

  private final int SPARE_COW_LIMIT = 3; // if more than this # of cows near
                                         // bursting, don't appear avg levels
  private final int ADV_SPARE_COW_LIMIT = 4; // if more than this # of cows near
                                         // bursting, don't appear avg levels
  private final int ADV_SPARE_COW_LEVEL = 10; // level to enable at

  private final int NUTS_SPARE_COW_LIMIT = 6; // if more than this # of cows near
                                         // bursting, don't appear avg levels
  private final int NUTS_SPARE_COW_LEVEL = 20; // level to enable at



  private int spareCowLimit = SPARE_COW_LIMIT; // # to spare

  private final int BASE_APPEAR_TIME = 10; // time to appear
  private final int APPEAR_TIME_LOG_FACTOR = 50;
  private final int LEVEL_BASE = 5;
  private final int LEVEL_FACTOR = 25;
  
  // hack: make the base factor this value after a certain level
  private final int LEVEL_BOUNDARY = 15;
  private final int EARLY_LEVEL_BOUNDARY = 3;
  private final int BASE_INTERVAL = 5; // milk fairy goes nuts

  int appearTime = BASE_APPEAR_TIME;
  
  private final int MAX_ADD_AMOUNT = 120;
  private final int BASE_ADD_AMOUNT = 10;
  int addAmount = BASE_ADD_AMOUNT; // amount of milk to add
  int choice; // choice of cow to milk.  Dud to prevent re-alloc/alloc from slowing down stuff.

  public MilkFairy() {
    // initialize a milk fairy
  }
  
  public void setCowList(Cow list[]) {
    // sets the cowlist (since the list is re-created each round);
    cowList = list;
  }

  public void reset() {
    // set variables based on level
    if (Farm.level >= NUTS_SPARE_COW_LEVEL)
      spareCowLimit = NUTS_SPARE_COW_LIMIT;
    else
      if (Farm.level >= ADV_SPARE_COW_LEVEL)
	spareCowLimit = ADV_SPARE_COW_LIMIT;
      else 
	if (Farm.level <= EARLY_SPARE_COW_LEVEL)
	  spareCowLimit = EARLY_SPARE_COW_LIMIT;
	else
	  spareCowLimit = SPARE_COW_LIMIT;
  }

  public void handle() {
    //    System.out.println("appeartime="+appearTime);
    appearTime--;
    // time to appear if countdown finished, and end Time not 
    // passed yet.
    if ((appearTime <= 0) && (Sun.endTime() == false)) {
      //      System.out.println("milk fairy appears!");
      
      // first check how many cows are near explosion level.  If too many
      // are, milk fairy takes pity on the bovines and leaves
      int cowsNearBursting = 0;
      for (int i=0;i<Farm.maxCowCount;i++) {
	if (cowList[i].milkReady()) {
	  if (cowList[i].milk > Cow.MILK_FAIRY_CEILING)
	    cowsNearBursting++;
	  //	      System.out.println("give milk to cow ="+i);
	  break;
	}
      }
      
      if (cowsNearBursting <= spareCowLimit) {
	// time for the magical milk fairy to appear!

	if (Farm.level >= LEVEL_BOUNDARY) {
	  // harsher appear time
	  appearTime = (int) (Math.random() * BASE_INTERVAL);
	} else 
	  if (Farm.level <= EARLY_LEVEL_BOUNDARY) {
	    // the earlier levels, the fairy appears faster to make things interesting
	    // this is compensated by making it limit to only 1 cow
	    appearTime = BASE_APPEAR_TIME + LEVEL_FACTOR + 
	      ( (int) (Math.random() * BASE_INTERVAL));
	  } else{
	    // average case
	    int levelFactor = LEVEL_BASE - Farm.level;
	    if (levelFactor <0)
	      levelFactor = 0;
	    
	    appearTime = (int)(Math.random() * (levelFactor * LEVEL_FACTOR));
	    appearTime += BASE_APPEAR_TIME;
	  } // end determine new appear time

	//		System.out.println("milk fairy new appear time="+appearTime);
	
	// set the amount to add
	addAmount = ((int)(Math.random()*MAX_ADD_AMOUNT)) + BASE_ADD_AMOUNT;
	
	// choose a cow to grant the gift of milk
	choice = (int)(Math.random() * Farm.maxCowCount);
	if (cowList[choice].milkReady()) {
	  cowList[choice].milkFairy(addAmount);
	}
	else { 
	  //	System.out.println("cow not available");
	  // choose one from a list
	  if (((int)(Math.random()*2)) == 1)
	    // search from bottom up
	    for (int i=0;i<Farm.maxCowCount;i++) {
	      if (cowList[i].milkReady()) {
		cowList[i].milkFairy(addAmount);
		//	      System.out.println("give milk to cow ="+i);
		break;
	      }
	    }
	  else
	    // search from top to bottom
	    for (int i=Farm.maxCowCount-1;i>=0;i--) {
	      if (cowList[i].milkReady()) {
		cowList[i].milkFairy(addAmount);
		//	      System.out.println("give milk to cow ="+i);
		break;
	      }
	    }
	}
	//	System.out.println("awarded amount="+addAmount);
      } else {
	//	System.out.println("milk fairy spares cow!");
      }
    } 
  }
}
