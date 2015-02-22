package org.lejos.ev3.sample.pilottest;
import lejos.hardware.motor.Motor;
import lejos.robotics.chassis.AckermannChassis;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.DifferentialChassis;
import lejos.robotics.chassis.Steer;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.localization.OdometryPoseProvider;
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
  LineFollowingMoveController  pilot;
  OdometryPoseProvider poseProvider;
  double diameter = 400;  
  double angle = 90;
  double distance = 500;
  long time = 1000;

  public static void main(String[] args) {
    TestPilot foo = new TestPilot();
    if (foo.pilot.getMinRadius() > 0)  foo.testSteer();
    foo.circle();
//    foo.travels();
//    foo.arcs();
//    foo.arcDirections();
//    foo.travelArcs();
//    if (foo.pilot.getMinRadius() == 0) foo.rotates();
//    foo.dynamics();
//    foo.stops();
//    if (foo.pilot.getMinRadius() == 0) foo.localization();
//    foo.symmetry();
//    foo.steers();
  }
  
  private void circle() {
    pilot.arc(diameter, 360);
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
    pilot.setTravelSpeed(pilot.getMaxTravelSpeed() * .5);
    pilot.setAcceleration((int) (pilot.getTravelSpeed() * 1));
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
    pilot.setTravelSpeed(pilot.getMaxTravelSpeed() * .8);
    pilot.setAcceleration((int) (pilot.getTravelSpeed() * 4));

  }

  private void stops() {
    pilot.setAcceleration((int) (pilot.getTravelSpeed() * 1));
    pilot.forward();
    Delay.msDelay(time);
    pilot.stop();
    pilot.backward();
    Delay.msDelay(time);
    pilot.stop();
    pilot.setAcceleration((int) (pilot.getTravelSpeed() * 4));
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
    pilot.setAcceleration((int) (pilot.getTravelSpeed() * 1));
   
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
    pilot.travel(distance);
    pilot.travel(-distance);
    pilot.travel(distance, true);
    while (pilot.isMoving());
    pilot.travel(-distance, true);
    while (pilot.isMoving());
  }

 
  private TestPilot() {
    Chassis chassis;
    Wheel[] wheels;
    Steer steer;
//    wheels = new Wheel[]{new Wheel.Modeler(Motor.D, 43.2).offset(66.5).build(), new Wheel.Modeler(Motor.A, 43.2).offset(-66.5).build()};
//    chassis = new DifferentialChassis(wheels ); 
    
    wheels = new Wheel[]{ new Wheel.Modeler(Motor.B, 43.2).offset(71).gearing(-1).build(), 
                          new Wheel.Modeler(Motor.C, 43.2).offset(-71).gearing(-1).build()};
    steer = new Steer.Modeler(Motor.A, 287).maxAngle(30).gearing(12.0/20.0).build();
    chassis = new AckermannChassis(wheels, steer);
    
    pilot = new NewPilot(chassis);
    diameter = Math.max(diameter, chassis.getMinRadius());
    System.out.println(diameter);    

    
    poseProvider = new OdometryPoseProvider(pilot);
//    pilot = new DifferentialPilot(43.2, 142, Motor.D, Motor.A);
//    poseProvider = new OdometryPoseProvider(pilot);
  }
  
  
  void getPose() {
    Pose pose = poseProvider.getPose();
    System.out.println(pose);
  }

}
