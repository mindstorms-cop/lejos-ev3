package org.lejos.android.sample.ev3androidrotate;

import java.io.IOException;

import org.lejos.android.sample.ev3androidrotate.RotationControl.RotationListener;

import lejos.remote.ev3.RemoteRequestEV3;
import lejos.remote.ev3.RemoteRequestPilot;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnTouchListener,
		RotationListener, OnSeekBarChangeListener {
	private static final String HOST = "192.168.0.9";
	private RemoteRequestEV3 ev3;
	private RemoteRequestPilot pilot;
	private int angle;
	private ImageButton forward, backward;
	private SeekBar speed, rotationSpeed;
	private CheckBox adjust;
	private TextView speedValue, rotationSpeedValue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		forward = (ImageButton) findViewById(R.id.forward);
		forward.setOnTouchListener(this);

		backward = (ImageButton) findViewById(R.id.backward);
		backward.setOnTouchListener(this);
		
		adjust = (CheckBox) findViewById(R.id.adjust);
		
		speed = (SeekBar) findViewById(R.id.speed);
		speed.setOnSeekBarChangeListener(this);	
		speedValue = (TextView) findViewById(R.id.speedValue);
		speed.setProgress(50);
		
		rotationSpeed = (SeekBar) findViewById(R.id.rotateSpeed);
		rotationSpeed.setOnSeekBarChangeListener(this);	
		rotationSpeedValue = (TextView) findViewById(R.id.rotateSpeedValue);
		rotationSpeed.setProgress(50);

		RotationControl rotateView = (RotationControl) findViewById(R.id.rotate);
		rotateView.setRotationListener(this);

		// Connect to the EV3
		new Control().execute("connect", HOST);
	}

	@Override
	public void onAngleChanged(int angle) {
		if (!adjust.isChecked()) new Control().execute("rotate", "" + angle);
		else this.angle = angle;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction();
		if (action == MotionEvent.ACTION_UP)
			new Control().execute("stop");
		else if (action == MotionEvent.ACTION_DOWN) {
			if (v == forward)
				new Control().execute("forward");
			else if (v == backward)
				new Control().execute("backward");
		}
		return false;
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (seekBar == speed) speedValue.setText("Travel speed: " + progress);
		else if (seekBar == rotationSpeed) rotationSpeedValue.setText("Rotation speed: " + progress);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {	
	}

	private class Control extends AsyncTask<String, Integer, Long> {
		protected Long doInBackground(String... cmd) {
			if (cmd[0].equals("connect")) {
				try {
					ev3 = new RemoteRequestEV3(cmd[1]);
					pilot = (RemoteRequestPilot) ev3.createPilot(3.5f, 20f,
							"A", "B");
					speed.setMax((int) pilot.getMaxTravelSpeed());
					rotationSpeed.setMax((int) pilot.getRotateMaxSpeed());
				} catch (IOException e) {
					return 1l;
				}
			} else if (cmd[0].equals("rotate")) {
				if (ev3 == null)
					return 2l;
				int newAngle = Integer.parseInt(cmd[1]);
				pilot.setRotateSpeed(rotationSpeed.getProgress());
				pilot.rotate(angle - newAngle);
				angle = newAngle;
			} else if (cmd[0].equals("forward")) {
				if (ev3 == null)
					return 2l;
				pilot.setTravelSpeed(speed.getProgress());
				pilot.forward();
			} else if (cmd[0].equals("backward")) {
				if (ev3 == null)
					return 2l;
				pilot.setTravelSpeed(speed.getProgress());
				pilot.backward();
			} else if (cmd[0].equals("stop")) {
				if (ev3 == null)
					return 2l;
				pilot.stop();
			} else if (cmd[0].equals("close")) {
				if (ev3 == null)
					return 2l;
				pilot.close();
				ev3.disConnect();
			}
			return 0l;
		}

		protected void onPostExecute(Long result) {
			if (result == 1l)
				Toast.makeText(MainActivity.this, "Could not connect to EV3",
						Toast.LENGTH_LONG).show();
			else if (result == 2l)
				Toast.makeText(MainActivity.this, "Not connected",
						Toast.LENGTH_LONG).show();
		}
	}
}
