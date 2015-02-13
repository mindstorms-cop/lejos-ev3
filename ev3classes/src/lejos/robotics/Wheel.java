package lejos.robotics;
import lejos.robotics.MirrorMotor;
import lejos.robotics.RegulatedMotor;

/**
 * The wheel class represent a wheel driven by a regulated motor (via a gear
 * train). Used in transforming location, speed, acceleration from robot unit
 * into tacho units and back
 * 
 * @author Aswin Bouwmeester
 *
 */
public class Wheel {
  protected final RegulatedMotor motor;
  protected final double         offset;
  protected final double         diameter;
  protected double               gear = 1;
  private int                    lastTacho;

  public Wheel(RegulatedMotor motor, double offset, double diameter) {
    this(motor, offset, diameter, 1, false);
  }

  public Wheel(RegulatedMotor motor, double offset, double diameter, boolean reverse) {
    this(motor, offset, diameter, 1, reverse);
  }

  
  public Wheel(RegulatedMotor motor, double offset, double diameter, double gear) {
    this(motor, offset, diameter, gear, false);
  }

  
  public Wheel(RegulatedMotor motor, double offset, double diameter, double gear,  boolean reverse) {
    if (reverse)
      this.motor = MirrorMotor.invertMotor(motor);
    else
      this.motor = motor;
    this.offset = offset;
    this.diameter = diameter;
    lastTacho = motor.getTachoCount();
  }

  protected int toTacho(double loc) {
    return (int) (360 * loc / (diameter * Math.PI * gear));
  }

  protected double fromTacho(double tacho) {
    return diameter * Math.PI * gear * tacho / 360;
  }

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
    motor.setAcceleration(toTacho(acceleration));
    motor.setSpeed(toTacho(speed));
    if (distance == Double.POSITIVE_INFINITY)
      motor.forward();
    else if (distance == Double.NEGATIVE_INFINITY)
      motor.backward();
    else
      motor.rotate(toTacho(distance), true);
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
    if (radius == 0)
      return v;
    return v * Math.abs(radius - offset) / radius;
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
    motor.setSpeed(toTacho(speed));
  }

}
