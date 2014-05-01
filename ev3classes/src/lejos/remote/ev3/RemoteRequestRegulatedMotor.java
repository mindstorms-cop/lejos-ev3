package lejos.remote.ev3;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import lejos.hardware.DeviceException;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.RegulatedMotorListener;

public class RemoteRequestRegulatedMotor implements RegulatedMotor {
	private ObjectInputStream is;
	private ObjectOutputStream os;
	private String portName;
	private int portNum;

	public RemoteRequestRegulatedMotor(ObjectInputStream is,
			ObjectOutputStream os, String portName, char motorType) {
		this.is = is;
		this.os = os;
		portNum = portName.charAt(0) - 'A';
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.CREATE_REGULATED_MOTOR;
		req.intValue = portNum;
		req.str = portName;
		req.ch = motorType;
		try {
			os.writeObject(req);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void forward() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.MOTOR_FORWARD;
		req.intValue = portNum;
		try {
			os.writeObject(req);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void backward() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.MOTOR_BACKWARD;
		req.intValue = portNum;
		try {
			os.writeObject(req);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	@Override
	public void stop() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.MOTOR_STOP;
		req.replyRequired = true;
		req.intValue = portNum;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void flt() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.MOTOR_FLT;
		req.replyRequired = true;
		req.intValue = portNum;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isMoving() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.MOTOR_IS_MOVING;
		req.replyRequired = true;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
			return reply.result;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public int getRotationSpeed() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.MOTOR_GET_ROTATION_SPEED;
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
	public int getTachoCount() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.MOTOR_GET_TACHO_COUNT;
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
		req.request = EV3Request.Request.MOTOR_RESET_TACHO_COUNT;;
		req.intValue = portNum;
		try {
			os.writeObject(req);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addListener(RegulatedMotorListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public RegulatedMotorListener removeListener() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void stop(boolean immediateReturn) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.MOTOR_STOP_IMMEDIATE;
		req.replyRequired = true;
		req.intValue = portNum;
		req.flag = immediateReturn;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void flt(boolean immediateReturn) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.MOTOR_FLT_IMMEDIATE;
		req.replyRequired = true;
		req.intValue = portNum;
		req.flag = immediateReturn;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void waitComplete() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.MOTOR_WAIT_COMPLETE;
		req.replyRequired = true;
		req.intValue = portNum;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	@Override
	public void rotate(int angle, boolean immediateReturn) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.MOTOR_ROTATE_IMMEDIATE;
		req.replyRequired = !immediateReturn;
		req.intValue = portNum;
		req.intValue2 = angle;
		req.flag = immediateReturn;
		try {
			os.writeObject(req);
			if (!immediateReturn) {
				EV3Reply reply = (EV3Reply) is.readObject();
				checkException(reply.e, req);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	@Override
	public void rotate(int angle) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.MOTOR_ROTATE;
		req.replyRequired = true;
		req.intValue = portNum;
		req.intValue2 = angle;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
			checkException(reply.e, req);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	@Override
	public void rotateTo(int limitAngle) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.MOTOR_ROTATE_TO;
		req.replyRequired = true;
		req.intValue = portNum;
		req.intValue2 = limitAngle;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void rotateTo(int limitAngle, boolean immediateReturn) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.MOTOR_ROTATE_TO_IMMEDIATE;
		req.replyRequired = !immediateReturn;
		req.intValue = portNum;
		req.intValue2 = limitAngle;
		req.flag = immediateReturn;
		try {
			os.writeObject(req);
			if (!immediateReturn) {
				EV3Reply reply = (EV3Reply) is.readObject();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	@Override
	public int getLimitAngle() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.MOTOR_GET_LIMIT_ANGLE;
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
	public void setSpeed(int speed) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.MOTOR_SET_SPEED;
		req.intValue = portNum;
		req.intValue2 = speed;
		try {
			os.writeObject(req);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getSpeed() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.MOTOR_GET_SPEED;
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
	public float getMaxSpeed() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.MOTOR_GET_MAX_SPEED;
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
	public boolean isStalled() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.MOTOR_IS_STALLED;
		req.replyRequired = true;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
			return reply.result;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void setStallThreshold(int error, int time) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.MOTOR_SET_STALL_THRESHOLD;
		req.intValue = portNum;
		req.intValue2 = error;
		req.intValue3 = time;
		try {
			os.writeObject(req);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setAcceleration(int acceleration) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.MOTOR_SET_ACCELERATION;
		req.intValue = portNum;
		req.intValue2 = acceleration;
		try {
			os.writeObject(req);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.MOTOR_CLOSE;
		req.replyRequired = true;
		req.intValue = portNum;
		try {
			os.writeObject(req);
			EV3Reply reply = (EV3Reply) is.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void checkException(Exception e, EV3Request req) {
		if (e != null) throw new DeviceException("Exception in remote request : " + req.request, e);
	}
}
