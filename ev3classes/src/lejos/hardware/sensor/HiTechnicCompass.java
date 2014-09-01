package lejos.hardware.sensor;

import lejos.hardware.port.I2CPort;
import lejos.hardware.port.Port;
import lejos.robotics.Calibrate;

/**
 * This class supports the <a href="http://www.hitechnic.com">HiTechnic</a> compass sensor.
 * 
 * See http://www.hitechnic.com/cgi-bin/commerce.cgi?preadd=action&key=NMC1034
 * 
 */

/**
 * <b>HiTechnic compass sensor Sensor name</b><br>
 * The HiTechnic compass measures the earth’s magnetic field and calculates a
 * heading angle.
 * 
 * <p style="color:red;">
 * The code for this sensor has not been tested. Please report test results to
 * the <A href="http://www.lejos.org/forum/"> leJOS forum</a>.
 * </p>
 * 
 * <p>
 * <table border=1>
 * <tr>
 * <th colspan=4>Supported modes</th>
 * </tr>
 * <tr>
 * <th>Mode name</th>
 * <th>Description</th>
 * <th>unit(s)</th>
 * <th>Getter</th>
 * </tr>
 * <tr>
 * <td>Compass</td>
 * <td>Measures the orientation of the sensor</td>
 * <td>Degrees</td>
 * <td> {@link #getCompassMode() }</td>
 * </tr>
 * </table>
 * 
 * 
 * 
 * <p>
 * <b>Sensor configuration</b><br>
 * The sensor can be calibrated for magnetic disturbances coming from the robot
 * (soft iron calibration). Use the startCalibration method to put the sensor in
 * calibration mode. While in calibration mode the sensor should be rotated
 * slowly for making at least 1.5 full rotations. Then end calibration with the
 * endCalibration method.
 * 
 * <p>
 * 
 * @see <a
 *      href="http://www.hitechnic.com/cgi-bin/commerce.cgi?preadd=action&key=NMC1034">
 *      Sensor Product page </a>
 * @see <a href="http://sourceforge.net/p/lejos/wiki/Sensor%20Framework/"> The
 *      leJOS sensor framework</a>
 * @see {@link lejos.robotics.SampleProvider leJOS conventions for
 *      SampleProviders}
 * 
 *      <p>
 * 
 * 
 * @author Your name
 * 
 */
public class HiTechnicCompass extends I2CSensor implements Calibrate {
  private final static byte COMMAND           = 0x41;
  private final static byte BEGIN_CALIBRATION = 0x43;
  private final static byte END_CALIBRATION   = 0x00;       // Back to measurement mode

  byte[]                    buf               = new byte[2];

  /**
   * Create a compass sensor object
   * 
   * @param port
   *          I2C port for the compass
   * @param address
   *          The I2C address used by the sensor
   */
  public HiTechnicCompass(I2CPort port, int address) {
    super(port, address);
    init();
  }

  /**
   * Create a compass sensor object
   * 
   * @param port
   *          I2C port for the compass
   */
  public HiTechnicCompass(I2CPort port) {
    super(port, DEFAULT_I2C_ADDRESS);
    init();
  }

  /**
   * Create a compass sensor object
   * 
   * @param port
   *          Sensor port for the compass
   * @param address
   *          The I2C address used by the sensor
   */
  public HiTechnicCompass(Port port, int address) {
    super(port, address);
    init();
  }

  /**
   * Create a compass sensor object
   * 
   * @param port
   *          Sensor port for the compass
   */
  public HiTechnicCompass(Port port) {
    super(port);
    init();
  }

  protected void init() {
    setModes(new SensorMode[] { new CompassMode() });
  }

  /**
   * Get a compass mode sensor provider
   * 
   * @return the sample provider
   */
  public SensorMode getCompassMode() {
    return getMode(0);
  }

  private class CompassMode implements SensorMode {

    @Override
    public int sampleSize() {
      return 1;
    }

    @Override
    public void fetchSample(float[] sample, int offset) {
      // TODO: Change sample register to 44h.
      // The sensor uses the two degree heading register and one degree adder (42h, 43h). 
      // It would be simpler to use the heading registers 44h and 45h 
      getData(0x42, buf, 2);
      sample[offset] = (((buf[0] & 0xff) << 1) + buf[1]);
      if (sample[offset] != 0) {
        // correction for right hand coordinate system
        sample[offset] = 360 - sample[offset];
      }
    }

    @Override
    public String getName() {
      return "Compass";
    }
  }

  /**
   * Starts calibration for the compass. Must rotate *very* slowly, taking at
   * least 20 seconds per rotation.
   * 
   * Should make 1.5 to 2 full rotations. Must call stopCalibration() when done.
   */
  @Override
  public void startCalibration() {
    buf[0] = BEGIN_CALIBRATION;
    sendData(COMMAND, buf, 1);
  }

  /**
   * Ends calibration sequence.
   *
   */
  @Override
  public void stopCalibration() {
    buf[0] = END_CALIBRATION;
    sendData(COMMAND, buf, 1);
  }
}
