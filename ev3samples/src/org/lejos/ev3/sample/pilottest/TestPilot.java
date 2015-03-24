package org.lejos.ev3.sample.pilottest;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.Motor;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.movechassis.AckermannMoveChassis;
import lejos.robotics.movechassis.DifferentialMoveChassis;
import lejos.robotics.movechassis.MoveChassis;
import lejos.robotics.movechassis.MoveSteer;
import lejos.robotics.movechassis.MoveWheel;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.LineFollowingMoveController;
import lejos.robotics.navigation.Pose;
import lejos.utility.Delay;
import lejos.robotics.navigation.NewPilot;


/**
 * Unit test for a  pilot
 * @author Aswin Bouwmeester
 *
 */
public class TestPilot {
  static final int OLD_DIFFERENTIAL = 0;
  static final int NEW_DIFFERENTIAL = 1;
  static final int NEW_STEERING = 2;
  
  LineFollowingMoveController  pilot;
  OdometryPoseProvider poseProvider;
  double diameter = 300;  
  double angle = 90;
  double distance = 300;
  long time = 4000;

  public static void main(String[] args) {
    TestPilot foo = new TestPilot(NEW_DIFFERENTIAL);
    foo.pilot.setTravelSpeed(100);
    foo.pilot.setAcceleration((int) (foo.pilot.getTravelSpeed() * 1));
    Sound.beep();
    Button.waitForAnyPress();
//    if (foo.pilot.getMinRadius() != 0) 
//      foo.testSteer();
    foo.travels();
//    foo.circle();
//    foo.arcs();
//    foo.arcDirections();
//    foo.travelArcs();
//    if (foo.pilot.getMinRadius() == 0) 
//      foo.rotates();
//    foo.dynamics();
//    foo.stop();
//    if (foo.pilot.getMinRadius() == 0) 
//      foo.localization();
//    foo.steers1();
//    foo.symmetry();
  }
  
  private void circle() {
    pilot.arc(diameter, 360);
    pilot.travel(0);
  }

  private void symmetry() {
    // both counterclockwise turn and clockwise turn should last equally long
    long start = System.currentTimeMillis();
    pilot.arc(diameter, angle * 10);
    System.out.format("CCW: %d%n", System.currentTimeMillis() -start);
    
    start = System.currentTimeMillis();
    pilot.arc(diameter, -angle * 10);  // Behaves strange with 0.9.0-beta, has radius=0;
    System.out.format(" CW: %d%n", System.currentTimeMillis() -start);
  }

  private void localization() {
    // Set to zero
    poseProvider.setPose(new Pose());
    getPose();
    pilot.travel(distance);
    getPose();
    pilot.rotate(90);
    getPose();
    pilot.travel(distance);
    getPose();
    pilot.rotate(90);
    getPose();
    pilot.arc(distance, 90);  // Strange behavior sometimes with both versions of the pilot, starts as an arc ends as a line
    getPose();
    pilot.rotate(90);
    getPose(); 
  }

  private void stop() {
    pilot.forward();
    Delay.msDelay(time);
    pilot.stop();
    pilot.backward();
    Delay.msDelay(time);
    pilot.stop();
  }

  private void dynamics() {
  // Test all the methods dealing with dynamics  
  pilot.setTravelSpeed(pilot.getMaxTravelSpeed() * .8);
  pilot.setAcceleration((int) (pilot.getTravelSpeed() * 4));
  pilot.travel(distance);
  pilot.setTravelSpeed(pilot.getMaxTravelSpeed() * .5);
  pilot.setAcceleration((int) (pilot.getTravelSpeed() * 1));
  pilot.travel(-distance);
  pilot.setTravelSpeed(pilot.getMaxTravelSpeed() * .8);
  pilot.setAcceleration((int) (pilot.getTravelSpeed() * 4));
  
  if (pilot.getMinRadius()==0) {
    pilot.setRotateSpeed(pilot.getRotateMaxSpeed() *.8);
    pilot.rotate(360);
    pilot.setRotateSpeed(pilot.getRotateMaxSpeed() *.2);
    pilot.rotate(-360);
    pilot.setRotateSpeed(pilot.getRotateMaxSpeed() *.8);
  }
  pilot.setTravelSpeed(100);
  pilot.setAcceleration((int) (pilot.getTravelSpeed() * 1));
  }

  private void steers1() {
    pilot.steer(0);         // Causes a stop with 0.9.0-beta
    Delay.msDelay(time);
//    pilot.steer(100);         // Causes a stop with 0.9.0-beta
//    Delay.msDelay(time);
//    pilot.steer(-100);         // Causes a stop with 0.9.0-beta
//    Delay.msDelay(time);
//    pilot.steer(199);         // Causes a stop with 0.9.0-beta
//    Delay.msDelay(time);
//    pilot.steer(-199);         // Causes a stop with 0.9.0-beta
//    Delay.msDelay(time);
    pilot.stop();
  }
  
  private void steers() {
    pilot.forward();
    Delay.msDelay(time);
    pilot.steer(0);         // Causes a stop with 0.9.0-beta
    Delay.msDelay(time);
    pilot.steer(20);        // Causes a stop with 0.9.0-beta
    Delay.msDelay(time);
    pilot.steer(-20);       // Causes a stop with 0.9.0-beta
    Delay.msDelay(time);
    pilot.stop();
    
    pilot.steerBackward(-20);
    Delay.msDelay(time);
    pilot.steerBackward(20);
    Delay.msDelay(time);
    pilot.steerBackward(0);
    Delay.msDelay(time);
  }

  private void arcs() {
    // 0.9.0-beta doesn't calculate acceleration or speed properly
   
    pilot.arc(diameter, angle);
    pilot.arc(diameter, -angle);   // Sometimes behaves strange with 0.9.0-beta, has radius=0;
    pilot.arc(-diameter, angle);
    pilot.arc(-diameter, -angle);  // Sometimes behaves strange with 0.9.0-beta, has radius=0;
  }
  
  

  
  private void testSteer() {
    pilot.travelArc(pilot.getMinRadius(), 0);
    Delay.msDelay(time);
    pilot.travelArc(-pilot.getMinRadius(), 0);
    Delay.msDelay(time);
    pilot.travel(0);
  }
    
  private void arcDirections() {
    pilot.arcForward(diameter); 
    Delay.msDelay(time);
    pilot.arcBackward(diameter); 
    Delay.msDelay(time);
    pilot.arcForward(-diameter); 
    Delay.msDelay(time);
    pilot.arcBackward(-diameter); 
    Delay.msDelay(time);
    pilot.stop();
  }

  
  private void travelArcs() {
    pilot.travelArc(diameter , distance);
    pilot.travelArc(diameter, -distance);
    pilot.travelArc(-diameter, distance); // Behaves strange with 0.9.0-beta,
    pilot.travelArc(-diameter, -distance);
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
    Delay.msDelay(time);
    pilot.backward();
    Delay.msDelay(time);
    pilot.travel(distance, true);
    while (pilot.isMoving());
    pilot.travel(-distance, true);
    while (pilot.isMoving());
    pilot.travel(distance);
    pilot.travel(-distance);
  }

 
  private TestPilot(int type) {
    MoveChassis chassis;
    MoveWheel[] wheels;
    MoveSteer steer;
    
    switch (type) {
      case OLD_DIFFERENTIAL: {
        pilot = new DifferentialPilot(43.2, 142, Motor.D, Motor.A);
        break;}
      case NEW_DIFFERENTIAL: {
        wheels = new MoveWheel[]{new MoveWheel.Modeler(Motor.D, 43.2).offset(72).build(), new MoveWheel.Modeler(Motor.A, 43.2).offset(-72).build()};
        chassis = new DifferentialMoveChassis(wheels ); 
        pilot = new NewPilot(chassis);
        break;
      }
      case NEW_STEERING: {
        wheels = new MoveWheel[]{ new MoveWheel.Modeler(Motor.D, 81.6).offset(80).gearing(1).build(), 
            new MoveWheel.Modeler(Motor.A, 81.6).offset(-80).gearing(1).build()};
        steer = new MoveSteer.Modeler(Motor.B, -132).maxAngle(30).gearing(.93).build();
        chassis = new AckermannMoveChassis(wheels, steer);
        pilot = new NewPilot(chassis);
        break;
      }
    }
    
    diameter = Math.max(diameter, pilot.getMinRadius());
    poseProvider = new OdometryPoseProvider(pilot);
  }
  
  
  void getPose() {
    Pose pose = poseProvider.getPose();
    System.out.println(pose);
  }

}
