import lejos.hardware.sensor.MindsensorsBTSense;
import lejos.hardware.sensor.SensorMode;
import lejos.utility.Delay;


public class BTSenseTest {
	public static void main(String[] args) throws Exception {
		MindsensorsBTSense btSense = new MindsensorsBTSense();
		
		SensorMode dateMode = btSense.getSensorMode('D');
		System.out.println("Created data mode");
		SensorMode accelMode = btSense.getSensorMode('A');
		System.out.println("Created acceleration mode");
		
		float[] sample = new float[dateMode.sampleSize()];
		
		for(int i=0;i<10;i++) {
			dateMode.fetchSample(sample,0);
			
			StringBuilder sb = new StringBuilder();
			sb.append("Date sample is ");
			
			for(int j=0;j<dateMode.sampleSize();j++) {
				sb.append(((int) sample[j]) + " ");
			}
			System.out.println(sb.toString());
			
			Delay.msDelay(500);
		}
		
		sample = new float[accelMode.sampleSize()];
		
		for(int i=0;i<50;i++) {
			accelMode.fetchSample(sample,0);
			
			StringBuilder sb = new StringBuilder();
			sb.append("Acceleration sample is ");
			
			for(int j=0;j<accelMode.sampleSize();j++) {
				sb.append(((int) sample[j]) + " ");
			}
			System.out.println(sb.toString());
			
			Delay.msDelay(500);
		} 
	}
}
