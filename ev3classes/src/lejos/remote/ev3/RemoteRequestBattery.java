package lejos.remote.ev3;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import lejos.hardware.Power;

public class RemoteRequestBattery implements Power {
	private ObjectInputStream is;
	private ObjectOutputStream os;
	
	public RemoteRequestBattery(ObjectInputStream is, ObjectOutputStream os) {
		this.is = is;
		this.os = os;
	}

	@Override
	public int getVoltageMilliVolt() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.GET_VOLTAGE_MILLIVOLTS;
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
	public float getVoltage() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.GET_VOLTAGE;
		req.replyRequired = true;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
			return reply.floatReply;
		} catch (Exception e) {
			e.printStackTrace();
			return 0f;
		}
	}

	@Override
	public float getBatteryCurrent() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.GET_BATTERY_CURRENT;
		req.replyRequired = true;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
			return reply.floatReply;
		} catch (Exception e) {
			return 0f;
		}
	}

	@Override
	public float getMotorCurrent() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.GET_MOTOR_CURRENT;
		req.replyRequired = true;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
			return reply.floatReply;
		} catch (Exception e) {
			return 0f;
		}
	}
}
