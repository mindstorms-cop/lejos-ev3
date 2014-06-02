import lejos.hardware.brickpi.BrickPi;
import lejos.hardware.brickpi.LocalBrickPi;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.MindsensorsAccelerometer;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class S5Test {
	
	public static void main(String[] args) {
		BrickPi pi = LocalBrickPi.get();	
		Port s5 = pi.getPort("S5");
		
		MindsensorsAccelerometer accel = new MindsensorsAccelerometer(s5);
		
		System.out.println("Vendor: " + accel.getVendorID());
		System.out.println("Sensor: " + accel.getProductID());
		System.out.println("Version: " + accel.getVersion());
		SampleProvider sp = accel.getAccelerationMode();
		
		float[] sample = new float[sp.sampleSize()];
		
		for(int i=0;i<10;i++) {
			sp.fetchSample(sample, 0);
			System.out.println("X: " + sample[0] + " Y: " + sample[1] + " Z: " + sample[2]);
			Delay.msDelay(500);
		}
		
		accel.close();
	}
}
