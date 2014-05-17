import java.io.IOException;

import lejos.remote.ev3.RemoteRequestEV3;
import lejos.remote.ev3.RemoteRequestSampleProvider;


public class RemoteRequest5 {

	public static void main(String[] args) throws IOException {
		RemoteRequestEV3 ev3 = new RemoteRequestEV3("192.168.0.9");
		
		System.out.println("Connected");
		RemoteRequestSampleProvider sp = (RemoteRequestSampleProvider) ev3.createSampleProvider("S1", "lejos.hardware.sensor.EV3IRSensor", "Distance");
		
		float[] sample = new float[sp.sampleSize()];
		
		for(int i=0;i<30;i++) {
			sp.fetchSample(sample, 0);
		}
		
		sp.close();

	}

}
