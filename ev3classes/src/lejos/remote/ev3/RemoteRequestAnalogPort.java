package lejos.remote.ev3;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import lejos.hardware.port.AnalogPort;

public class RemoteRequestAnalogPort extends RemoteRequestIOPort implements AnalogPort {
	private ObjectInputStream is;
	private ObjectOutputStream os;
	private int portNum;
	
	public RemoteRequestAnalogPort(ObjectInputStream is, ObjectOutputStream os) {
		this.is = is;
		this.os = os;
	}
	
	@Override
	public boolean open(int typ, int portNum,
			RemoteRequestPort remoteRequestPort) {
		boolean res = super.open(typ,portNum,remoteRequestPort);
		this.portNum = portNum;
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.OPEN_ANALOG_PORT;;
		req.intValue = typ;
		req.intValue2 = portNum;
		try {
			os.writeObject(req);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	@Override
	public void close() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.CLOSE_SENSOR_PORT;
		req.intValue = portNum;
		try {
			os.writeObject(req);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
    @Override
    public boolean setType(int type)
    {
        switch(type)
        {
        case TYPE_NO_SENSOR:
        case TYPE_SWITCH:
        case TYPE_TEMPERATURE:
        case TYPE_CUSTOM:
        case TYPE_ANGLE:
            setPinMode(CMD_FLOAT);
            break;
        case TYPE_LIGHT_ACTIVE:
        case TYPE_SOUND_DBA:            
        case TYPE_REFLECTION:
            setPinMode(CMD_SET|CMD_PIN5);
            break;
        case TYPE_LIGHT_INACTIVE:
        case TYPE_SOUND_DB: 
            setPinMode(CMD_SET);
            break;
        case TYPE_LOWSPEED:
            setPinMode(CMD_SET);
            break;
        case TYPE_LOWSPEED_9V:
            setPinMode(CMD_SET|CMD_PIN1);
            break;
        default:
            throw new UnsupportedOperationException("Unrecognised sensor type");
        }
        return true;
    }
    
	@Override
	public void setPinMode(int mode) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.SET_PIN_MODE;
		req.intValue = portNum;
		req.intValue2 = mode;
		try {
			os.writeObject(req);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
 
	@Override
	public float getPin6() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.GET_PIN_6;
		req.replyRequired = true;
		req.intValue = portNum;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
			return reply.floatReply;
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public float getPin1() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.GET_PIN_1;
		req.replyRequired = true;
		req.intValue = portNum;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
			return reply.floatReply;
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public void getFloats(float[] vals, int offset, int length) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.GET_PIN_6;
		req.replyRequired = true;
		req.intValue = portNum;
		req.intValue2 = length;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
			for(int i=0;i<vals.length;i++) vals[offset+i] = reply.floats[i];
		} catch (Exception e) {
		}	
	}
}
