import java.io.IOException;

import lejos.remote.ev3.RemoteRequestEV3;
import lejos.remote.ev3.RemoteRequestPilot;
import lejos.utility.Delay;


public class RemoteRequest4 {

	public static void main(String[] args) throws IOException {
		RemoteRequestEV3 ev3 = new RemoteRequestEV3("192.168.0.9");
		
		System.out.println("Connected");
		
		RemoteRequestPilot pilot = (RemoteRequestPilot) ev3.createPilot(3.5,  20.0, "A", "B");
		
		pilot.travel(10);
		pilot.rotate(90);
		
		pilot.close();
		
		Delay.msDelay(2000);

	}

}
