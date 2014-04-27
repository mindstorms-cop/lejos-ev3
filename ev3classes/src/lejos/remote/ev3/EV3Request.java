package lejos.remote.ev3;

import java.io.File;
import java.io.Serializable;

public class EV3Request implements Serializable {
	private static final long serialVersionUID = 3277625894143478775L;

	public enum Request {
		GET_VOLTAGE_MILLIVOLTS,
		GET_VOLTAGE,
		GET_BATTERY_CURRENT,
		GET_MOTOR_CURRENT,
		SYSTEM_SOUND,
		PLAY_TONE_VOL,
		PLAY_TONE,
		PLAY_SAMPLE_VOL,
		PLAY_SAMPLE,
		PLAY_SAMPLE_DATA,
		PLAY_NOTE,
		SET_VOLUME,
		GET_VOLUME,
		GET_NAME,
		LED_PATTERN,
		DISCARD_EVENTS,
		WAIT_FOR_ANY_EVENT,
		WAIT_FOR_ANY_PRESS,
		GET_BUTTONS,
		READ_BUTTONS
		
	}
	
	public Request request;
	
	public boolean replyRequired;
	
	public int intValue, intValue2, intValue3;
	
	public File file;
	
	public byte[] byteData;
	
	public int[] intData;

}
