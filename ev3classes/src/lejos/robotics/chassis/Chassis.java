package lejos.robotics.chassis;

import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Move;

public interface Chassis {

  /**
   * Returns True if the robot is moving.
   * 
   * @return
   */
  public boolean isMoving();

  /**
   * Makes the robot stop and returns immediately.
   */
  public void stop();

  
  /** Moves the chassis with specified speed
   * @param linearSpeed
   * @param angularSpeed
   */
  public void travel(double linearSpeed, double angularSpeed);

  /** Moves the chassis the specified distance and angle
   * @param linear
   * @param angular
   */
  public void moveTo(double linear, double angular);
  
  /** Moves the chassis in an arc 
   * @param radius
   * the radius of the arc. 
   * A positive radius means the center of the arc is on the left side of the robot, 
   * the center of a negative arc is on the right side of the robot. Infinite radius is not allowed. 
   * A radius of 0 makes the robot spin in place.
   *
   * @param angle
   * The number of degrees of the arc. A positive number of degrees makes the robot go forward,
   * a negative number makes it go backward.  
   */
  public void arc(double radius, double angle);

  /**
   * Returns the maximum speed of the robot.
   * 
   * @return Speed in robot units
   */
  public double getMaxLinearSpeed();

  /**
   * Returns how fast the robot can rotate.
   * 
   * @return Speed in degrees / second
   */
  public double getMaxAngularSpeed();

  /**
   * Blocks while the chassis is moving, returns when all wheels have stopped
   * (including stops caused by stalls)
   */
  public void waitComplete();

  /**
   * Returns true if at least one of the wheels is stalled
   * 
   * @return
   */
  public boolean isStalled();

  /**
   * Returns the smallest possible radius this chassis is able turn
   * 
   * @return radius in robot units
   */
  public double getMinRadius();

  /** Sets the speed of the chassis for the moveTo method
   * 
   * @param forwardSpeed
   * @param angularSpeed
   */
  public void setSpeed(double forwardSpeed, double angularSpeed);

  /** Sets the acceleration of the chassis for the moveTo and travel methods
   * @param forwardAcceleration
   * @param angularAcceleration
   */
  public void setAcceleration(double forwardAcceleration, double angularAcceleration);
  
  /** Returns an Pose provider that uses odometry to keep track of the pose of the chassis
   * @return
   */
  public PoseProvider getOdometer();  
  
  /** Method used by the MovePilot to tell the chassis that a new move has started
   * 
   */
  public void moveStart();
  
  /** Method used by the MovePilot to update a move object that describes the move executed since the last call to startMove. <p>
   * This method is only to be used by applications that only apply moves that meet the following conditions:<ul>
   * <li> The move must start and end with the robot being motionless</li>
   * <li> The speed ratio between the wheels must be constant during the move</li>
   * </ul>
   *  
   * @param move
   * The move object to update
   */
  public void getDisplacement(Move move);
  
}
