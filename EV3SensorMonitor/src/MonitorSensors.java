import static java.lang.System.out;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;

import lejos.hardware.Device;
import lejos.hardware.device.DeviceIdentifier;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.robotics.SampleProvider;

/**
 * MonitorSensors monitors sensors that are plugged in and removed and attempts to identify them.
 * 
 * It then uses reflection to call common sensor methods such as all parameterless get methods and
 * is methods, and fetchSample.
 * 
 * This is useful to see if a sensor is working but not for much else.
 * 
 * It needs to be run on the EV3.
 * 
 * The mapping to sensor classes needs extending for more sensors.
 * 
 * @author Lawrie Griffiths
 *
 */
public class MonitorSensors {
    HashMap<String,String> sensorClasses = new HashMap<String,String>();
      
    // Monitor the sensor ports
    void monitorSensorPorts()
    {
        // TODO: add more sensors.
    	sensorClasses.put("IIC:mndsnsrs/NRLink","lejos.hardware.device.RCXLink");
    	sensorClasses.put("IIC:mndsnsrs/ACCL3X03","lejos.hardware.sensor.MindsensorsAccelerometer");
    	sensorClasses.put("IIC:HiTechnc/Color","lejos.hardware.sensor.ColorHTSensor");
    	sensorClasses.put("IIC:HiTechnc/IRLink","lejos.hardware.device.IRLink");
    	sensorClasses.put("IIC:HiTechnc/Compass","lejos.hardware.sensor.HiTechnicCompass");
    	sensorClasses.put("NXT_COLOR:NXT_Color","lejos.hardware.sensor.ColorSensor");
    	// Use LightSensor class for NXT dumb sensors
    	sensorClasses.put("NXT_ANALOG:NXT_LIGHT","lejos.hardware.sensor.LightSensor");
    	// Use TouchSensor for EV3 dumb sensors
    	sensorClasses.put("EV3_ANALOG:EV3_TOUCH","lejos.hardware.sensor.EV3TouchSensor");
    	sensorClasses.put("IIC:LEGO/Sonar","lejos.hardware.sensor.NXTUltrasonicSensor");
    	sensorClasses.put("UART:IR-PROX","lejos.hardware.sensor.EV3IRSensor");
    	sensorClasses.put("UART:COL-REFLECT","lejos.hardware.sensor.EV3ColorSensor");

        Port[] port = {SensorPort.S1, SensorPort.S2, SensorPort.S3, SensorPort.S4};
        String [] currentSig = new String[port.length];
        int [] currentTyp = new int[port.length];
        DeviceIdentifier ids[] = new DeviceIdentifier[port.length];
        for(int i = 0; i < port.length; i++)
        {
            currentSig[i] = "";
            currentTyp[i] = 0;
            ids[i] = new DeviceIdentifier(port[i]);
        }
        while(true) {
            // Look for changes
            for(int i = 0; i < port.length; i++) {
                int typ = ids[i].getPortType();
                if (typ != currentTyp[i])
                {
                    currentTyp[i] = typ;
                    String sig = ids[i].getDeviceSignature(false);
                    if (!currentSig[i].equals(sig)) {
                        out.println("Port " + i + " changed to " + sig);
                        currentSig[i] = sig;
                        ids[i].close();
                        String className = sensorClasses.get(sig);
                        out.println("Sensor class for " + sig + " is " + className);
                        callGetMethods(className, Port.class, port[i]);
                        ids[i] = new DeviceIdentifier(port[i]);
                    }
                }
            }
        }
    }
    
    // Construct an instance of the class with a single parameter, and call its parameterless get and is methods
    private void callGetMethods(String className, Class<?> paramClass, Object param) {
    	if (className != null) {
        	Class<?> c;
        	
        	try {
				c = Class.forName(className);
				Class<?>[] params = new Class<?>[1];
				params[0] = paramClass;
				Constructor<?> con = c.getConstructor(params);
				Object[] args = new Object[1];
				args[0] = param;
				Object o = con.newInstance(args);
				out.println("Calling get methods for " + className);
				callGetMethods(c, o);
				((Device)o).close();
			} catch (Exception e) {
				e.printStackTrace();
			} 
        }
    }
    
    // Call the parameterless get and is methods of the instance of the class
    void callGetMethods(Class<?> c, Object o) {
    	int sampleSize = 0;
		try {	    
		    Method[] allMethods = c.getDeclaredMethods();
		    for (Method m : allMethods) {
		        if (!m.getName().startsWith("get") && !m.getName().startsWith("is") && !m.getName().startsWith("sample")) continue;
		        Class<?>[] pType  = m.getParameterTypes();
		        if (pType.length > 0) continue;

		        if (o != null) {
			        out.println("Invoking " + m.toGenericString());
		        	Object res = m.invoke(o, (Object[]) null);
		        	if (res.getClass().isArray()) {
		        		for(int i=0;i<Array.getLength(res);i++) {
		        			out.println("Element " + i + " is " + Array.get(res, i));
		        		}
		        	} else {
		        		out.println("Result is " + res);
		        		if (m.getName().startsWith("sample")) {
		        			sampleSize = (int) (Integer) res;
		        		} else if (m.getName().endsWith("Mode")) {
		        	        // Fetch a sample
		        	        if (res instanceof SampleProvider) {
		        	        	sampleSize =((SampleProvider) res).sampleSize();
		        	        	float[] sample = new float[sampleSize];
		        	        	((SampleProvider) res).fetchSample(sample, 0);
		        	        	for(int i=0;i<sampleSize;i++) System.out.println("sample[" + i + "] is " + sample[i]);
		        	        }
		        			
		        		}
		        				
		        	}
		        }
	        }
		} catch (Exception e) {
		    e.printStackTrace();
		}
    }

    public static void main(String[] args) {
    	new MonitorSensors().monitorSensorPorts();
    }
}
