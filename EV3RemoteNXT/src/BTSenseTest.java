import lejos.hardware.sensor.MindsensorsBTSense;
import lejos.hardware.sensor.SensorMode;


public class BTSenseTest {
	public static void main(String[] args) throws Exception {
		MindsensorsBTSense btSense = new MindsensorsBTSense();
		
		SensorMode dateMode = btSense.getSensorMode('D');
		SensorMode accelMode = btSense.getSensorMode('A');
		
		Thread.sleep(5000);
		
		float[] sample = new float[dateMode.sampleSize()];
		
		for(int i=0;i<10;i++) {
			dateMode.fetchSample(sample,0);
			System.out.print("Sample is ");
			
			for(int j=0;j<dateMode.sampleSize();j++) {
				System.out.print(((int) sample[j]) + " ");
			}
			System.out.println();
		}
		
		sample = new float[accelMode.sampleSize()];
		
		for(int i=0;i<50;i++) {
			accelMode.fetchSample(sample,0);
			System.out.print("Sample is ");
			
			for(int j=0;j<accelMode.sampleSize();j++) {
				System.out.print(((int) sample[j]) + " ");
			}
			System.out.println();
		} 
	}
}
