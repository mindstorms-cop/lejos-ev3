package lejos.hardware.sensor;

import lejos.hardware.port.AnalogPort;
import lejos.hardware.port.Port;
import lejos.utility.Delay;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * <p>This class represents a Dexter Industries Laser Sensor. The sensor contains a laser and a photodiode to read
 * ambient light values. This sensor can be calibrated to low and high values.</p>
 * 
 *  <p>The Dexter Industries laser can turn on and off very rapidly, with the following characteristics:</p>
 *  <li>it takes about 8-10 ms to turn on and reach full power
 *  <li>it takes about 5 ms to turn off
 * 
 */
public class DexterLaserSensor extends AnalogSensor implements SensorConstants, SensorMode
{
    protected static final long SWITCH_DELAY = 10;
	private boolean laser = false;

	private class Laser implements SensorMode
	{

        @Override
        public int sampleSize()
        {
            return 1;
        }

        @Override
        public void fetchSample(float[] sample, int offset)
        {
            setLaser(true);
            sample[offset] = 1.0f - normalize(port.getPin1());
        }

        @Override
        public String getName()
        {
            // TODO Auto-generated method stub
            return "Laser";
        }
	    
	}
    /**
     * Create a laser sensor object attached to the specified port,
     * and sets the laser on or off.
     * @param port an already open analog port
     */
    public DexterLaserSensor(AnalogPort port)
    {
        super(port);
        setLaser(laser);
    }
    
	/**
	 * Create a laser sensor object attached to the specified port,
	 * and sets the laser on or off.
	 * @param port port, e.g. Port.S1
	 */
	public DexterLaserSensor(Port port)
	{
	   super(port);
       setLaser(laser);
	}
	
	public void setLaser(boolean laserState)
	{
	    switchType(laserState ? TYPE_LIGHT_ACTIVE : TYPE_LIGHT_INACTIVE, SWITCH_DELAY);
		this.laser = laserState;
	}

	/**
	 * Get a sample provider that returns samples with the laser turned off.
	 * @return
	 */
	public SensorMode getAmbientMode()
	{
	    return this;
	}

	/**
	 * Get a sample provider that returns samples with the laser turned on.
	 * @return
	 */
	public SensorMode getLaserMode()
	{
	    return new Laser();
	}
	
    @Override
    public int sampleSize()
    {
        return 1;
    }

    @Override
    public void fetchSample(float[] sample, int offset)
    {
        setLaser(false);
        sample[offset] = 1.0f - normalize(port.getPin1());
    }

    @Override
    public String getName()
    {
        return "Ambient";
    }
}
