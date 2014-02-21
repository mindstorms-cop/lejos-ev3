package lejos.remote.ev3;

import java.rmi.RemoteException;

import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import lejos.hardware.port.PortException;

public class RemoteKey implements Key {
	private RMIKey key;
	
	public RemoteKey(RMIKey key) {
		this.key=key;
	}

	@Override
	public int getId() {
		try {
			return key.getId();
		} catch (RemoteException e) {
			throw new PortException(e);
		}
	}

	@Override
	public boolean isDown() {
		try {
			return key.isDown();
		} catch (RemoteException e) {
			throw new PortException(e);
		}
	}

	@Override
	public boolean isUp() {
		try {
			return key.isUp();
		} catch (RemoteException e) {
			throw new PortException(e);
		}
	}

	@Override
	public void waitForPress() {
		try {
			key.waitForPress();
		} catch (RemoteException e) {
			throw new PortException(e);
		}		
	}

	@Override
	public void waitForPressAndRelease() {
		try {
			key.waitForPressAndRelease();
		} catch (RemoteException e) {
			throw new PortException(e);
		}
	}

	@Override
	public void addKeyListener(KeyListener listener) {
		try {
			key.addKeyListener(listener);
		} catch (RemoteException e) {
			throw new PortException(e);
		}
	}

	@Override
	public void simulateEvent(int event) {
		try {
			key.simulateEvent(event);
		} catch (RemoteException e) {
			throw new PortException(e);
		}
	}
}
