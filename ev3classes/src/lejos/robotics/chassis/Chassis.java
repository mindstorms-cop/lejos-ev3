package lejos.robotics.chassis;

import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Move;


/**
 * Represents the chassis of a robot. The Chassis provides a control system for driving a mobile robot. 
 * 
 * <p><b>How to use a Chassis object</b><br>
 * The primary goal of the chassis is to make a robot move. 
 * This can be done in two different ways. <br>
 * One way is by specifying the speed at which a chassis should move, this is called velocity mode. 
 * The primary methods for this mode are {@link #travel(double, double) travel} and {@link #stop() stop} .<br>
 * The second way is by specifying  the kind of move the chassis should make, this is called move mode. 
 * The primary methods for this mode are {@link #arc(double, double) arc}, {@link #rotateTo(double) rotateTo} and {@link #moveTo(double) move}.
 * The move mode methods use speed and acceleration parameters that have to be specified beforehand using {@link #setSpeed(double, double) setSpeed} 
 * and {@link #setAcceleration(double, double) setAcceleration}. <br>
 * Depending on the application one of these modes will suit better, but the modes can be used together.</p>
 * 
 * <p><b>Understanding linear and angular velocity and acceleration</b><br>
 * Within the context of the chassis speed and acceleration have both a linear and an angular component. 
 * The linear component describes the forward speed (or acceleration) of the robot. 
 * It specifies the speed of the robot traveling a straight line using the same unit as was used to specify the wheels of the chassis.
 * The linear speed is always the speed of the center of the robot, when driving an arc the forward speed of the outer wheel exceeds the speed of the center of the chassis.<br>
 * The angular component of speed (or acceleration) describes how fast the robot is turning around its center. This is always expressed in degrees/second. <br>
 * When using velocity based methods the chassis combines both speed elements to calculate the speed of each of the wheels. 
 * This results in a movement of the robot that can be anything from a rotation around its center (when the linear speed component is zero) 
 * to a curve (when both components are not zero) to a straight line (when the angular component is zero). <br>
 * When using move based methods the speed settings are used to calculate the speed at which a move can be made. 
 * If, for example, a very low angular speed has been set then this influences the speed at which an arc will be driven. 
 * The chassis makes sure the forward speed will be such that the angular speed will not exceed the specified setting. <p>
 * 
 * <p><b>Speed transitions</b><br>
 * To prevent jerky movements of the robot the velocity based methods {@link #travel(double, double) travel} and {@link #stop() stop} 
 * ensure smooth speed transitions using the acceleration settings that are specified using the 
 * {@link #setAcceleration(double, double) setAcceleration} method. 
 * This means that each of the wheels of the chassis takes exactly the same time to reach final speed, 
 * no matter what the speed of the wheels was at the moment the travel method was issued.<br>
 * The move beased methods do not know these smooth transitions. 
 * These methods assume that the robot is at stand still when the method is issued and that the robot is at stand still again when the move ends. 
 * (Without this assumption it cannot be guaranteed that the trajectory of the robot has indeed the same shape as the method name implies.) 
 * 
 * <p><b>Odometry</b><br>
 * The chassis can provide a {@link lejos.robotics.localization.PoseProvider PoseProvider} that keeps track of the robots pose using the encoders of the wheels.
 * The object is provided by the {@link #getOdometer() getOdometer} method.</p>
 * 
 * @author Aswin Bouwmeester
 *
 */
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
   * linear component of the robot speed, expressed in the same unit as the wheel diameter.
   * @param angularSpeed
   * angular component of the robot speed expressed in degrees/second.
   */
  public void travel(double linearSpeed, double angularSpeed);

  /** Moves the chassis the specified distance 
   * @param linear
   * linear component of the robot speed, expressed in the same unit as the wheel diameter.
   */
  public void moveTo(double linear);
  
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
   * @param linearSpeed
   * linear component of the robot speed, expressed in the same unit as the wheel diameter.
   * @param angularSpeed
   * angular component of the robot speed expressed in degrees/second.
   */
  public void setSpeed(double linearSpeed, double angularSpeed);

  /** Sets the acceleration of the chassis for the moveTo and travel methods
   * @param forwardAcceleration
   * linear component of the robot acceleration, expressed in robot units/second^2.
   * @param angularAcceleration
   * angular component of the robot speed expressed acceleration, expressed in degrees/second^2.
   */
  public void setAcceleration(double forwardAcceleration, double angularAcceleration);
  
  /** Returns an Pose provider that uses odometry to keep track of the pose of the chassis
   * @return
   */
  public PoseProvider getOdometer();  
  
  /** Method used by the MovePilot to tell the chassis that a new move has started. This method is used in conjuction with the getDisplacement method.
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
  public Move getDisplacement(Move move);

  /** Rotates the chassis for the specified number of degrees
   * @param angular
   */
  void rotateTo(double angular);
  
}
