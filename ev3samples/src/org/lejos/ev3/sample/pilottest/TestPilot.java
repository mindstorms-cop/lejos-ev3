package org.lejos.ev3.sample.pilottest;
import lejos.hardware.motor.Motor;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.Pose;
import lejos.utility.Delay;
import lejos.robotics.navigation.NewDifferentialPilot;


/**
 * Unit test for a differential pilot
 * @author Aswin Bouwmeester
 *
 */
public class TestPilot {
  NewDifferentialPilot pilot;
  OdometryPoseProvider poseProvider;

  public static void main(String[] args) {
    TestPilot foo = new TestPilot();
    foo.travels();
    foo.arcs();
    foo.arcDirections();
    foo.travelArcs();
    foo.rotates();
    foo.dynamics();
    foo.stops();
    foo.localization();
    foo.symmetry();
    foo.steers();
  }
  
  private void symmetry() {
    long start = System.currentTimeMillis();
    pilot.arc(250, 900);
    System.out.format("CCW: %d%n", System.currentTimeMillis() -start);
    
    start = System.currentTimeMillis();
    pilot.arc(250, -900);  // Behaves strange with 0.9.0-beta, has radius=0;
    System.out.format(" CW: %d%n", System.currentTimeMillis() -start);
  }

  private void localization() {
    // Set to zero
    pilot.setTravelSpeed(pilot.getMaxTravelSpeed() * .5);
    pilot.setAcceleration((int) (pilot.getTravelSpeed() * 1));
    poseProvider.setPose(new Pose());
    getPose();
    pilot.travel(500);
    getPose();
    pilot.rotate(90);
    getPose();
    pilot.travel(500);
    getPose();
    pilot.rotate(90);
    getPose();
    pilot.arc(500, 90);  // Strange behavior sometimes with both versions of the pilot, starts as an arc ends as a line
    getPose();
    pilot.rotate(90);
    getPose(); 
    pilot.setTravelSpeed(pilot.getMaxTravelSpeed() * .8);
    pilot.setAcceleration((int) (pilot.getTravelSpeed() * 4));

  }

  private void stops() {
    pilot.setAcceleration((int) (pilot.getTravelSpeed() * 1));
    pilot.forward();
    Delay.msDelay(2000);
    pilot.stop();
    pilot.backward();
    Delay.msDelay(2000);
    pilot.quickStop();
    pilot.setAcceleration((int) (pilot.getTravelSpeed() * 4));
  }

  private void dynamics() {
  // Test all the methods dealing with dynamics  
  pilot.setTravelSpeed(pilot.getMaxTravelSpeed() * .8);
  pilot.setAcceleration((int) (pilot.getTravelSpeed() * 4));
  pilot.travel(500);
  pilot.setTravelSpeed(pilot.getMaxTravelSpeed() * .5);
  pilot.setAcceleration((int) (pilot.getTravelSpeed() * 1));
  pilot.travel(-500);
  pilot.setTravelSpeed(pilot.getMaxTravelSpeed() * .8);
  pilot.setAcceleration((int) (pilot.getTravelSpeed() * 4));
  
  pilot.setRotateSpeed(pilot.getRotateMaxSpeed() *.8);
  pilot.rotate(360);
  pilot.setRotateSpeed(pilot.getRotateMaxSpeed() *.2);
  pilot.rotate(-360);
  pilot.setRotateSpeed(pilot.getRotateMaxSpeed() *.8);

  }

  private void steers() {
    pilot.forward();
    Delay.msDelay(1000);
    pilot.steer(0);         // Causes a stop with 0.9.0-beta
    Delay.msDelay(1000);
    pilot.steer(20);        // Causes a stop with 0.9.0-beta
    Delay.msDelay(2000);
    pilot.steer(-20);       // Causes a stop with 0.9.0-beta
    Delay.msDelay(2000);
    pilot.stop();
  }

  private void arcs() {
    // 0.9.0-beta doesn't calculate acceleration or speed properly
    pilot.setAcceleration((int) (pilot.getTravelSpeed() * 1));
   
    pilot.arc(300, 180);
    pilot.arc(300, -180);   // Sometimes behaves strange with 0.9.0-beta, has radius=0;
    pilot.arc(-300, 180);
    pilot.arc(-300, -180);  // Sometimes behaves strange with 0.9.0-beta, has radius=0;
  }
  
  

    
  private void arcDirections() {
    pilot.arcForward(200); 
    Delay.msDelay(2000);
    pilot.arcBackward(200); 
    Delay.msDelay(2000);
    pilot.arcForward(-200); 
    Delay.msDelay(2000);
    pilot.arcBackward(-200); 
    Delay.msDelay(2000);
    pilot.stop();
  }

  
  private void travelArcs() {
    pilot.travelArc(300 , 500);
    pilot.travelArc(300, -500);
    pilot.travelArc(-300, 500); // Behaves strange with 0.9.0-beta,
    pilot.travelArc(-300, -500);
  }
  
  private void rotates() {
    pilot.rotate(360);
    pilot.rotate(-360);
    pilot.rotate(360, true);
    while (pilot.isMoving());
    pilot.rotate(-360, true);
    while (pilot.isMoving());
  }

  private void travels() {
    pilot.forward();
    Delay.msDelay(2000);
    pilot.backward();
    Delay.msDelay(2000);
    pilot.travel(500);
    pilot.travel(-500);
    pilot.travel(500, true);
    while (pilot.isMoving());
    pilot.travel(-500, true);
    while (pilot.isMoving());
  }

 
  
  private TestPilot() {
    pilot = new NewDifferentialPilot(43.2, 142, Motor.D, Motor.A);
    poseProvider = new OdometryPoseProvider(pilot);
  }
  
  
  void getPose() {
    Pose pose = poseProvider.getPose();
    System.out.println(pose);
  }

}
