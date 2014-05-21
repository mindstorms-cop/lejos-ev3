package org.lejos.android.sample.ev3androidtilt;

import java.io.IOException;

import lejos.remote.ev3.RemoteRequestEV3;
import lejos.remote.ev3.RemoteRequestPilot;
import lejos.utility.Delay;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

public class MainActivity extends Activity implements SensorEventListener {
	private static final float 
			ZERO_X = 0f, ZERO_Y = 7.5f, LEEWAY = 0.5f, 
			SPEED_FACTOR = 10f, TURN_FACTOR = 20f;
	
	private SensorManager sensorManager;
	private float x = 0, y = 0, z = 0;
	private TextView xAccel, yAccel, zAccel;
	private RemoteRequestPilot pilot;
	private RemoteRequestEV3 ev3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		xAccel = (TextView) findViewById(R.id.xAccel);
		yAccel = (TextView) findViewById(R.id.yAccel);
		zAccel = (TextView) findViewById(R.id.zAccel);
		new Control().execute("192.168.0.9");
	}
	
	 @Override 
	 protected void onResume() {
		 sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
		 super.onResume();
	 }
	
	 @Override
	 protected void onStop() {
	  sensorManager.unregisterListener(this);
	  super.onStop();
	 }

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Do nothing
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		x = event.values[0];
		y = event.values[1];
		z = event.values[2];
		
		xAccel.setText("x = " + x);
		yAccel.setText("y = " + y);
		zAccel.setText("z = " + z);
	}
	
	private class Control extends AsyncTask<String, Integer, Long> {
		protected Long doInBackground(String... cmd) {
			try {
				ev3 = new RemoteRequestEV3(cmd[0]);
				pilot = (RemoteRequestPilot) ev3.createPilot(3.5f, 20f, "A", "B");
				
				for(;;) {
					pilot.setTravelSpeed(Math.abs(y - ZERO_Y) * SPEED_FACTOR);
					if (y > ZERO_Y + LEEWAY) {
						pilot.backward();
					}
					else if (y < ZERO_Y - LEEWAY) {
						if (Math.abs(x - ZERO_X) < LEEWAY) pilot.forward();
						else pilot.steer((x - ZERO_X) * TURN_FACTOR);
					}
					else pilot.stop();
					Delay.msDelay(200);
				}
			} catch (Exception e) {
				return (ev3 == null ? 1l : 2l);
			} finally {
				if (pilot != null) {
					pilot.stop();
					pilot.close();
				}
				if (ev3 != null) ev3.disConnect();
			}
		}

		protected void onPostExecute(Long result) {
			if (result == 1l)
				Toast.makeText(MainActivity.this, "Could not connect to EV3",
						Toast.LENGTH_LONG).show();
			else if (result == 2l)
				Toast.makeText(MainActivity.this, "Error accessing EV3",
						Toast.LENGTH_LONG).show();
		}
	}
}
