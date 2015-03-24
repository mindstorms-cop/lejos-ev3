package lejos.robotics.chassis;

import lejos.robotics.RegulatedMotor;
import lejos.robotics.localization.PoseProvider;
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
  }

  @Override
  public void setSpeed(double linearSpeed, double angularSpeed) {
    if (linearSpeed <=0 || angularSpeed <=0) throw new  IllegalArgumentException("Speed must be greater than 0");
    speed = toMatrix(linearSpeed, angularSpeed);
  }

  private void setSpeed(Matrix robotSpeed) {
    Matrix wheelSpeed = forwardAbs.times(robotSpeed);
    for (int i = 0; i < nWheels; i++) {
      motor[i].setSpeed((int) wheelSpeed.get(i, 0));
    }
  }

  @Override
  public void setAcceleration(double linearAcceleration, double angularAcceleration) {
    if (linearAcceleration <=0 || angularAcceleration <=0) throw new  IllegalArgumentException("Speed must be greater than 0");
    acceleration = toMatrix(linearAcceleration, angularAcceleration);
  }

  private void setAcceleration(Matrix robotAcceleration) {
    Matrix wheelAcceleration = forwardAbs.times(robotAcceleration);
    for (int i = 0; i < nWheels; i++) {
      motor[i].setAcceleration((int) wheelAcceleration.get(i, 0));
    }
  }

  @Override
  public void travel(double linearSpeed, double angularSpeed) {
    Matrix robotSpeed = toMatrix(linearSpeed, angularSpeed);
    Matrix motorSpeed = forward.times(robotSpeed);
    Matrix motorAcceleration = forwardAbs.times(acceleration);
    Matrix currentMotorSpeed = (getAttribute(2));

    // calculate acceleration for each of the wheels. The goal is that all
    // wheels take an even amount of time to reach final speed
    Matrix dif = copyAbsolute(motorSpeed.minus(currentMotorSpeed)); // get the
                                                                    // difference
                                                                    // between
                                                                    // current
                                                                    // and
                                                                    // target
                                                                    // speed for
                                                                    // each
                                                                    // motor
    dif.arrayRightDivideEquals(motorAcceleration); // Calculate how much time it
                                                   // would take for each wheel
                                                   // to reach target speed
                                                   // using acceleration
                                                   // settings
    double longestTime = getMax(dif); // Find the longest of the acceleration
                                      // times
    if (longestTime == 0)
      return; // Aha, no speed differences.
    double maxT = 1 / longestTime; // Find the longest of the acceleration times
    dif = dif.timesEquals(maxT); // Create a correction factor for acceleration
                                 // accelTime / longestTime
    Matrix transition = motorAcceleration.arrayTimes(dif); // decrease
                                                           // acceleration using
                                                           // the correction
                                                           // factor

    master.startSynchronization();
    for (int i = 0; i < nWheels; i++) {
      double s = motorSpeed.get(i, 0);
      double a = transition.get(i, 0);
      motor[i].setAcceleration((int) a);
      motor[i].setSpeed((int) s);
      if (s == 0)
        motor[i].stop();
      else if (s < 0)
        motor[i].backward();
      else if (s > 0)
        motor[i].forward();
    }
    master.endSynchronization();
  }

  @Override
  public void moveTo(double linear, double angular) {
    Matrix wheelDelta = toMatrix(linear, angular);
    Matrix motorDelta = forward.times(wheelDelta);
    setSpeed(speed);
    setAcceleration(acceleration);
    master.startSynchronization();
    for (int i = 0; i < nWheels; i++) {
      double s = motorDelta.get(i, 0);
      motor[i].rotate((int) s);
    }
    master.endSynchronization();
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
  public void stop() {
    travel(0, 0);
  }

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

  public static Modeler modelWheel(RegulatedMotor motor, double diameter) {
    return new Modeler(motor, diameter);
  }

  @Override
  public PoseProvider getOdometer() {
    return new Odometer();
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
  private Matrix getAttribute(int attribute) {
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

  /**
   * The odometer keeps track of the robot pose based on odometry
   * @author Aswin Bouwmeester
   *
   */
  private class Odometer implements PoseProvider {
    Matrix lastTacho;
    double xPose, yPose, aPose;

    int    time = 64;

    private Odometer() {
      lastTacho = new Matrix(nWheels, 1);
      for (int i = 0; i < nWheels; i++) {
        lastTacho.set(i, 0, motor[i].getTachoCount());
      }
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
      // TODO: Next statement will trigger a motor synchronize from a second thread (the chassis thread being the other one)
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

      
      //      LCD.clear();
//      LCD.drawString("dist: " + dist, 0, 0);
//      LCD.drawString("angl: " + delta.get(1, 0), 0, 1);
//      LCD.drawString("max : " + max, 0, 2);
//      LCD.drawString("time: " + time, 0, 3);
//
//      LCD.drawString("xPose:    " + xPose, 0, 5);
//      LCD.drawString("yPose:    " + yPose, 0, 6);
//      LCD.drawString("aPose:    " + aPose, 0, 7);
//      LCD.refresh();

    }

    private class PoseTracker extends Thread {

      public void run() {
        // this.setPriority(MAX_PRIORITY);
        while (true) {
          updatePose();
          Delay.msDelay(time);
        }
      }

    }

  }
  
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
