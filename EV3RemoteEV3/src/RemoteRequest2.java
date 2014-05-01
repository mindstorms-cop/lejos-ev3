import java.io.IOException;

import lejos.remote.ev3.RemoteRequestEV3;
import lejos.remote.ev3.RemoteRequestSampleProvider;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;

public class RemoteRequest2 {
	
	public static void main(String[] args) throws IOException {
		RemoteRequestEV3 ev3 = new RemoteRequestEV3("192.168.0.9");
		
		RegulatedMotor left = ev3.createRegulatedMotor("A", 'L');
		RegulatedMotor right = ev3.createRegulatedMotor("B", 'L');
		
		left.rotate(1800,true);
		right.rotate(1800);
		
		left.rotate(560,true);
		right.rotate(-560);
		
		Delay.msDelay(5000);
		
		left.close();
		right.close();
		
		RemoteRequestSampleProvider sp = (RemoteRequestSampleProvider) ev3.createSampleProvider("S1", "lejos.hardware.sensor.EV3IRSensor", "Distance");
		
		int sampleSize = sp.sampleSize();
		
		System.out.println("Sample size is " + sampleSize);
		
		float[] sample = new float[sampleSize];
		
		sp.fetchSample(sample, 0);
		
		System.out.println("Sample is " + sample[0]);
		
		sp.close();
		
		Delay.msDelay(20000);
		
	}
}
