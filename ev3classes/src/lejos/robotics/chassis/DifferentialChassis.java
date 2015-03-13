package lejos.robotics.chassis;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.Move;

/**
 * Represents the chassis of a robot including its motorized wheels.
 * Used for transforming robot moves into rotation of its wheels.
 * Used for transforming whel displacement into robot displacement.
 * The chassis is specified in a length unit of choice (called the robot unit). 
 * All the chassis methods use robots units. 
 * @author Aswin Bouwmeester
 *
 */
public class DifferentialChassis implements Chassis {

  final protected Wheel[]        wheels; // Wheel order should be from left to right
  final protected RegulatedMotor master;

  /**
   * @param wheels
   * An Array of Wheel objects representing each of the motorized wheels on the robot. 
   * The wheels should be ordered in the array according to wheel location (from left to right) 
   */
  public DifferentialChassis(final Wheel[] wheels) {
    this.wheels = wheels;
    this.master = wheels[0].getMotor();
    // TODO: Sort the wheel from left to right
    RegulatedMotor[] sync = new RegulatedMotor[wheels.length - 1];
    for (int n = 1; n < wheels.length; n++) {
      sync[n - 1] = wheels[n].getMotor();
    }
    master.synchronizeWith(sync);

  }

  @Override
  public boolean isMoving() {
    for (Wheel wheel : wheels) {
      if (wheel.isMoving()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void stop() {
    master.startSynchronization();
    for (Wheel wheel : wheels) {
      wheel.stop();
    }
    master.endSynchronization();
  }

  @Override
  public void arc(double radius, double angle, double speed, double acceleration) {
    double speedCorrection = speedCorrection(radius);
    master.startSynchronization();
    for (Wheel wheel : wheels) {
      wheel.arc(radius, angle, speed * speedCorrection, acceleration * speedCorrection);
    }
    master.endSynchronization();
  }

  @Override
  public void travel(double distance, double speed, double acceleration) {
    master.startSynchronization();
    for (Wheel wheel : wheels) {
      wheel.travel(distance, speed, acceleration);
    }
    master.endSynchronization();
  }

  @Override
  public double getMaxSpeed() {
    double maxSpeed = Double.POSITIVE_INFINITY;
    master.startSynchronization();
    for (Wheel wheel : wheels) {
      maxSpeed = Math.min(wheel.getMaxSpeed(), maxSpeed);
    }
    master.endSynchronization();
    return maxSpeed;
  }

  @Override
  public double getSpeed() {
    double speed = 0;
    master.startSynchronization();
    for (Wheel wheel : wheels) {
      speed = Math.max(speed, wheel.getSpeed());
    }
    master.endSynchronization();
    return speed;
  }
  
  @Override
  public void setSpeed(double speed) {
    double current = getSpeed();
    master.startSynchronization();
      for (Wheel wheel : wheels) {
        double ratio =  wheel.getSpeed() / current ;
        wheel.setSpeed(speed * ratio);
      }
      master.endSynchronization();
  }
  
  @Override
  public void setAcceleration(double acceleration) {
    double current = getSpeed();
    master.startSynchronization();
      for (Wheel wheel : wheels) {
        double ratio =  wheel.getSpeed() / current ;
        wheel.setSpeed(acceleration * ratio);
      }
      master.endSynchronization();
  }

  @Override
  public Move getDisplacement(Move move, boolean noReset) {
    // TODO: This method calculates the distance wrong if the two outer wheels do not have the same offset
    master.startSynchronization();
    double left = wheels[0].getDisplacement(noReset);
    double right = wheels[wheels.length - 1].getDisplacement(noReset);
    master.endSynchronization();
    double width = wheels[0].offset - wheels[wheels.length - 1].offset;
    double displacement = (left + right) / 2;
    double rotation = Math.toDegrees((right - left ) / width);
    if (left == 0 && right == 0  )
      move.setValues(Move.MoveType.STOP, (float) displacement, (float) rotation, isMoving());
    else if (Math.abs(rotation) < 1 )
      move.setValues(Move.MoveType.TRAVEL, (float) displacement, (float) rotation, isMoving());
    else if (Math.abs(displacement) < width/20)
      move.setValues(Move.MoveType.ROTATE, (float) displacement, (float) rotation, isMoving());
    else move.setValues(Move.MoveType.ARC, (float) displacement, (float) rotation, isMoving());
    return move;  
  }

  @Override
  public Move getDisplacement(Move move) {
    return getDisplacement(move, false);
  }
  
  
  
  /** Helper method to calculate speed ratios for each of the wheels when driving arcs.
   *  The outermost wheel will drive with robot speed.
   * Robot speed applies to the speed of the outer wheel in an arc
   * @param radius
   * @return
   */
  private double speedCorrection(double radius) {
    if (radius == Double.POSITIVE_INFINITY || radius == Double.NEGATIVE_INFINITY) return 1;
    double max = 1;
    for (Wheel wheel : wheels) {
      max = Math.max(max, wheel.correct(1, radius));
    }
    return 1 / max;
  }

  @Override
  public void waitComplete() {
    for (Wheel wheel : wheels)
      wheel.waitComplete();
  }

  @Override
  public double getWidth() {
    return wheels[0].offset - wheels[wheels.length - 1].offset;
  }

  @Override
  public double getMaxRotateSpeed() {
    double max = Double.POSITIVE_INFINITY;
    for (Wheel wheel : wheels) {
      max = Math.min(360 * wheel.getSpeed() / (Math.PI * Math.abs(wheel.offset)), max);
    }
    return max;
  }


  @Override
  public boolean isStalled() {
    for (Wheel wheel : wheels) {
      if (wheel.isStalled())
        return true;
    }
    return false;
  }

  @Override
  public double getMinRadius() {
    return 0;
  }

}
