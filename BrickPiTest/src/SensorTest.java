import lejos.hardware.brickpi.BrickPi;
import lejos.hardware.brickpi.LocalBrickPi;
import lejos.hardware.port.AnalogPort;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.AnalogSensor;
import lejos.hardware.sensor.HiTechnicGyro;
import lejos.hardware.sensor.NXTColorSensor;
import lejos.hardware.sensor.NXTLightSensor;
import lejos.hardware.sensor.NXTTouchSensor;
import lejos.hardware.sensor.NXTUltrasonicSensor;
import lejos.robotics.Color;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class SensorTest {
	private static final String[] colors = {"None", "Red", "Green", "Blue","Yellow", "Magenta", "Orange", "White", "Black"};
	
	public static void main(String[] args) {
		BrickPi pi = LocalBrickPi.get();	
		Port s1 = pi.getPort("S1");
		Port s2 = pi.getPort("S2");
		Port s3 = pi.getPort("S3");
		Port s4 = pi.getPort("S4");
		
		//NXTLightSensor sensor1 = new NXTLightSensor(s1);
		NXTColorSensor sensor1 = new NXTColorSensor(s1);
		//NXTTouchSensor sensor2 = new NXTTouchSensor(s2);
		NXTUltrasonicSensor sensor2 = new NXTUltrasonicSensor(s2);
		HiTechnicGyro sensor3 = new HiTechnicGyro(s3);
		AnalogPort p4 = s4.open(AnalogPort.class);
		p4.setType(AnalogSensor.TYPE_CUSTOM);
		
		SampleProvider sp1 = sensor1.getRGBMode();
		SampleProvider sp2 = sensor2.getContinuousMode();
		SampleProvider sp3 = sensor3.getMode(0);

		float[] sample1 = new float[sp1.sampleSize()];
		float[] sample2 = new float[sp2.sampleSize()];
		float[] sample3 = new float[sp3.sampleSize()];
		
		for(int i=0;i<100;i++) {
			sp1.fetchSample(sample1, 0);
			sp2.fetchSample(sample2, 0);
			sp3.fetchSample(sample3, 0);
			//System.out.println("Value 1: " + colors[(int) sample1[0]+1]);
			System.out.println("Value 1: " + sample1[0] + "," + sample1[1]);
			//System.out.println("Value 2: " + sample2[0]);
			//System.out.println("Value 3: " + sample3[0]);
			//System.out.println("Value 4: " + p4.getPin1());
			Delay.msDelay(500);
		}
		
		sensor1.close();
		sensor2.close();
		sensor3.close();
		p4.close();
	}
}
