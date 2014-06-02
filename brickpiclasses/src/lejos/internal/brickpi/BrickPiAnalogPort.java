package lejos.internal.brickpi;

import java.io.IOException;

import com.ergotech.brickpi.sensors.ColorSensorBlue;
import com.ergotech.brickpi.sensors.ColorSensorFull;
import com.ergotech.brickpi.sensors.ColorSensorGreen;
import com.ergotech.brickpi.sensors.ColorSensorNone;
import com.ergotech.brickpi.sensors.ColorSensorRed;
import com.ergotech.brickpi.sensors.LightSensorOff;
import com.ergotech.brickpi.sensors.LightSensorOn;
import com.ergotech.brickpi.sensors.RawSensor;
import com.ergotech.brickpi.sensors.TouchSensor;

import lejos.hardware.port.AnalogPort;

public class BrickPiAnalogPort extends BrickPiIOPort implements AnalogPort {
	private int type;

    @Override
    public boolean open(int typ, int port, BrickPiPort ref)
    {
        if (!super.open(typ, port, ref))
            return false;
        return true;
    }

	@Override
	public float getPin6() {
		System.out.println("getPin6");
		return 0;
	}

	@Override
	public float getPin1() {
		int val = sensor.getValue();
		System.out.println("Pin 1 value is " + val);
		return (type == TYPE_SWITCH ? (1 - val) * 5.0f : 5.0f * (val / 1023f));
	}

	@Override
	public void getFloats(float[] vals, int offset, int length) {
		System.out.println("getFloats: " + sensor.getValue());
		if (type == TYPE_COLORFULL) vals[4] = sensor.getValue();
		else if (type == TYPE_COLORNONE) vals[3] = sensor.getValue();
		else if (type == TYPE_COLORRED) vals[0] = sensor.getValue();
		else if (type == TYPE_COLORGREEN) vals[1] = sensor.getValue();
		else if (type == TYPE_COLORBLUE) vals[2] = sensor.getValue();
	}
	
    // The following methods provide compatibility with NXT sensors
    
    @Override
    public boolean setType(int type)  {
    	this.type = type;
    	sensor = new RawSensor();
        switch(type) {
        case TYPE_NO_SENSOR:
        	break;
        case TYPE_SWITCH:
            sensor = new TouchSensor();
            break;
        case TYPE_TEMPERATURE:
        case TYPE_CUSTOM:
        case TYPE_ANGLE:
            break;
        case TYPE_LIGHT_ACTIVE:
        case TYPE_SOUND_DBA:            
        case TYPE_REFLECTION:
            sensor = new LightSensorOn();
            break;
        case TYPE_LIGHT_INACTIVE:
        case TYPE_SOUND_DB: 
        	sensor = new LightSensorOff();
            break;
        case TYPE_LOWSPEED:
            break;
        case TYPE_LOWSPEED_9V:
            break;
        case TYPE_COLORFULL:
        	sensor = new ColorSensorFull();
        	break;
        case TYPE_COLORRED:
        	sensor = new ColorSensorRed();
        	break;
        case TYPE_COLORGREEN:
        	sensor = new ColorSensorGreen();
        	break;
        case TYPE_COLORBLUE:
        	sensor = new ColorSensorBlue();
        	break;
        case TYPE_COLORNONE:
            sensor = new ColorSensorNone();
            break;

        default:
            return false;
        }
        
        if (sensor != null) {
        	com.ergotech.brickpi.BrickPi.getBrickPi().setSensor(sensor, port);
        	try {
    			com.ergotech.brickpi.BrickPi.getBrickPi().setupSensors();
    		} catch (IOException e) {
    			e.printStackTrace();
    			return false;
    		}
        }
        
        return true;
    }

}
