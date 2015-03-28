package lejos.robotics.movechassis;

import lejos.robotics.RegulatedMotor;

public class HolonomicMoveWheel extends MoveWheel {
  
  private double angle;


  private HolonomicMoveWheel(Modeler builder) {
    super(builder);
    this.angle= builder.angle;
    }
  
  

 
  public static class Modeler extends MoveWheel.Modeler {
    private double angle =0;

    public Modeler(RegulatedMotor motor, double diameter) {
      super(motor, diameter);
    }
    
    /** Defines the angle between wheel axis and robot x-axis.
     * @param val
     * The angle between wheel axis and robot x-axis
     * @return
     */
    public Modeler angle(double val) {
      this.angle = val;
      return this;
    }

  }

}
