package lejos.internal.ev3;

import lejos.hardware.sensor.EV3SensorConstants;
import lejos.internal.io.NativeDevice;

public class EV3DeviceManager implements EV3SensorConstants
{
    protected static NativeDevice dev;
    
    protected static EV3DeviceManager localDeviceManager = new EV3DeviceManager();
    protected int[] forcedPortType = new int[PORTS];

    /**
     * Return the device manager for the local EV3
     * @return The local device manager singleton.
     */
    public static synchronized EV3DeviceManager getLocalDeviceManager()
    {
        if (dev == null)
        {
            try {
                initDeviceIO();
            } catch (Throwable e)
            {
                throw new UnsupportedOperationException("Unable to access EV3 hardware. Is this an EV3?", e);
            }
        }
        return localDeviceManager;
    }

    /**
     * Get the type classification for the port. If a sensor is attached to the port
     * this will identify the connection type (UART/IIC/Analog)
     * @param port the port to check.
     * @return The type of the port. 
     */
    public int getPortType(int port)
    {
        if (forcedPortType[port] != 0)
            return forcedPortType[port];
        else
            return EV3AnalogPort.getPortType(port);
    }

    /**
     * Force the type of the specified port to be typ. This can be used if automatic sensor detection
     * does not work for a particular hardware device. Setting a type of 0 resumes automatic detection.
     * @param port the port to force
     * @param typ the new typ
     */
    public void forcePortType(int port, int typ)
    {
        forcedPortType[port] = typ;
    }

    /**
     * This function returns information on the sensor that is attached to the
     * specified port. Note that only very basic sensor identification information
     * may be available for some sensor types. It may be necessary to actually open the
     * sensor to allow it to be identified in further detail.
     * @param port the port to check
     * @return the sensor type
     */
    public int getSensorType(int port)
    {
        return EV3AnalogPort.getAnalogSensorType(port);
    }

    /**
     * Set the basic operating mode of the various sensor pins to allow correct
     * operation of the attached sensor.
     * @param port port to set
     * @param mode the pin mode to use.
     */
    public void setPortMode(int port, int mode)
    {
        byte [] modes = new byte[PORTS];
        for(int i = 0; i < modes.length; i++)
            modes[i] = (byte)'-';
        modes[port] = (byte)mode;
        dev.write(modes, modes.length);
        
    }
    
    /**
     * Return a human readable version of the port type.
     * @param typ port type
     * @return string version of the type
     */
    public String getPortTypeName(int typ)
    {
        switch(typ)
        {
        case CONN_INPUT_UART:
            return "UART";
        case CONN_UNKNOWN:
            return "Unknown";
        case CONN_INPUT_DUMB:
            return "Dumb";
        case CONN_NXT_DUMB:
            return "NXT Dumb";
        case CONN_NXT_IIC:
            return "NXT_IIC";
        case CONN_NXT_COLOR:
            return "NXT Color";
        case CONN_NONE:
            return "None";
        default:
            return "Error";
        }
    }
    
    
    private static void initDeviceIO()
    {
        dev = new NativeDevice("/dev/lms_dcm"); 
        // set all ports to be auto detect
        for(int port = 0; port < PORTS; port++)
            localDeviceManager.setPortMode(port,  CMD_AUTOMATIC);
    }
}
