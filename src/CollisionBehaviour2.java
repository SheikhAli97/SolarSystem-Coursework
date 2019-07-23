import java.util.*;
import javax.media.j3d.*;
import com.sun.j3d.utils.geometry.*;

/**
* This class initiates two alternating movements
* when a collision occurs.
*
* @author Frank Klawonn Edited R. Crole
* 
*/

public class CollisionBehaviour2 extends Behavior {

   //Geometric shapes (eg Box and Sphere) are all derived from Primitive
   //It is a base class for geometric shapes
   public Primitive cylinder;

   //The movement is started, and continues, when a collision exit occurs.
   //used in both initialize and process stimulus 
   public WakeupOnCollisionEntry i_and_pS_criterion;

   //The Alphas associated with the movements.
   public Alpha[] alphas;

   //Each movement corresponds to an Alpha object;
   //alphas is an array of the (two) Alpha objects
   //with array entry index (0 or 1 indicating which Alpha): 
   int whichAlpha;

   /* BEGIN THE CONSTRUCTOR FOR THIS CLASS  --------------------------------- */
   public CollisionBehaviour2(Primitive theShape, Alpha[] theAlphas,
                              Bounds theBounds) {

     cylinder = theShape;
     alphas = theAlphas;
     setSchedulingBounds(theBounds);

   }
   /* END THE CONSTRUCTOR FOR THIS CLASS  --------------------------------- */

   /* BEGIN INITIALISE BEHAVIOR  --------------------------------- */
   public void initialize() {
     //At the very first collision, alphas[0] should be carried out.
     whichAlpha = 0;

     i_and_pS_criterion = new WakeupOnCollisionEntry(cylinder);
     // wakeupOn(WakeUpCondition wakeupcondition) -- see API 
     // this method passes the wake up criterion to processStimulus as an enumeration 
     // ... with only one element of course!
     wakeupOn(i_and_pS_criterion);
   }
   /* END INITIALISE BEHAVIOR  --------------------------------- */

   /* BEGIN PROCESS STIMULUS BEHAVIOR  --------------------------------- */

   // API: criteria - an enumeration of triggered wakeup criteria for this behavior
   // !! Look up Behavior class in the API !! 

   public void processStimulus(Enumeration criteria) {

       // !! do we really needs this loop ? !!
      while (criteria.hasMoreElements())
      {
          WakeupCriterion theCriterion = (WakeupCriterion) criteria.nextElement();

          //Set the starting time to "now".
          alphas[whichAlpha].setStartTime(System.currentTimeMillis());
          
	  
      } // end while

      	 // set the next wake up (another collision entry) 
          i_and_pS_criterion = new WakeupOnCollisionEntry(cylinder);
          wakeupOn(i_and_pS_criterion);

   }

   /* END PROCESS STIMULUS BEHAVIOR  --------------------------------- */

}
