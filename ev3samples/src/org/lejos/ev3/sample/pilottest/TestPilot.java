package org.lejos.ev3.sample.pilottest;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.Motor;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.DifferentialChassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.ArcRotateMoveController;
import lejos.robotics.navigation.DifferentialPilot;
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
  
  ArcRotateMoveController  pilot;
  PoseProvider poseProvider;
  double radius = 300;  
  double angle = 90;
  double distance = 1000;
  int time = 4000;
  private boolean wait = false;

  public static void main(String[] args) {
    TestPilot foo = new TestPilot(NEW_DIFFERENTIAL);
    foo.pilot.setLinearSpeed(foo.pilot.getMaxLinearSpeed()/2);
    foo.pilot.setLinearAcceleration(foo.pilot.getMaxLinearSpeed()/4);
    foo.pilot.setAngularSpeed(foo.pilot.getMaxAngularSpeed()/2);
    foo.pilot.setAngularAcceleration(foo.pilot.getMaxAngularSpeed()/4);
    Sound.beep();
    Button.waitForAnyPress();
    //foo.travel();
    //foo.arc();
    foo.dynamics();
    Button.waitForAnyPress();
  }
  
private void endMove() {
  System.out.println(poseProvider.getPose()); 
  poseProvider.setPose(new Pose(0,0,0)); 
  if (wait ) Button.waitForAnyPress(time);

}

private TestPilot(int type) {
    
    switch (type) {
      case OLD_DIFFERENTIAL: {
        pilot = new DifferentialPilot(43.2, 142, Motor.D, Motor.A);
        poseProvider = new OdometryPoseProvider(pilot);
        break;}
      case NEW_DIFFERENTIAL: {
        Chassis chassis;
        Wheel wheel1 = DifferentialChassis.modelWheel(Motor.A,43.2 * 0.989).offset(-72 / 1.033);
        Wheel wheel2 = DifferentialChassis.modelWheel(Motor.D,43.2 * 0.989).offset(72 / 1.033);
        chassis = new DifferentialChassis(new Wheel[]{wheel1, wheel2}); 
        pilot = new NewPilot(chassis);
        poseProvider = chassis.getOdometer();
        break;
      }
    }
    
    radius = Math.max(radius, pilot.getMinRadius());
    poseProvider = new OdometryPoseProvider(pilot);
  }


private void travel() {
  pilot.travel(distance);
  endMove();
  pilot.travel(-distance);
  endMove();
  pilot.travel(distance, true);
  while(pilot.isMoving()) Delay.msDelay(10);
  endMove();
  pilot.travel(-distance, true);
  while(pilot.isMoving()) Delay.msDelay(10);
  endMove();
  pilot.travel(0, true);
  while(pilot.isMoving());
  endMove();
}

private void arc() {
  pilot.arc(radius, angle);
  endMove();
  pilot.arc(radius, -angle);
  endMove();
  pilot.arc(-radius, angle);
  endMove();
  pilot.arc(-radius, -angle);
  endMove();
  pilot.arc(radius, angle, true);
  while(pilot.isMoving()) Delay.msDelay(10);
  endMove();
  pilot.arc(radius, -angle, true);
  while(pilot.isMoving()) Delay.msDelay(10);
  endMove();
}

private void dynamics() {
  double lMax = pilot.getMaxLinearSpeed();

//  pilot.setLinearSpeed(lMax);
//  pilot.setLinearAcceleration(lMax/16);
//  pilot.travel(distance);
//  pilot.travel(-distance);
//  endMove();
  pilot.setLinearSpeed(lMax/16);
  pilot.setLinearAcceleration(lMax);
  pilot.travel(distance);
  pilot.travel(-distance);
  endMove();
}
}
