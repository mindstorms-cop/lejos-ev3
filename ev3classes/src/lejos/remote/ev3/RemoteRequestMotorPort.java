package lejos.remote.ev3;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import lejos.hardware.motor.MotorRegulator;
import lejos.hardware.port.TachoMotorPort;

public class RemoteRequestMotorPort extends RemoteRequestIOPort implements TachoMotorPort {
	private ObjectInputStream is;
	private ObjectOutputStream os;
	private int portNum;

	public RemoteRequestMotorPort(ObjectInputStream is, ObjectOutputStream os) {
		this.is = is;
		this.os = os;
	}
	
	@Override
	public boolean open(int typ, int portNum,
			RemoteRequestPort remoteRequestPort) {
		boolean res = super.open(typ,portNum,remoteRequestPort);
		this.portNum = portNum;
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.OPEN_MOTOR_PORT;
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
		req.request = EV3Request.Request.CLOSE_MOTOR_PORT;
		req.intValue = portNum;
		try {
			os.writeObject(req);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	@Override
	public void controlMotor(int power, int mode) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.CONTROL_MOTOR;
		req.intValue = portNum;
		req.intValue2 = power;
		req.intValue3 = mode;
		try {
			os.writeObject(req);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setPWMMode(int mode) {
		// Not implemented
	}

	@Override
	public int getTachoCount() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.GET_TACHO_COUNT;
		req.replyRequired = true;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
			return reply.reply;
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public void resetTachoCount() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.RESET_TACHO_COUNT;
		try {
			os.writeObject(req);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public MotorRegulator getRegulator() {
		throw(new UnsupportedOperationException("Remote regulators are not supported"));
	}
}
