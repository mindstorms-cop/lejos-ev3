package lejos.remote.ev3;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import lejos.hardware.Key;
import lejos.hardware.KeyListener;

public class RemoteRequestKey implements Key {

	public RemoteRequestKey(ObjectInputStream is, ObjectOutputStream os) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isDown() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isUp() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void waitForPress() {
		// TODO Auto-generated method stub

	}

	@Override
	public void waitForPressAndRelease() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addKeyListener(KeyListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void simulateEvent(int event) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
