
package lejos.robotics.chassis;

import lejos.robotics.RegulatedMotor;

// TODO: discuss the modeler versus the constructor 

/** The Steer class represents a steering mechanism of an Ackermann chassis (a car-like chassis). <p>
 * The steer class presumes the steering mechanism is driven by a regulated motor. 
 * It also presumes the steering mechanism to be centered on the Y axis with either one wheel on the Y-axis or two wheels each having the same but opposite offset from the Y-axis.
 * 
 * Use the helper class Modeller to model the steer:
 * <code>
 * <pre>
 * Steer.Modeller modeller = new Wheel.Modeller(Motor.motorA, 120);
 * modeller.maxAngle(45);
 * modeller.gearing(-36/12);
 * Steer steer = modeller.build();
 * 
 * //Alternative
 * Steer steer = Steer.Modeller(Motor.motorA, 43.2).maxAngle(45).gearing(-36/12).build();
 * </pre>
 * </code> 
 * 
 * @author Aswin Bouwmeester
 *
 */
public class Steer {
  double offset =0;
  double maxAngle = 90;
  double gearing = 1;
  double center;
  RegulatedMotor motor;
  
  /**
   * @param motor
   * The motor driving the steering mechanism
   * @param offset
   * The offset of the steering mechanism from the center of the robot (The y-position). Negative offset means the steering wheel is behind the driving wheels.
   * @param maxAngle
   * The maximum angle the steering mechanism can make
   * @param gear
   * The gearing ratio of the steering mechanism being the ratio between motor rotation and steering mechanism rotation
   */
  public Steer (RegulatedMotor motor, double offset, double maxAngle, double gearing) {
    this.motor = motor;
    this.offset= offset;
    this.maxAngle= maxAngle;
    this.gearing= gearing;
    center = motor.getTachoCount();
  }
  
  private Steer(Modeler model) {
    this.motor = model.motor;
    this.offset= model.offset;
    this.maxAngle= model.maxAngle;
    this.gearing= model.gearing;
    center = motor.getTachoCount();
  }

   /** Convert angle (in degrees) to radius
   * @param angle
   * @return
   */
  private double angleToRadius(double angle) {
    return offset / Math.tan(Math.toRadians(angle)) ;
  }
  
  /** Convert radius into angle
   * @param radius
   * @return
   */
  private double RadiusToAngle(double radius) {
    return Math.toDegrees(Math.atan(offset/radius));
  }
  
  /** Convert angle into tacho counts
   * @param angle
   * @return
   */
  private double angleToTacho(double angle) {
    return angle / gearing + center;
  }
  
  /** Returns the current angle of the steering mechanism
   * @return
   */
  public double getAngle() {
    return (motor.getTachoCount() - center ) * gearing ;
  }
  
  /**
   * Returns the radius of the arc that the robot would currently make
   * @return
   */
  public double getRadius() {
    return angleToRadius(getAngle());
  }
  
  /** Returns the minum radius the steering mechanism can make 
   * @return
   */
  public double getMinRadius() {
    return angleToRadius(maxAngle);
  }
  
  /** Prepares the steering mechanism to drive an arc with given radius
   * @param radius
   * @param immediateReturn
   */
  public void setRadius(double radius, boolean immediateReturn) {
    motor.rotateTo((int) angleToTacho(RadiusToAngle(radius)), immediateReturn);
  }
  
  /** Centers the steering mechanism
   * @param immediateReturn
   */
  public void center(boolean immediateReturn) {
    motor.rotateTo((int)center, immediateReturn);
  }

  
  /**
   * Defines the current position of the steering mechanism as the center
   */
  public void setCenter() {
    setCenter(motor.getTachoCount());
  }
  
  /** Sets the center of the steering mechanism to the given tacho position
   * @param tacho
   */
  public void setCenter(double tacho) {
    center=tacho;
  }
  
  /**
   * Returns when the steering mechanism has completed its move
   */
  public void waitComplete () {
    motor.waitComplete();
  }
  
  /** Helper class to model a steering mechanism
   * @author Aswin Bouwmeester
   *
   */
  public static class Modeler {
    private RegulatedMotor motor;
    private double offset ;
    private double maxAngle = 90;
    private double gearing = 1;
    
    /** Creates a modeler object to model a steering mechanism
     * @param motor
     * A regulated motor that drives the steering mechanism
     * @param offset
     * The distance between the origin of the robot (between the driving wheels) and the axis of the steering wheel(s)
     */
    public Modeler(RegulatedMotor motor, double offset) {
      this.motor = motor;
      this.offset = offset;
    }

    /** Defines the maximum angle the steering wheel(s) can make
     * @param val
     * angle in degrees
     * @return
     */
    public Modeler maxAngle(double val) 
      {this.maxAngle = val; return this;}
    
    /** Defines the gear train between motor and steering wheel(s)
     * @param val
     * The ratio between steering angle and motor rotation
     * @return
     */
    public Modeler gearing(double val) 
    {this.gearing = val; return this;}

    /** Builds a Steer object from the model
     * @return
     * A Steer object
     */
    public Steer build() {
      return new Steer(this);
    }

  }




  
}
