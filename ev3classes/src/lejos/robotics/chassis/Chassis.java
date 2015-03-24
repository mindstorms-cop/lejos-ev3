package lejos.robotics.chassis;

import lejos.robotics.localization.PoseProvider;

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
}
