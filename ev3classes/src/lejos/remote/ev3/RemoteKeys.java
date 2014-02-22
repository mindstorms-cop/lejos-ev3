package lejos.remote.ev3;

import java.rmi.RemoteException;

import lejos.hardware.Keys;
import lejos.hardware.port.PortException;

public class RemoteKeys implements Keys {
	private RMIKeys keys;
	
	public RemoteKeys(RMIKeys keys) {
		this.keys=keys;
	}
	
	@Override
	public void discardEvents() {
		try {
			keys.discardEvents();
		} catch (RemoteException e) {
			throw new PortException(e);
		}
	}

	@Override
	public int waitForAnyEvent() {
		try {
			return keys.waitForAnyEvent();
		} catch (RemoteException e) {
			throw new PortException(e);
		}
	}

	@Override
	public int waitForAnyEvent(int timeout) {
		try {
			return keys.waitForAnyEvent(timeout);
		} catch (RemoteException e) {
			throw new PortException(e);
		}
	}

	@Override
	public int waitForAnyPress(int timeout) {
		try {
			return keys.waitForAnyPress(timeout);
		} catch (RemoteException e) {
			throw new PortException(e);
		}
	}

	@Override
	public int waitForAnyPress() {
		try {
			return keys.waitForAnyPress();
		} catch (RemoteException e) {
			throw new PortException(e);
		}
	}

	@Override
	public int getButtons() {
		try {
			return keys.getButtons();
		} catch (RemoteException e) {
			throw new PortException(e);
		}
	}

	@Override
	public int readButtons() {
		try {
			return keys.readButtons();
		} catch (RemoteException e) {
			throw new PortException(e);
		}
	}

	@Override
	public void setKeyClickVolume(int vol) {
		try {
			keys.setKeyClickVolume(vol);
		} catch (RemoteException e) {
			throw new PortException(e);
		}
	}

	@Override
	public int getKeyClickVolume() {
		try {
			return keys.getKeyClickVolume();
		} catch (RemoteException e) {
			throw new PortException(e);
		}
	}

	@Override
	public void setKeyClickLength(int len) {
		try {
			keys.setKeyClickLength(len);
		} catch (RemoteException e) {
			throw new PortException(e);
		}	
	}

	@Override
	public int getKeyClickLength() {
		try {
			return keys.getKeyClickLength();
		} catch (RemoteException e) {
			throw new PortException(e);
		}
	}

	@Override
	public void setKeyClickTone(int key, int freq) {
		try {
			keys.setKeyClickTone(key, freq);
		} catch (RemoteException e) {
			throw new PortException(e);
		}
	}

	@Override
	public int getKeyClickTone(int key) {
		try {
			return keys.getKeyClickTone(key);
		} catch (RemoteException e) {
			throw new PortException(e);
		}
	}
}
