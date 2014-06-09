package lejos.internal.brickpi;

import lejos.hardware.port.UARTPort;

public class BrickPiUARTPort extends BrickPiIOPort implements UARTPort {

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
	public byte getByte() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void getBytes(byte[] vals, int offset, int len) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getShort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void getShorts(short[] vals, int offset, int len) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getModeName(int mode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean initialiseSensor(int mode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void resetSensor() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int rawRead(byte[] buffer, int offset, int len) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int rawWrite(byte[] buffer, int offset, int len) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setBitRate(int bitRate) {
		// TODO Auto-generated method stub
		
	}

}
