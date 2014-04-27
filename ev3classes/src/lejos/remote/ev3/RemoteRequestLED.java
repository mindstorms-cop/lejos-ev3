package lejos.remote.ev3;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import lejos.hardware.LED;

public class RemoteRequestLED implements LED {
	private ObjectOutputStream os;
	
	public RemoteRequestLED(ObjectInputStream is, ObjectOutputStream os) {
		this.os = os;
	}

	@Override
	public void setPattern(int pattern) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.LED_PATTERN;
		req.intValue = pattern;
		try {
			os.writeObject(req);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
