package lejos.hardware.device;

import lejos.hardware.Device;
import lejos.hardware.port.AnalogPort;
import lejos.hardware.port.ConfigurationPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.UARTPort;
import lejos.hardware.sensor.EV3SensorConstants;
import lejos.hardware.sensor.I2CSensor;

public class DeviceIdentifier extends Device implements EV3SensorConstants
{
    Port port;
    ConfigurationPort configPort;
    
    /**
     * Create an instance of the device identifier for a particular port
     * @param port The port to operate with
     */
    public DeviceIdentifier(Port port)
    {
        this.port = port;
        configPort = port.open(ConfigurationPort.class);
    }
    
    public void close()
    {
        if (configPort != null)
            configPort.close();
        super.close();            
    }

    /**
     * Get the type classification for the port. If a sensor is attached to the port
     * this will identify the connection type (CONN_NONE, CONN_INPUT_UART etc.). See 
     * the class EV3SensorConstants for the actual values
     * @return The type of the port
     * 
     */
    public int getPortType()
    {
        return configPort.getPortType();
    }

    /**
     * Thismethod returns information on the sensor/motor that is attached to the
     * specified port. Note that only very basic sensor identification information
     * may be available for some sensor types. It may be necessary to actually open the
     * sensor to allow it to be identified in further detail.
     * @return the sensor type
     */
    public int getDeviceType()
    {
        return configPort.getDeviceType();
    }
    
    /**
     * Returns the signature for a dumb NXT sensor
     * @return string identifying the device
     */
    protected String getNXTDumbSignature()
    {
        switch(getDeviceType())
        {
        case TYPE_NXT_TOUCH:
            return "NXT_TOUCH";
        case TYPE_NXT_LIGHT:
            return "NXT_LIGHT";
        case TYPE_NXT_SOUND:
            return "NXT_SOUND";
        default:
            return "UNKNOWN";
        }
    }

    /**
     * Returns the signature for a i2c sensor
     * @return string identifying the device
     */
    protected String getI2CSignature()
    {
        configPort.close();
        configPort = null;
        String product = "";
        String vendor = "";
        String version = "";
        String address = "2";
        I2CSensor i2c = null;
        try {
            // we need to try and read the device identification strings
            i2c = new I2CSensor(port);
            // search for the device on available addresses
            for(int i = 2; i < 255; i+= 2)
            {
                i2c.setAddress(i);
                product = i2c.getProductID();
                if (product.length() != 0)
                {
                    address = String.valueOf(i);
                    break;
                }
            }
            vendor = i2c.getVendorID();
            version = i2c.getVersion();
        }
        catch (Exception e)
        {
            // ignore any exceptions during detection.
        }
        finally
        {
            if (i2c != null)
                i2c.close();
            configPort = port.open(ConfigurationPort.class);
        }
        if (product.length() == 0)
            product = "unknown";
        if (vendor.length() == 0)
            vendor = "unknown";
        if (version.length() == 0)
            version = "unknown";
        return address + "-" + vendor + "-" + product + "-" + version;
    }
    
    /**
     * Returns the signature for a dumb EV3 sensor
     * @return string identifying the device
     */
    protected String getEV3DumbSignature()
    {
        // need to look at analog value on pin 1 to identify
        configPort.close();
        configPort = null;
        String product = "";
        AnalogPort ap = null;
        try {
            ap = port.open(AnalogPort.class);
            float p1Val = ap.getPin1();
            System.out.println("Pin 1 voltage is " + p1Val);
            product = "EV3TOUCH";
        }
        catch (Exception e)
        {
            // ignore any exceptions during detection.
        }
        finally
        {
            if (ap != null)
                ap.close();
            configPort = port.open(ConfigurationPort.class);
        }
        if (product.length() == 0)
            product = "unknown"; 
        return product;
    }
    
    /**
     * Returns the signature for a UART sensor
     * @return string identifying the device
     */
    protected String getUARTSignature()
    {
        configPort.close();
        configPort = null;
        String product = "";
        UARTPort uart = null;
        try {
            // we need to try and read the device identification strings
            uart = port.open(UARTPort.class);
            uart.setMode(0);
            product = uart.getModeName(0);
        }
        catch (Exception e)
        {
            // ignore any exceptions during detection.
        }
        finally
        {
            if (uart != null)
                uart.close();
            configPort = port.open(ConfigurationPort.class);
        }
        if (product.length() == 0)
            product = "unknown";
        return product;        
    }
    
    /**
     * Returns the signature for a motor/output device
     * @return string identifying the device
     */
    protected String getMotorSignature()
    {
        switch(getDeviceType())
        {
        case TYPE_TACHO:
            return "TACHO";
        case TYPE_MINITACHO:
            return "MINITACHO";
        case TYPE_NEWTACHO:
            return "NEWTACHO";
        default:
            return "UNKNOWN";
        }
    }
    
    
    /**
     * return the signature of the attached device. This signature can be used to identify
     * the actual device. Note that identification may require that the device is opened.
     * @return a string signature
     */
    public String getDeviceSignature()
    {
        int portType = getPortType();
        switch(portType)
        {
        case CONN_NONE:
            return "NONE::NONE";
        case CONN_ERROR:
        case CONN_UNKNOWN:
            return "UNKNOWN::UNKNOWN";
        case CONN_NXT_COLOR:
            return "NXT_COLOR::NXT_COLOR";
        case CONN_NXT_DUMB:
            return "NXT_ANALOG::" + getNXTDumbSignature();
        case CONN_NXT_IIC:
            return "IIC::" + getI2CSignature();
        case CONN_INPUT_DUMB:
            return "EV3_ANALOG::" + getEV3DumbSignature();
        case CONN_INPUT_UART:
            return "UART::" + getUARTSignature();
        case CONN_OUTPUT_DUMB:
            // TODO: Does anyone have anything that is recognised as this?
            return "OUTPUT_DUMB::UNKNOWN";
        case CONN_OUTPUT_INTELLIGENT:
            // TODO: Same for this type
            return "OUTPUT_INTELLIGENT::UNKNOWN";
        case CONN_OUTPUT_TACHO:
            return "OUTPUT_TACHO::" + getMotorSignature();
        default:
            return "UNKNOWN::UNKNOWN";
        }
    }
    
}