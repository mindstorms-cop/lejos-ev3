package lejos.robotics.chassis;

import lejos.robotics.RegulatedMotor;

/**
 * The wheel class represent a wheel driven by a regulated motor (via a gearing
 * train). Used in transforming location, speed, acceleration from robot unit
 * into tacho units and back <p>
 * 
 * Use the helper class Modeller to model the wheel:
 * <code>
 * <pre>
 * Wheel.Modeller modeller = new Wheel.Modeller(Motor.motorA, 43.2);
 * modeller.offset(72);
 * modeller.reverse(true);
 * Wheel wheel = modeller.build();
 * 
 * //Alternative
 * Wheel wheel = Wheel.Modeller(Motor.motorA, 43.2).offset(72).reverse(true).build();
 * </pre>
 * </code> 
 * @author Aswin Bouwmeester
 */
public class Wheel {

  protected RegulatedMotor motor;
  protected double         offset;
  protected double         diameter;
  protected double         gearing = 1;
  private int            lastTacho;



  public Wheel(RegulatedMotor motor, double offset, double diameter, double gearing, boolean reverse) {
    this.motor = motor;
    this.offset = offset;
    this.diameter = diameter;
    if (reverse) gearing = -gearing;
    lastTacho = motor.getTachoCount();
  }

  protected Wheel(Modeler model) {
    this.motor = model.motor;
    this.offset = model.offset;
    this.diameter = model.diameter;
    this.gearing = model.gearing;
    if (model.invert) gearing = -gearing;
    lastTacho = motor.getTachoCount();
  }
  
  
  /**
   * This method blocks while the wheel is moving. It returns when the motor stops or stalls
   */
  public void waitComplete() {
    motor.waitComplete();
  }
  
  /** Returns true if the wheel is stalled
   * @return
   */
  public boolean isStalled() {
    return motor.isStalled();
  }
  
  /** Returns true if the wheel is rotating
   * @return
   */
  public boolean isMoving() {
    return motor.isMoving();
  }
  

  
  /** Returns the gearing ratio of the wheel. 
   * @return
   * The number of rotations the motor has to make for one rotation of the wheel.
   */
  public double getGearing() {
    return gearing;
  }

  /** Sets the gearing ratio of the wheel. 
   * @param gearing
   * The number of rotations the motor has to make for one rotation of the wheel.
   */
  public void setGearing(double gearing) {
    this.gearing = gearing;
  }

  /** Returns the offset of the wheel
   * @return
   * Distance to the Y axis
   */
  public double getOffset() {
    return offset;
  }

  /** Returns the diameter of the wheel
   * @return
   */
  public double getDiameter() {
    return diameter;
  }

  /** converts distance to tacho counts
   * @param loc
   * @return
   */
  protected double toTacho(double loc) {
    return 360 * loc / (diameter * Math.PI * gearing);
  }

  /** converts tacho counts to distance
   * @param tacho
   * @return
   */
  protected double fromTacho(double tacho) {
    return diameter * Math.PI * gearing * tacho / 360;
  }

  /** Returns the maximum speed the wheel can achieve
   * @return
   */
  public double getMaxSpeed() {
    return fromTacho(motor.getMaxSpeed());
  }

  /**
   * Makes the wheel travel the specified distance using specified dynamics. A
   * motionless motor at start and end of the travel is assumed.
   * 
   * @param distance
   *          Distance in robot units
   * @param speed
   *          Speed in robot units / second
   * @param acceleration
   *          Acceleration in robot units / second^2
   */
  public void travel(double distance, double speed, double acceleration) {
    motor.setAcceleration((int) toTacho(acceleration));
    motor.setSpeed((int) toTacho(speed));
    if (distance * gearing == Double.POSITIVE_INFINITY)
      motor.forward();
    else if (distance * gearing == Double.NEGATIVE_INFINITY)
      motor.backward();
    else
      motor.rotate((int) toTacho(distance), true);
  }

  /**
   * Makes the wheel travel the specified distance using specified dynamics. A
   * correction for making an arc is applied based on the motors offset from the
   * center of the robot.
   * 
   * @param distance
   *          Distance in robot units
   * @param speed
   *          Speed in robot units / second
   * @param acceleration
   *          Acceleration in robot units / second^2
   */
  public void arc(double radius, double angle, double speed, double acceleration) {
    travel(Math.toRadians(angle) * (radius - offset), correct(speed, radius), correct(acceleration, radius));
  }

  /**
   * Returns the displacement of the wheel since this method was last called
   * 
   * @param noReset
   *          If true the displacement is not set to zero.
   * @return
   */
  public double getDisplacement(boolean noReset) {
    int tacho = motor.getTachoCount();
    int delta = tacho - lastTacho;
    if (!noReset)
      lastTacho = tacho;
    return fromTacho(delta);
  }

  /**
   * Calculates a correction factor for offset when driving arcs
   * 
   * @param v
   * @param radius
   * @return
   */
  protected double correct(double v, double radius) {
    if (radius == 0 || radius == Double.POSITIVE_INFINITY || radius == Double.NEGATIVE_INFINITY)
      return v;
    return v * Math.abs((radius - offset) / radius);
  }

  /**
   * Returns the speed of the wheel in robot units
   * 
   * @return
   */
  public double getSpeed() {
    return fromTacho(motor.getSpeed());
  }

  /**
   * Sets the speed of the wheel
   * 
   * @param speed
   *          Speed in robot units
   */
  public void setSpeed(double speed) {
    motor.setSpeed((int) toTacho(speed));
  }

  /** A helper class to model the wheel
   * @author Aswin Bouwmeester
   *
   */
  public static class Modeler {
    private RegulatedMotor motor;
    private double         diameter;
    private double         offset=0;
    private double         gearing = 1;
    private boolean        invert  = false;
    
    /** Creates a modeler object to model a robot wheel
     * @param motor
     * The regulated motor that drives the wheel
     * @param diameter
     * The diameter of the wheel (Lego wheels have the diameter printed on the side)
     */
    public Modeler(RegulatedMotor motor, double diameter) {
      this.motor=motor;
      this.diameter=diameter;
    }

    /** Defines the offset off the wheel
     * @param val
     * The distance between the robots y-axis and the center of the wheel
     * @return this
     */
    public Modeler offset(double val) {
      this.offset = val;
      return this;
    }

    /** Defines the gear train between motor and wheel.
     * @param val
     * The ratio between wheel speed and motor speed
     * @return
     */
    public Modeler gearing(double val) {
      this.gearing = val;
      return this;
    }

    /** Inverts the motor direction
     * @param val
     * @return
     */
    public Modeler invert(boolean val) {
      invert = val;
      return this;
    }

    /** Builds Wheel object from the model 
     * @return
     * A Wheel object
     */
    public Wheel build() {
      return new Wheel(this);
    }
  }


}
