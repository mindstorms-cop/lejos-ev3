package lejos.remote.ev3;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import lejos.hardware.port.UARTPort;

public class RemoteRequestUARTPort extends RemoteRequestIOPort implements UARTPort  {
	private ObjectInputStream is;
	private ObjectOutputStream os;
	private int portNum;
	
	public RemoteRequestUARTPort(ObjectInputStream is, ObjectOutputStream os) {
		this.is = is;
		this.os = os;
	}
	
	@Override
	public boolean open(int typ, int portNum,
			RemoteRequestPort remoteRequestPort) {
		boolean res = super.open(typ,portNum,remoteRequestPort);
		this.portNum = portNum;
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.OPEN_UART_PORT;;
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
	public byte getByte() {
		System.out.println("Getting byte");
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.UART_GET_BYTE;
		req.replyRequired = true;
		try {
			os.writeObject(req);
			req.intValue = portNum;
			EV3Reply reply = (EV3Reply) is.readObject();
			System.out.println("Byte is " + reply.reply);
			return (byte) reply.reply;
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public void getBytes(byte[] vals, int offset, int len) {
		System.out.println("Getting bytes");
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.UART_GET_BYTES;
		req.replyRequired = true;
		req.intValue = portNum;
		req.intValue2 = len;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
			System.out.println("Got bytes: " + len);
			for(int i=0;i<len;i++) vals[offset+i] = reply.contents[i];
		} catch (Exception e) {
		}
	}

	@Override
	public int getShort() {
		System.out.println("Getting short");
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.UART_GET_SHORT;
		req.replyRequired = true;
		req.intValue = portNum;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
			System.out.println("Got short: " + reply.reply);
			return (short) reply.reply;
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public void getShorts(short[] vals, int offset, int len) {
		System.out.println("Getting shorts");
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.UART_GET_SHORTS;
		req.replyRequired = true;
		req.intValue = portNum;
		req.intValue2 = len;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
			System.out.println("Got shorts: " + len);
			for(int i=0;i<len;i++) vals[offset+i] = reply.shorts[i];
		} catch (Exception e) {
		}	
	}

	@Override
	public String getModeName(int mode) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.UART_GET_MODE_NAME;
		req.replyRequired = true;
		try {
			os.writeObject(req);
			req.intValue = portNum;
			EV3Reply reply = (EV3Reply) is.readObject();
			return reply.name;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public boolean initialiseSensor(int mode) {
		System.out.println("Initialise sensor");
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.UART_GET_BYTES;
		req.replyRequired = true;
		req.intValue = portNum;
		req.intValue2 = mode;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
			return reply.result;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void resetSensor() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.UART_RESET_SENSOR;
		req.intValue = portNum;
		try {
			os.writeObject(req);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean setMode(int mode) {
		System.out.println("Setting mode to " + mode);
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.UART_SET_MODE;
		req.replyRequired = true;
		req.intValue = portNum;
		req.intValue2 = mode;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
			return reply.result;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
