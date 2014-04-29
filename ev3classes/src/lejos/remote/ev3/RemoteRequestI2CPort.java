package lejos.remote.ev3;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import lejos.hardware.port.I2CPort;

public class RemoteRequestI2CPort extends RemoteRequestIOPort implements I2CPort {
	private ObjectInputStream is;
	private ObjectOutputStream os;
	private int portNum;
	
	public RemoteRequestI2CPort(ObjectInputStream is, ObjectOutputStream os) {
		this.is = is;
		this.os = os;
	}
	
	@Override
	public boolean open(int typ, int portNum,
			RemoteRequestPort remoteRequestPort) {
		boolean res = super.open(typ,portNum,remoteRequestPort);
		this.portNum = portNum;
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.OPEN_I2C_PORT;
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
	public void i2cTransaction(int deviceAddress, byte[] writeBuf,
			int writeOffset, int writeLen, byte[] readBuf, int readOffset,
			int readLen) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.SYSTEM_SOUND;
		req.replyRequired = true;
		req.intValue = portNum;
		req.intValue2 = deviceAddress;
		req.intValue3 = writeOffset;
		req.intValue4 = writeLen;
		req.intValue5 = readLen;
		req.byteData = writeBuf;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
			for(int i=0;i<readLen;i++) readBuf[readOffset+i] = reply.contents[i];
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
}
