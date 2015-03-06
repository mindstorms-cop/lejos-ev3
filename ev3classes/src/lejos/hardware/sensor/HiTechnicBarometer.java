package lejos.hardware.sensor;

import lejos.hardware.port.I2CPort;
import lejos.hardware.port.Port;

/**
 * <b>Hitechnic Barometric sensor</b><br>
 * The sensor measures both atmospheric pressure and temperature.
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
 * <td>Pressure</td>
 * <td>Measures atmospheric pressure</td>
 * <td>HectoPascal</td>
 * <td> {@link #getPressureMode() }</td>
 * </tr>
 * <tr>
 * <td>Temperature</td>
 * <td>Measures temperature</td>
 * <td>Degree Celcius</td>
 * <td> {@link #getTemperatureMode() }</td>
 * </tr>
 * </table>
 * 
 * 
 * <p>
 * <b>Sensor configuration</b><br>
 * The sensor can be calibrated for pressure using the calibrate method.
 * <p>
 * 
 * @see <a
 *      href="http://www.hitechnic.com/cgi-bin/commerce.cgi?preadd=action&key=NBR1036">
 *      Sensor Product page </a>
 * @see <a href="http://sourceforge.net/p/lejos/wiki/Sensor%20Framework/"> The
 *      leJOS sensor framework</a>
 * @see {@link lejos.robotics.SampleProvider leJOS conventions for
 *      SampleProviders}
 * 
 *      <p>
 * 
 * 
 * @author Matthias Paul Scholz
 * 
 */
public class HiTechnicBarometer extends I2CSensor {

  private static final int BAROMETRIC_TEMPERATURE          = 0x42;
  private static final int BAROMETRIC_PRESSURE             = 0x44;
  private static final int BAROMETRIC_PRESSURE_CALIBRATION = 0x46;
  private final float      INHG_TO_HPA                     = 2992 / 1013.25f;
  private final float      STANDARD_ATMOSPHERIC_PRESSURE   = 1013.25f;

  private final byte[]     buffer                          = new byte[2];

  /**
   * Constructor.
   * 
   * @param port
   *          the {@link I2CPort} the sensor is connected to.
   */
  public HiTechnicBarometer(final I2CPort port) {
    super(port, DEFAULT_I2C_ADDRESS);
  }

  /**
   * Constructor.
   * 
   * @param port
   *          the {@link I2CPort} the sensor is connected to.
   * @param address
   *          the address
   */
  public HiTechnicBarometer(final I2CPort port, final int address) {
    super(port, address);
    init();
  }

  public HiTechnicBarometer(final Port port, final int address) {
    super(port, address, TYPE_LOWSPEED);
    init();
  }

  public HiTechnicBarometer(final Port port) {
    this(port, DEFAULT_I2C_ADDRESS);
    init();
  }

  protected void init() {
    setModes(new SensorMode[] { new PressureMode(), new TemperatureMode() });
    if (getCalibrationMetric() == 0)
      calibrate(STANDARD_ATMOSPHERIC_PRESSURE);
  }

  /**
   * Re-calibrates the sensor.
   * 
   * @param pascals
   *          the calibration value in hectopascals
   */
  public void calibrate(float pascals) {
    int calibrationImperial = (int) ((pascals / 10) / INHG_TO_HPA);
    buffer[0] = (byte) (calibrationImperial >> 8);
    buffer[1] = (byte) calibrationImperial;
    sendData(BAROMETRIC_PRESSURE_CALIBRATION, buffer, 2);
  }
  
  //FIXME can't be correct that both calibrate() and getCalibrationMetric() divide by INHG_TO_HPA
  //FIXME getCalibrationMetric() says pascals, calibrate() says hectopascals

  /**
   * @return the present calibration value in pascals. Will be 0 in case no
   *         explicit calibration has been performed.
   */
  public float getCalibrationMetric() {
    getData(BAROMETRIC_PRESSURE_CALIBRATION, buffer, 2);
    //FIXME likely, buffer[1] must be masked with 0xFF
    //or use EndianTools.decodeUShortBE
    int result = ((buffer[0] & 0xff) << 8) + buffer[1];
    return (result / INHG_TO_HPA) * 10;
  }

  /**
   * <b>HiTechnic Barometer, Pressure mode</b><br>
   * Measures the atmospheric pressure of the air.
   * 
   * <p>
   * <b>Size and content of the sample</b><br>
   * The sample contains one element containing the atmospheric pressure (in HectoPascal) of the air.
   */  
  public SensorMode getPressureMode() {
    return getMode(0);
  }

  private class PressureMode implements SensorMode {

    @Override
    public int sampleSize() {
      return 1;
    }

    @Override
    public String getName() {
      return "Pressure";
    }

    @Override
    public void fetchSample(float[] sample, int offset) {
      getData(BAROMETRIC_PRESSURE, buffer, 2);
      //FIXME likely, buffer[1] must be masked with 0xFF
      //or use EndianTools.decodeUShortBE
      sample[0] = ((((buffer[0] & 0xff) << 8) + buffer[1]) / INHG_TO_HPA) * 10;
    }

  }

  /**
   * <b>HiTechnic Barometer, Temperature mode</b><br>
   * Measures the temperature of the air.
   * 
   * <p>
   * <b>Size and content of the sample</b><br>
   * The sample contains one element containing the air temperature (in degree celcius).
   */    
  public SensorMode getTemperatureMode() {
    return getMode(1);
  }

  private class TemperatureMode implements SensorMode {
    @Override
    public int sampleSize() {
      return 1;
    }

    @Override
    public void fetchSample(float[] sample, int offset) {
      getData(BAROMETRIC_TEMPERATURE, buffer, 2);
      //FIXME RobotC driver suite shifts buffer[0] by 8, not 2
      //FIXME RobotC driver suite divides by 10 instead of multiplying by 10
      sample[offset] = ((buffer[0] << 2) | (buffer[1] & 0xFF)) * 10 + 273.15f;
    }

    @Override
    public String getName() {
      return "Temperature";
    }
  }
}