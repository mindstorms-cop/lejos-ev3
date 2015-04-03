package lejos.robotics.chassis;

import lejos.hardware.lcd.LCD;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Pose;
import lejos.utility.Delay;
import lejos.utility.Matrix;

/**
 * Represents the chassis of a differential robot. <br>
 * The Chassis is a control system for driving a mobile robot.
 * 
 * @author Aswin Bouwmeester
 *
 */
public class DifferentialChassis implements Chassis {

  final int              nWheels;
  final RegulatedMotor[] motor;

  protected Matrix       speed;
  protected Matrix       acceleration;

  private double[]       tacho;
  final protected Matrix forward;
  final protected Matrix reverse;
  final protected Matrix forwardAbs;
  final protected Matrix reverseAbs;
  private RegulatedMotor master;
  private Matrix tachoAtMoveStart;

  
  // Constructors
  
  public DifferentialChassis(final Wheel[] wheels) {
    nWheels = wheels.length;
    if (nWheels < 2 ) throw new  IllegalArgumentException("Differential robots must have at least two motorized wheels");

    // register the motor and their tacho position
    motor = new RegulatedMotor[nWheels];
    tacho = new double[nWheels];
    for (int i = 0; i < nWheels; i++) {
      motor[i] = wheels[i].getMotor();
      tacho[i] = motor[i].getTachoCount();
    }
    // Make the wheels synchronized
    this.master = motor[0];
    RegulatedMotor[] sync = new RegulatedMotor[nWheels - 1];
    System.arraycopy(motor, 1, sync, 0, nWheels - 1);
    master.synchronizeWith(sync);

    // create the forward forward matrix
    forward = new Matrix(wheels.length, 2);
    for (int row = 0; row < nWheels; row++) {
      forward.setMatrix(row, row, 0, 1, wheels[row].getFactors());
    }
    // create the reverse matrix
    reverse = forward.inverse();
    forwardAbs = this.copyAbsolute(forward);
    reverseAbs = this.copyAbsolute(reverse);
    
    // Give speed and acceleration some default values
    double s = this.getMaxLinearSpeed();
    double a = this.getMaxAngularSpeed();
    setSpeed(s/2, a/2);
    setAcceleration(s/2, a/2);
    
    // store position of tacho's
    tachoAtMoveStart = getAttribute(0);
  }

  
  // Gettters and setters
  @Override
  public void setSpeed(double linearSpeed, double angularSpeed) {
    if (linearSpeed <=0 || angularSpeed <=0) throw new  IllegalArgumentException("Speed must be greater than 0");
    speed = toMatrix(linearSpeed, angularSpeed);
  }

  @Override
  public void setAcceleration(double linearAcceleration, double angularAcceleration) {
    if (linearAcceleration <=0 || angularAcceleration <=0) throw new  IllegalArgumentException("Acceleration must be greater than 0");
    acceleration = toMatrix(linearAcceleration, angularAcceleration);
  }
  
  
  // Dynamics
  @Override
  public double getMaxLinearSpeed() {
    Matrix motorSpeed = getAttribute(1);
    Matrix wheelSpeed = copyAbsolute(reverse).times(motorSpeed);
    return wheelSpeed.get(0, 0);
  }

  @Override
  public double getMaxAngularSpeed() {
    Matrix motorSpeed = getAttribute(1);
    Matrix wheelSpeed = copyAbsolute(reverse).times(motorSpeed);
    return wheelSpeed.get(1, 0);
  }
  
  @Override
  public boolean isMoving() {
    for (RegulatedMotor wheel : motor) {
      if (wheel.isMoving()) {
        return true;
      }
    }
    return false;
  }


  @Override
  public void waitComplete() {
    for (RegulatedMotor wheel : motor)
      wheel.waitComplete();
  }

  @Override
  public boolean isStalled() {
    for (RegulatedMotor wheel : motor) {
      if (wheel.isStalled())
        return true;
    }
    return false;
  }

  @Override
  public double getMinRadius() {
    return 0;
  }

  

// Velocity based methods  

  @Override
  public void stop() {
    travel(0, 0);
  }

  @Override
  public void travel(double linearSpeed, double angularSpeed) {
    Matrix robotSpeed = toMatrix(linearSpeed, angularSpeed);
    travel(robotSpeed);
  }
  
  
 private synchronized void travel(Matrix robotSpeed) {   
    Matrix motorSpeed = forward.times(robotSpeed);
    Matrix motorAcceleration = forwardAbs.times(acceleration);
    Matrix currentMotorSpeed = (getAttribute(2));

    // calculate acceleration for each of the wheels. The goal is that all
    // wheels take an even amount of time to reach final speed
    Matrix dif = copyAbsolute(motorSpeed.minus(currentMotorSpeed)); 
    dif.arrayRightDivideEquals(motorAcceleration); 
    double longestTime = getMax(dif); 
    if (longestTime == 0) return; // Aha, no speed differences. Do nothing.
    double maxT = 1 / longestTime; 
    dif = dif.timesEquals(maxT); 
    Matrix transition = motorAcceleration.arrayTimes(dif); 
    
    // Set the dynamics and execute motion
    master.startSynchronization();
    for (int i = 0; i < nWheels; i++) {
      motor[i].setAcceleration((int) transition.get(i, 0));
      motor[i].setSpeed((int) Math.abs(motorSpeed.get(i, 0)));
      switch((int)Math.signum(motorSpeed.get(i, 0))) {
        case -1: motor[i].backward(); break;
        case 0: motor[i].stop(); break;
        case 1: motor[i].forward(); break;
      }
    }
    master.endSynchronization();
  }

  
  
  
  

  public static Modeler modelWheel(RegulatedMotor motor, double diameter) {
    return new Modeler(motor, diameter);
  }

  @Override
  public PoseProvider getOdometer() {
    return new Odometer();
  }
  
  
  

  
  private synchronized void setMotors(Matrix motorDelta, Matrix motorSpeed, Matrix motorAcceleration) {
    master.startSynchronization();
    for (int i = 0; i < nWheels; i++) {
      motor[i].setAcceleration((int) motorAcceleration.get(i, 0));
      motor[i].setSpeed((int) motorSpeed.get(i, 0));
      motor[i].rotate((int) motorDelta.get(i, 0));
    }
    master.endSynchronization();
  }

  
  @Override
  public  void moveTo(double linear) {
    if (Math.abs(linear) == Double.POSITIVE_INFINITY ) throw new  IllegalArgumentException("Distance must be finite");
    Matrix motorDelta = forward.times(toMatrix(linear, 0));
    Matrix motorSpeed = forwardAbs.times(toMatrix(speed.get(0,0), 0 ));
    Matrix motorAcceleration = forwardAbs.times(toMatrix(acceleration.get(0,0), 0 ));
    setMotors( motorDelta, motorSpeed, motorAcceleration);
  }

  @Override
  public  void rotateTo(double angular) {
    if (Math.abs(angular) == Double.POSITIVE_INFINITY ) throw new  IllegalArgumentException("Angle must be finite");
    Matrix motorDelta = forward.times(toMatrix(0, angular));
    Matrix motorSpeed = forwardAbs.times(toMatrix(0, speed.get(1,0) ));
    Matrix motorAcceleration = forwardAbs.times(toMatrix(0, acceleration.get(0,0) ));
    setMotors( motorDelta, motorSpeed, motorAcceleration);
  }
  
  public void arc (double radius, double angle) {
    if (angle == 0) return;
    double ratio =  Math.abs(Math.PI * radius / 180 );
  
    if (Math.abs(angle) == Double.POSITIVE_INFINITY) {
      Matrix tSpeed = speed.copy();
      if ((ratio) > 1) tSpeed.set(1, 0, tSpeed.get(0,0) / ratio);
      if ((ratio) < 1) tSpeed.set(0, 0, tSpeed.get(1,0) * ratio);
      if (angle < 0) tSpeed.set(0, 0, -tSpeed.get(0,0) );
      if (radius <0) tSpeed.set(1, 0, -tSpeed.get(1,0) );
      travel(tSpeed);
    }
    else if (radius == 0) {
      rotateTo(angle);
      return;
    }
    else {
      Matrix displacement  = toMatrix(2 * Math.PI * Math.abs(radius) * Math.abs(angle) / 360 , angle);
      if (angle < 0) displacement.set(0, 0, -displacement.get(0,0) );
      if (radius <0) displacement.set(1, 0, -displacement.get(1,0) );
      Matrix tSpeed = speed.copy();
      if (ratio > 1) tSpeed.set(1, 0, tSpeed.get(0,0) / ratio);
      if (ratio < 1) tSpeed.set(0, 0, tSpeed.get(1,0) * ratio);
      Matrix tAcceleration = acceleration.copy();
      if (ratio > 1) tAcceleration.set(1, 0, tAcceleration.get(0,0) / ratio);
      if (ratio < 1) tAcceleration.set(0, 0, tAcceleration.get(1,0) * ratio);
      Matrix motorDelta = forward.times(displacement);
      Matrix mRatio = motorDelta.times(1 / this.getMax(motorDelta));
      Matrix motorSpeed = forwardAbs.times(tSpeed).arrayTimes(mRatio);
      Matrix motorAcceleration = forwardAbs.times(tAcceleration).arrayTimes(mRatio);
      setMotors( motorDelta, motorSpeed, motorAcceleration);
    }
  }
  
  // Support for move based pilot
  
  public void moveStart() {
    tachoAtMoveStart = getAttribute(0);
  }
  
  public Move getDisplacement(Move move) {
    Matrix currentTacho = getAttribute(0);
    Matrix delta = currentTacho.minus(tachoAtMoveStart);

    delta = reverse.times(delta);
    double distance = delta.get(0, 0);
    double rotation = delta.get(1, 0);
    if (distance == 0 && rotation == 0  )
      move.setValues(Move.MoveType.STOP, (float) distance, (float) rotation, isMoving());
    else if (Math.abs(rotation) < 1 )
      move.setValues(Move.MoveType.TRAVEL, (float) distance, (float) rotation, isMoving());
    else if (Math.abs(distance) < 1)
      move.setValues(Move.MoveType.ROTATE, (float) distance, (float) rotation, isMoving());
    else move.setValues(Move.MoveType.ARC, (float) distance, (float) rotation, isMoving());
    return move;
  }


    // Matrix utilities

  /**
   * Create a Matrix to store linear and angular components
   * 
   * @param linear
   * @param angular
   * @return
   */
  private Matrix toMatrix(double linear, double angular) {
    Matrix x = new Matrix(2, 1);
    x.set(0, 0, linear);
    x.set(1, 0, angular);
    return x;
  }

  /**
   * Helper method to get some dynamic attributes from each motor
   * 
   * @param attribute
   * @return
   */
  private synchronized Matrix getAttribute(int attribute) {
    Matrix x = new Matrix(nWheels, 1);
    master.startSynchronization();
    for (int i = 0; i < nWheels; i++) {
      switch (attribute) {
      case 0:
        x.set(i, 0, motor[i].getTachoCount());
        break;
      case 1:
        x.set(i, 0, motor[i].getMaxSpeed());
        break;
      case 2:
        x.set(i, 0, motor[i].getRotationSpeed());
        break;
      }
    }
    master.endSynchronization();
    return x;
  }

  /**
   * Gets the biggest value from a matrix
   * 
   * @param a
   */
  private double getMax(Matrix a) {
    double max = Double.NEGATIVE_INFINITY;
    for (int m = 0; m < a.getRowDimension(); m++) {
      for (int n = 0; n < a.getColumnDimension(); n++) {
        max = Math.max(Math.abs(a.get(m, n)), max);
      }
    }
    return max;
  }

  /**
   * Make a copy of the source matrix, each of its element being the absolute
   * value of the elements of the source matrix
   * 
   * @param in
   * @return
   */
  private Matrix copyAbsolute(Matrix in) {
    Matrix a = in.copy();
    for (int m = 0; m < a.getRowDimension(); m++) {
      for (int n = 0; n < a.getColumnDimension(); n++) {
        a.set(m, n, Math.abs(a.get(m, n)));
      }
    }
    return a;
  }

  /** The odometer keeps track of the robot pose based on odometry 
   * @author Aswin Bouwmeester
   *
   */
  private class Odometer implements PoseProvider {
    Matrix lastTacho;
    double xPose, yPose, aPose;

    int    time = 64;

    private Odometer() {
      lastTacho = getAttribute(0);
      PoseTracker tracker = new PoseTracker();
      tracker.setDaemon(true);
      tracker.start();
    }

    @Override
    public Pose getPose() {
      return new Pose((float) xPose, (float) yPose, (float) aPose);
    }

    @Override
    public void setPose(Pose pose) {
      xPose = pose.getX();
      yPose = pose.getY();
      aPose = pose.getHeading();
    }

    private void updatePose() {
      Matrix currentTacho = getAttribute(0);
      Matrix delta = currentTacho.minus(lastTacho);

      int max = (int) getMax(delta);

      delta = reverse.times(delta);
      double distance = delta.get(0, 0);
      xPose += Math.cos(Math.toRadians(aPose)) * distance;
      yPose += Math.sin(Math.toRadians(aPose)) * distance;
      aPose += delta.get(1, 0);
      while (aPose < 180)
        aPose += 360;
      while (aPose > 180)
        aPose -= 360;

      // adjust loop speed (between 4 and 64 msec);
      if (max > 10) time=time / 2;
      if (max < 10) time=time * 2;
      time = Math.max(Math.min(time, 64), 4);
      lastTacho = currentTacho;
    }

    private class PoseTracker extends Thread {
      public void run() {
        while (true) {
          updatePose();
          Delay.msDelay(time);
        }
      }
    }
  }
  
  /** The Modeler class helps to model a wheel. Wheel attributes can be modeled using methods.
   * <ul>
   * <li>offset() specifes the location of the wheel along the y-axis</li>
   * <li>gearing() specifes the gear ratio of the gear train between motor and wheel</li>
   * <li>invert() inverts the direction of the motor. Equivalent to a negative gearing</li>
   * </ul>
   * <p>
   * @author Aswin Bouwmeester
   *
   */
  public static class Modeler implements Wheel {
    protected RegulatedMotor motor;
    protected double         diameter;
    protected double         gearing = 1;
    protected double         offset  = 0;
    protected double         angle   = 0;

    /**
     * Creates a modeler object to model a robot wheel
     * 
     * @param motor
     *          The regulated motor that drives the wheel
     * @param diameter
     *          The diameter of the wheel (Lego wheels have the diameter printed
     *          on the side)
     */
    public Modeler(RegulatedMotor motor, double diameter) {
      this.motor = motor;
      this.diameter = diameter;
    }

    /**
     * Defines the offset off the wheel
     * 
     * @param val
     *          The distance between the robots yPose-axis and the center of the
     *          wheel
     * @return this
     */
    public Modeler offset(double val) {
      this.offset = val;
      return this;
    }

    /**
     * Defines the gear train between motor and wheel.
     * 
     * @param val
     *          The ratio between wheel speed and motor speed
     * @return
     */
    public Modeler gearing(double val) {
      this.gearing = val;
      return this;
    }

    /**
     * Inverts the motor direction
     * 
     * @param val
     * @return
     */
    public Modeler invert(boolean val) {
      gearing = -gearing;
      return this;
    }

    public Matrix getFactors() {
      Matrix factors = new Matrix(1, 2);
      factors.set(0, 0, (360 * gearing) / (diameter * Math.PI));
      factors.set(0, 1, -((2.0 * offset * gearing) / diameter));
      return factors;
    }

    public RegulatedMotor getMotor() {
      return motor;
    }

  }


}
