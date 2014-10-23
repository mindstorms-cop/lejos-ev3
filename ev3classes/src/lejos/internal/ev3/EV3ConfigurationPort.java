package lejos.internal.ev3;

import java.io.IOError;

import lejos.hardware.port.ConfigurationPort;
import lejos.hardware.sensor.EV3SensorConstants;
import lejos.internal.io.NativeDevice;
import lejos.utility.Delay;

public class EV3ConfigurationPort extends EV3IOPort  implements ConfigurationPort
{
    protected static NativeDevice dev;
    
    static {
        initDeviceIO();
    }

    /** {@inheritDoc}
     */    
    @Override
    public boolean open(int typ, int port, EV3Port ref)
    {
        if (!super.open(typ, port, ref))
            return false;
        // enable automatic detection on this port
        setPinMode(CMD_AUTOMATIC);
        // allow time for detection to work
        Delay.msDelay(100);
        for(int i = 0; i < 2000; i++)
        {
            if (getPortType() != CONN_NONE) 
            {
                //System.out.println("detected after " + i);
                break;
            }
            Delay.msDelay(1);
        }
        return true;
    }


    /**
     * Get the type classification for the port. If a sensor is attached to the port
     * this will identify the connection type (UART/IIC/Dumb/Output etc.)
     * @return The type of the port. 
     */
    public int getPortType()
    {
        if (typ == EV3Port.MOTOR_PORT)
            return EV3AnalogPort.getMotorPortType(port);
        else
            return EV3AnalogPort.getPortType(port);
    }

    /**
     * This function returns information on the sensor/motor that is attached to the
     * specified port. Note that only very basic sensor identification information
     * may be available for some sensor types. It may be necessary to actually open the
     * sensor to allow it to be identified in further detail.
     * @return the sensor type
     */
    public int getDeviceType()
    {
        if (typ == EV3Port.MOTOR_PORT)
            return EV3AnalogPort.getMotorType(port);
        else
            return EV3AnalogPort.getAnalogSensorType(port);
    }


    /**
     * Set the basic operating mode of the various sensor pins to allow correct
     * operation of the attached sensor.
     * @param port port to set
     * @param mode the pin mode to use.
     */
    public static boolean setPortMode(int typ, int port, int mode)
    {
        //System.out.println("Set port mode " + port + " mode " + mode);
        byte [] modes = new byte[PORTS*(EV3Port.MOTOR_PORT+1)];
        for(int i = 0; i < modes.length; i++)
            modes[i] = (byte)'-';
        modes[port+typ*PORTS] = (byte)mode;
        return dev.write(modes, modes.length) >= 0;
    }
    
   
    private static void initDeviceIO()
    {
        try {
            dev = new NativeDevice("/dev/lms_dcm");
        } catch(IOError e)
        {
            throw new UnsupportedOperationException("Unable to access EV3 hardware. Is this an EV3?", e);
        }
        // set all ports to be disconnected
        for(int typ = 0; typ <= EV3Port.MOTOR_PORT; typ++)
            for(int port = 0; port < PORTS; port++)
            {
                setPortMode(typ, port,  CMD_DISCONNECTED);
                //System.out.println("reset port " + port + " type " + EV3AnalogPort.getPortType(port));
            }
    }
}
