package lejos.robotics.navigation;
import java.util.ArrayList;

import lejos.robotics.Chassis;
import lejos.robotics.Wheel;
import lejos.robotics.DifferentialChassis;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.ArcRotateMoveController;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;

/**
 * The DifferentialPilot class is a software abstraction of the Pilot mechanism
 * of a robot. It contains methods to control robot movements: travel forward or
 * backward in a straight line or a circular path or rotate to a new direction.<br>
 * This class will only work with two or more independently controlled motors to
 * steer differentially, so it can rotate within its own footprint (i.e. turn on
 * one spot). An object of this class assumes that it has exclusive control of
 * its motors. If any other object makes calls to its motors, the results are
 * unpredictable. <br>
 * This class can be used with robots that have reversed motor design: the robot
 * moves in the direction opposite to the the direction of motor rotation. .<br>
 * It automatically updates a
 * {@link lejos.robotics.localization.OdometryPoseProvider} which has called the
 * <code>addMoveListener</code> method on this object.<br>
 * Some methods optionally return immediately so the thread that called it can
 * do things while the robot is moving, such as monitor sensors and call
 * {@link #stop()}.<br>
 * Handling stalls: If a stall is detected, <code>isStalled()</code> returns
 * <code>
 * true </code>, <code>isMoving()</code> returns <code>false</code>,
 * <code>moveStopped()
 * </code> is called, and, if a blocking method is executing, that method exits.
 * The units of measure for travel distance, speed and acceleration are the
 * units used in specifying the wheel diameter and track width in the
 * constructor. <br>
 * In all the methods that cause the robot to change its heading (the angle
 * relative to the X axis in which the robot is facing) the angle parameter
 * specifies the change in heading. A positive angle causes a turn to the left
 * (anti-clockwise) to increase the heading, and a negative angle causes a turn
 * to the right (clockwise). <br>
 * Example of use of come common methods:
 * <p>
 * <code><pre>
 * DifferentialPilot pilot = new DifferentialPilot(2.1f, 4.4f, Motor.A, Motor.C, true);  // parameters in inches
 * pilot.setRobotSpeed(30);  // cm per second
 * pilot.travel(50);         // cm
 * pilot.rotate(-90);        // degree clockwise
 * pilot.travel(-50,true);  //  move backward for 50 cm
 * while(pilot.isMoving())Thread.yield();
 * pilot.rotate(-90);
 * pilot.rotateTo(270);
 * pilot.steer(-50,180,true); // turn 180 degrees to the right
 * waitComplete();            // returns when previous method is complete
 * pilot.steer(100);          // turns with left wheel stationary
 * Delay.msDelay(1000;
 * pilot.stop();
 * </pre></code>
 * </p>
 * 
 * Note: A DifferentialPilot robot can simulate a SteeringPilot robot by calling
 * DifferentialPilot.setMinRadius() and setting the value to something greater
 * than zero (perhaps 15 cm).
 * 
 **/
public class NewDifferentialPilot implements LineFollowingMoveController {
  private double                  minRadius   = 0;      
  final private Chassis           chassis;
  private ArrayList<MoveListener> _listeners  = new ArrayList<MoveListener>();
  private double                  travelSpeed;
  private double                  acceleration;
  private double                  rotateSpeed;
  private Monitor                 _monitor;
  private boolean                 _moveActive = false;
  private Move                    move = null;

  /**
   * Allocates a DifferentialPilot object, and sets the physical parameters of
   * the robot.<br>
   * Assumes Motor.forward() causes the robot to move forward.
   * 
   * @param wheelDiameter
   *          Diameter of the tire, in any convenient units (diameter in mm is
   *          usually printed on the tire).
   * @param trackWidth
   *          Distance between center of right tire and center of left tire, in
   *          same units as wheelDiameter.
   * @param leftMotor
   *          The left Motor (e.g., Motor.C).
   * @param rightMotor
   *          The right Motor (e.g., Motor.A).
   */
  public NewDifferentialPilot(final double wheelDiameter, final double trackWidth, final RegulatedMotor leftMotor,
      final RegulatedMotor rightMotor) {
    this(wheelDiameter, trackWidth, leftMotor, rightMotor, false);
  }

  /**
   * Allocates a DifferentialPilot object, and sets the physical parameters of
   * the robot.<br>
   * 
   * @param wheelDiameter
   *          Diameter of the tire, in any convenient units (diameter in mm is
   *          usually printed on the tire).
   * @param trackWidth
   *          Distance between center of right tire and center of left tire, in
   *          same units as wheelDiameter.
   * @param leftMotor
   *          The left Motor (e.g., Motor.C).
   * @param rightMotor
   *          The right Motor (e.g., Motor.A).
   * @param reverse
   *          If true, the NXT robot moves forward when the motors are running
   *          backward.
   */
  public NewDifferentialPilot(final double wheelDiameter, final double trackWidth, final RegulatedMotor leftMotor,
      final RegulatedMotor rightMotor, final boolean reverse) {
    this(wheelDiameter, wheelDiameter, trackWidth, leftMotor, rightMotor, reverse);
  }

  /**
   * Allocates a DifferentialPilot object, and sets the physical parameters of
   * the robot.<br>
   * 
   * @param leftWheelDiameter
   *          Diameter of the left wheel, in any convenient units (diameter in
   *          mm is usually printed on the tire).
   * @param rightWheelDiameter
   *          Diameter of the right wheel. You can actually fit intentionally
   *          wheels with different size to your robot. If you fitted wheels
   *          with the same size, but your robot is not going straight, try
   *          swapping the wheels and see if it deviates into the other
   *          direction. That would indicate a small difference in wheel size.
   *          Adjust wheel size accordingly. The minimum change in wheel size
   *          which will actually have an effect is given by minChange =
   *          A*wheelDiameter*wheelDiameter/(1-(A*wheelDiameter) where A =
   *          PI/(moveSpeed*360). Thus for a moveSpeed of 25 cm/second and a
   *          wheelDiameter of 5,5 cm the minChange is about 0,01058 cm. The
   *          reason for this is, that different while sizes will result in
   *          different motor speed. And that is given as an integer in degree
   *          per second.
   * @param trackWidth
   *          Distance between center of right tire and center of left tire, in
   *          same units as wheelDiameter.
   * @param leftMotor
   *          The left Motor (e.g., Motor.C).
   * @param rightMotor
   *          The right Motor (e.g., Motor.A).
   * @param reverse
   *          If true, the NXT robot moves forward when the motors are running
   *          backward.
   */
  public NewDifferentialPilot(final double leftWheelDiameter, final double rightWheelDiameter, final double trackWidth,
      final RegulatedMotor leftMotor, final RegulatedMotor rightMotor, final boolean reverse) {
    this(new DifferentialChassis(new Wheel[] { new Wheel(leftMotor, trackWidth / 2, leftWheelDiameter, reverse),
        new Wheel(rightMotor, trackWidth / -2, rightWheelDiameter, reverse) }));
  }

  /**
   * Allocates a DifferentialPilot object, and sets the physical parameters of
   * the robot.<br>
   * 
   * @param chassis
   *          A Chassis object describing the physical parameters of the robot.
   */
  public NewDifferentialPilot(Chassis chassis) {
    this.chassis = chassis;
    setTravelSpeed(chassis.getMaxSpeed() * 0.8);
    setAcceleration(getTravelSpeed() * 4);
    this.setRotateSpeed(chassis.getMaxRotateSpeed() * 0.8);
    minRadius = chassis.getMinRadius();
    _monitor = new Monitor();
    _monitor.start();

  }

  // Getters and setters of dynamics

  // TODO: setting acceleration should be part of an interface. Note the double
  // parameter (aka setTravelSpeed) as opposed to the current differentialPilot.
  
  // TODO: decide if we want to support mid moves acceleration changes (This is only possible when acceleration increases)
  public void setAcceleration(double acceleration) {
//    if (_moveActive && acceleration > this.acceleration) {
//      chassis.setAcceleration(acceleration);
//    }
    this.acceleration = acceleration;
  }

  // TODO: getting acceleration should be part of an interface.
  public double getAcceleration() {
    return acceleration;
  }

  @Override
  public void setTravelSpeed(double speed) {
    this.travelSpeed = speed;
    if (_moveActive) {
      move.setDynamics((float)travelSpeed, (float)rotateSpeed);
      if (move.getMoveType() != Move.MoveType.TRAVEL) {
        chassis.setSpeed(travelSpeed);
      }
    }
  }

  @Override
  public double getTravelSpeed() {
    return travelSpeed;
  }

  @Override
  public double getMaxTravelSpeed() {
    return chassis.getMaxSpeed();
  }

  @Override
  public void setRotateSpeed(double speed) {
    this.rotateSpeed = speed;
    if (_moveActive) {
      move.setDynamics((float)travelSpeed, (float)rotateSpeed);
      if (move.getMoveType() == Move.MoveType.TRAVEL) {
        chassis.setSpeed(rotateSpeed);
      }
    }
  }

  @Override
  public double getRotateSpeed() {
    return rotateSpeed;
  }

  @Override
  public double getRotateMaxSpeed() {
    return chassis.getMaxRotateSpeed();
  }

  @Override
  public double getMinRadius() {
    return minRadius;
  }

  @Override
  public void setMinRadius(double radius) {
    minRadius = radius;
  }

  // Moves of the travel family

  @Override
  public void forward() {
    travel(Double.POSITIVE_INFINITY, true);

  }

  @Override
  public void backward() {
    travel(Double.NEGATIVE_INFINITY, true);
  }

  @Override
  public void travel(double distance) {
    travel(distance, false);

  }

  @Override
  public void travel(double distance, boolean immediateReturn) {
    if (chassis.isMoving())
      stop();
    chassis.travel(distance, travelSpeed, acceleration);
    move = new Move(Move.MoveType.TRAVEL, (float) distance, 0, (float) travelSpeed, (float) rotateSpeed, chassis.isMoving());
    movementStart();
    handleEndofMove(immediateReturn);
  }

  // Moves of the Arc family

  @Override
  public void arcForward(double radius) {
    arc(radius, Double.POSITIVE_INFINITY, true);
  }

  @Override
  public void arcBackward(double radius) {
    arc(radius, Double.NEGATIVE_INFINITY, true);
  }

  @Override
  public void arc(double radius, double angle) {
    arc(radius, angle, false);
  }

  @Override
  public void travelArc(double radius, double distance) {
    travelArc(radius, distance, false);
  }

  @Override
  public void travelArc(double radius, double distance, boolean immediateReturn) {
    arc(radius,  distance / (2 * Math.PI), immediateReturn);
  }

  @Override
  public void rotate(double angle) {
    rotate(angle, false);
  }

  @Override
  public void rotate(double angle, boolean immediateReturn) {
    arc(0, angle, immediateReturn);
  }

  // TODO: Not part of an interface
  public void rotateLeft() {
    rotate(Double.POSITIVE_INFINITY, true);
  }

  // TODO: Not part of an interface
  public void rotateRight() {
    rotate(Double.NEGATIVE_INFINITY, true);
  }

  
  @Override
  public void arc(double radius, double angle, boolean immediateReturn) {
    if (chassis.isMoving()) {
      stop();
    }
    if (Math.abs(radius) > minRadius) {
      throw new RuntimeException("Turn radius too small.");
    }
    arcNoStop(radius, angle, immediateReturn);
  }

  /**
   * Deals with making an arc without ensuring the termination of an already
   * active move. This method may only be called from moves of the steer family
   * and by arc(radius, angle, immediateReturn)
   * 
   * @param radius
   * @param angle
   * @param immediateReturn
   */
  private void arcNoStop(double radius, double angle, boolean immediateReturn) {
    if (radius == 0) {
      chassis.arc(radius, angle, rotateSpeed, acceleration);
      move = new Move(Move.MoveType.ROTATE, 0, (float) angle, (float) travelSpeed, (float) rotateSpeed, chassis.isMoving());
    } else {
      chassis.arc(radius, angle, travelSpeed, acceleration);
      move = new Move(Move.MoveType.ARC, (float) (Math.toRadians(angle) * radius), (float) angle, (float) travelSpeed, (float) rotateSpeed,
          chassis.isMoving());
    }
    movementStart();
    handleEndofMove(immediateReturn);
  }

  // Moves of the steer family, steer moves are a branch of the arc moves.
  // Steer moves can be started before an active move is stopped;

  public void steer(double steerRatio) {
    steer(steerRatio, Double.POSITIVE_INFINITY, true);
  }

  public void steerBackward(double steerRatio) {
    steer(steerRatio, Double.NEGATIVE_INFINITY, true);
  }

  private void steer(double steerRatio, double angle) {
    steer(steerRatio, angle, false);
  }

  private void steer(double steerRatio, double angle, boolean immediateReturn) {
    double ratio = Math.abs((100 - Math.abs(steerRatio)) / 100);
    double radius;
    if (ratio == 1) {
      radius=Double.POSITIVE_INFINITY;
    } 
    else 
    {
      radius = chassis.getWidth() / (1-ratio );
      if (steerRatio > 0) {
        radius = -radius;
        angle = - angle;
      }
    }
    if (_moveActive) {
      // Instruct the listeners the current move will end by starting a new
      // move
      movementStop();
    }
    arcNoStop(radius, angle, immediateReturn);
  }

  // Stops. Stops must be blocking!

  @Override
  public void stop() {
    // This method must be blocking
    chassis.stop();
    chassis.waitComplete();
    synchronized (_monitor) {
      _monitor.notifyAll();
    }
    while (_moveActive) Thread.yield();
  }

  // TODO: not part of the interface
  public void quickStop() {
    chassis.quickStop();
    chassis.waitComplete();
    synchronized (_monitor) {
      _monitor.notifyAll();
    }
  }

  // State
  @Override
  public boolean isMoving() {
    return chassis.isMoving();
  }

  // Handle the end of a move

  /**
   * Handles the end of a move.
   * 
   * @param immediateReturn
   *          If false the method will block until the move has ended and then
   *          deal with it . If true the method will instruct a monitor process
   *          to detect athe end-of-move and to deal with it.
   */
  private void handleEndofMove(boolean immediateReturn) {
    if (!immediateReturn) {
      chassis.waitComplete();
      if (chassis.isStalled())
        stop();
      movementStop();
    } else {
      // Non blocking method, let the monitor handle the end of the move
      _moveActive = true;
    }
  }

  // Methods dealing with the listener model

  /**
   * Advertises the start of a move to the listeners.
   * 
   * @param move
   *          A move object containing the move that will be made.
   */
  protected void movementStart() {
    for (MoveListener ml : _listeners)
      ml.moveStarted(move, this);
  }

  /**
   * Advertises the end of a move to the listeners. The move object being
   * advertised contains the move that has actually been made.
   */
  private void movementStop() {
    _moveActive = false;
    chassis.getDisplacement(move);
    for (MoveListener ml : _listeners)
      ml.moveStopped(move, this);
  }

  @Override
  public Move getMovement() {
    if (_moveActive) {
    return chassis.getDisplacement(move, true);
    }
    else {
      return new Move(Move.MoveType.STOP, 0, 0, false);
    }
  }

  @Override
  public void addMoveListener(MoveListener listener) {
    _listeners.add(listener);

  }

  /**
   * The monitor class detects end-of-move situations when non blocking move
   * call were made and makes sure these are dealt with.
   *
   */
  private class Monitor extends Thread {
    public boolean more = true;

    public Monitor() {
      // don't make VM hang for us!
      setDaemon(true);
    }

    public synchronized void run() {
      while (more) {
        if (_moveActive) {
          if (chassis.isStalled())
            NewDifferentialPilot.this.stop();
          if (!chassis.isMoving()) {
            movementStop();
          }
        }
        // wait for an event
        try {
          wait(_moveActive ? 1 : 100);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }


}
