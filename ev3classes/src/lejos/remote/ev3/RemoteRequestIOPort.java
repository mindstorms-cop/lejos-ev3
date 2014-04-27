package lejos.remote.ev3;

import lejos.hardware.port.BasicSensorPort;
import lejos.hardware.port.IOPort;
import lejos.hardware.sensor.EV3SensorConstants;

public class RemoteRequestIOPort implements IOPort, BasicSensorPort, EV3SensorConstants {

	public boolean open(int typ, int portNum,
			RemoteRequestPort remoteRequestPort) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getMode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	@Deprecated
	public int getType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean setMode(int mode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	@Deprecated
	public boolean setType(int type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	@Deprecated
	public boolean setTypeAndMode(int type, int mode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPinMode(int mode) {
		// TODO Auto-generated method stub
		
	}

}
