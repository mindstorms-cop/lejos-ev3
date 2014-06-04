package lejos.hardware.port;

/**
 * Interface that provides a binding between a physical port and the different
 * types of sensor interfaces that can be used with it
 * @author andy
 *
 */
public interface Port
{
    /**
     * return the string name used to reference this physical port
     * @return a string representation of the port
     */
    public String getName();

    /**
     * Obtain access to a class that can be used to talk to the port hardware
     * @param portclass the required port interface
     * @return a class that implements the requested interface
     */
    public <T extends IOPort> T open(Class<T> portclass);
    
    /**
     * Get the type classification for the port. If a sensor is attached to the port
     * this will identify the connection type (UART/IIC/Analog, etc.). Note that not all
     * platforms may be able to support this method.
     * @return The type of the port. 
     */
    public int getPortType();
    
    /**
     * This function returns information on the sensor that is attached to the
     * specified port. Note that only very basic sensor identification information
     * may be available for some sensor types. It may be necessary to actually open the
     * sensor to allow it to be identified in further detail.
     * @return the sensor type
     */
    public int getSensorType();
    
    /**
     * Force the type of the specified port to be typ. This can be used if automatic sensor detection
     * does not work for a particular hardware device. Setting a type of 0 resumes automatic detection.
     * @param typ the new typ
     */
    public void forcePortType(int typ);



}
