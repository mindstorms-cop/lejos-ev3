package lejos.robotics.movechassis;
import lejos.robotics.navigation.Move;

public interface MoveChassis {

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


  /**
   * Makes the robot drive an Arc and returns immediately
   * 
   * @param radius
   *          The radius of the Arc. Positive values will make it turn left.
   * @param angle
   *          The angle of the arc (in degrees). An infinite value will make the
   *          robot circle endlessly. Negative values will make it turn
   *          backwards.
   * @param speed
   *          Speed in robot units
   * @param acceleration
   *          Acceleration in robot units
   * 
   */
  public void arc(double radius, double angle, double speed, double acceleration);

  /**
   * Makes the robot drive in a straight line and returns immediately
   * 
   * @param distance
   *          An infinite value will make the robot circle endlessly. Negative
   *          values will make the robot drive back.
   * @param speed
   *          Speed in robot units
   * @param acceleration
   *          Acceleration in robot units
   * 
   */
  public void travel(double distance, double speed, double acceleration);

  /**
   * Returns the maximum speed of the robot.
   * 
   * @return Speed in robot units
   */
  public double getMaxSpeed();

  /**
   * Returns how fast the robot can rotate.
   * 
   * @return Speed in degrees / second
   */
  public double getMaxRotateSpeed();

  /**
   * Returns the current speed setting of the robot. For robots moving in arcs
   * this will be the speed of the outer wheel.
   * 
   * @return Speed in robot units
   */
  public double getSpeed();
  
  /**
   * Sets the current speed of the robot. If a robot is not moving this method
   * has no effect.
   * 
   * @param speed
   *          Speed in robot units
   */
  public void setSpeed(double speed);

  /**
   * Sets the current acceleration of the robot. If a robot is not moving this method
   * has no effect.
   * 
   * @param acceleration
   *          Speed in robot units
   */
  public void setAcceleration(double acceleration);
  
  
  /**
   * Returns a Move object containing the displacement of the robot since this
   * method was last called
   * 
   * @return A Move object containing the displacement in robot units
   */
  public Move getDisplacement(Move move);

  /**
   * Returns a Pose object containing the displacement of the robot since this
   * method was last called
   * 
   * @param noReset
   *          If set to true the method will not reset the displacement to zero
   * @return A Move object containing the displacement in robot units
   */
  public Move getDisplacement(Move move, boolean noReset);

  /**
   * Blocks while the chassis is moving, returns when all wheels have stopped
   * (including stops caused by stalls)
   */
  public void waitComplete();

  /**
   * Returns the track width of the robot, the distance between the two outer
   * most wheels
   * 
   * @return Track width in robot units
   * 
   */
  public double getWidth();

  /**
   * Returns true if at least one of the wheels is stalled
   * 
   * @return
   */
  public boolean isStalled();
  
  /** Returns the smallest possible radius this chassis is able turn
   * @return radius in robot units
   */
  public double getMinRadius() ;
  
}


