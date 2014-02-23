package lejos.internal.ev3;

import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import lejos.hardware.ev3.LocalEV3;
import lejos.utility.Delay;

public class EV3Key implements Key {	
	private static final int WAITFOR_RELEASE_SHIFT = 8;
	private static final int DEBOUNCE_TIME = 10;
	
	private int iCode;
	private EV3Keys keys;
	
	public EV3Key(EV3Keys keys, int iCode) {
		this.iCode = iCode;
		this.keys = keys;
	}

	@Override
	public int getId() {
		return iCode;
	}

	@Override
	public boolean isDown() {
		return (keys.readButtons() & iCode) != 0;
	}

	@Override
	public boolean isUp() {
		return (keys.readButtons() & iCode) == 0;
	}

	@Override
	public void waitForPress() {
		while ((keys.waitForAnyPress(0) & iCode) == 0)
		{
			// wait for next press
		}
	}

	@Override
	public void waitForPressAndRelease() {
		this.waitForPress();
		int tmp = iCode << WAITFOR_RELEASE_SHIFT;
		while ((keys.waitForAnyEvent(0) & tmp) == 0)
		{
			// wait for next event
		}
	}

	@Override
	public void addKeyListener(KeyListener listener) {
		// TODO Auto-generated method stub
	}

	@Override
	public void simulateEvent(int event) {
		int simulatedState = keys.getSimulatedState();
		if (event == Key.KEY_PRESSED) keys.setSimultedState(simulatedState |= iCode); // set bit
		else if (event == Key.KEY_RELEASED) keys.setSimultedState(simulatedState &= ~iCode); // unset bit
		else if (event == Key.KEY_PRESSED_AND_RELEASED) {
			keys.setSimultedState(simulatedState |= iCode);
			Delay.msDelay(DEBOUNCE_TIME * 4);
			keys.setSimultedState(simulatedState &= ~iCode);
		}
	}
	
	private void checkKeys() {
		if (keys == null) keys = (EV3Keys) LocalEV3.get().getKeys();
	}
}
