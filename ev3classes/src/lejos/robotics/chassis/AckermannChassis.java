package lejos.robotics.chassis;
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
public class AckermannChassis extends DifferentialChassis {

  private Steer steer;

  /**
   * @param wheels
   * An Array of Wheel objects representing each of the motorized wheels on the robot. 
   * The wheels should be ordered in the array according to wheel location (from left to right) 
   * @param steer 
   * A Steer object representing the steering mechanism
   */
  public AckermannChassis(Wheel[] wheels, Steer steer) {
    super(wheels);
    this.steer = steer;
  }



  @Override
  public void arc(double radius, double angle, double speed, double acceleration) {
    steer.setRadius(radius, false);
    super.arc(radius, angle, speed, acceleration);
  }

  @Override
  public void travel(double distance, double speed, double acceleration) {
    steer.center(false);
    super.travel(distance, speed, acceleration);
  }

  @Override
  public double getMinRadius() {
    return steer.getMinRadius();
  }

  @Override
  public Move getDisplacement(Move move, boolean noReset) {
    // With multiple driven wheels the move can be calculated just like in a differentialChassis
    if (wheels.length > 1) return super.getDisplacement(move, noReset);

    double displacement = wheels[0].getDisplacement(noReset);
    double radius = steer.getRadius();
    double rotation = Math.toDegrees( displacement  / (2 * Math.PI * radius));
    if (displacement == 0  )
      move.setValues(Move.MoveType.STOP, (float) displacement, (float) rotation, isMoving());
    else if (Math.abs(rotation) < 1 )
      move.setValues(Move.MoveType.TRAVEL, (float) displacement, (float) rotation, isMoving());
    else move.setValues(Move.MoveType.ARC, (float) displacement, (float) rotation, isMoving());
    return move;  
  }

}
